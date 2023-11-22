package com.example.recycle_app;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityObjectDetect extends AppCompatActivity {

    private Uri imgUri;
    private File dir=null,imgFile=null;
    private int deltaY,deltaX,dot_size;
    private ArrayList<DetectedObject> detectedObj = new ArrayList<>();
    private ArrayList<RelativeLayout> added_views = new ArrayList<>();
    private FloatingActionButton focused_dot = null;
    private boolean registerScroll=true;
    private HorizontalScrollView items_detected;
    private RelativeLayout img_container,tooltip;
    private ImageView img_camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detect);

        ObjectDetectorOptions options =
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()  // Optional
                        .build();

        ObjectDetector objectDetector = ObjectDetection.getClient(options);


        ShimmerFrameLayout shimmerFrameLayout1 = findViewById(R.id.shimmer1);
        ShimmerFrameLayout shimmerFrameLayout2 = findViewById(R.id.shimmer2);
        ShimmerFrameLayout shimmerFrameLayout3 = findViewById(R.id.shimmer3);
        img_camera = findViewById(R.id.img_camera);
        ImageView btn_camera = findViewById(R.id.btn_camera);
        ImageView btn_close = findViewById(R.id.close_btn);
        RelativeLayout loading = findViewById(R.id.loading);
        tooltip = findViewById(R.id.tooltip);
        img_container = findViewById(R.id.img_container);
        items_detected = findViewById(R.id.items_detected);


        ActivityResultLauncher<Uri> contract = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        // updating imgName
                        if(result)
                        {
                            for (RelativeLayout dot:added_views) {
                                img_container.removeView(dot);
                            }
                            items_detected.setOnScrollChangeListener(null);

                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            img_camera.setImageBitmap(bitmap);
                            img_camera.setFocusable(false);
                            img_camera.setClickable(false);
                            shimmerFrameLayout1.setBaseAlpha(0.5f);
                            shimmerFrameLayout2.setBaseAlpha(0.5f);
                            shimmerFrameLayout3.setBaseAlpha(0.5f);
                            shimmerFrameLayout1.startShimmerAnimation();
                            shimmerFrameLayout2.startShimmerAnimation();
                            shimmerFrameLayout3.startShimmerAnimation();
                            loading.setVisibility(View.VISIBLE);

                            btn_camera.setClickable(false);
                            btn_camera.setAlpha(0.5f);

                            new CountDownTimer(5000, 1000) {

                                public void onTick(long millisUntilFinished) {}

                                public void onFinish() {

                                    btn_camera.setClickable(true);
                                    btn_camera.setAlpha(1f);

                                    InputImage image = InputImage.fromBitmap(bitmap, 0);

                                    obj_detect(image, objectDetector, loading);
                                }
                            }.start();

                        }
                    }
                });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    imgUri = createImageURI();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (camera_intent.resolveActivity(getPackageManager()) != null)
                    contract.launch(imgUri);
                else
                    Toast.makeText(ActivityObjectDetect.this, "A problem occurred while opening camera", Toast.LENGTH_SHORT).show();
            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_camera.callOnClick();
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void removeCards(int num) {
        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        if(num<container.getChildCount()) {

            num = container.getChildCount() - num;

            for (int i = container.getChildCount() - 1; i >= 0; i--) {
                if (num > 0)
                    container.removeViewAt(i);
                else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getChildAt(i).getLayoutParams();
                    params.setMarginEnd((int) (params.getMarginEnd() + 25 * getApplicationContext().getResources().getDisplayMetrics().density));
                    break;
                }
                num--;
            }
        }

    }

    private void addCards(int num)
    {
        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        if(num>container.getChildCount()){

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getChildAt(container.getChildCount()-1).getLayoutParams();
            params.setMarginEnd(0);
            container.getChildAt(container.getChildCount()-1).setLayoutParams(params);

            num = num - container.getChildCount();

            int margin_start;
            while(num>0) {
                RelativeLayout card = (RelativeLayout) View.inflate(ActivityObjectDetect.this, R.layout.inflate_card_detected_obj, null);
                container.addView(card);

                params = (RelativeLayout.LayoutParams) card.getLayoutParams();

                margin_start = (int) (((250 + 10) * (container.getChildCount()-1) + 15) * getApplicationContext().getResources().getDisplayMetrics().density);
                params.setMarginStart(margin_start);

                if(num==1)
                    params.setMarginEnd((int) (25 * getApplicationContext().getResources().getDisplayMetrics().density));

                card.setLayoutParams(params);
                for(int i=0;i<added_views.size();i++)
                {
                    if(i>added_views.size()-num-1)
                    {
                        linkCard_Dot(added_views.get(i).findViewById(R.id.dot),card);
                        break;
                    }
                }
                num--;
            }

        }
    }

    private Uri createImageURI() throws IOException {

        //make a separate dir with name as itemName in files
        String filepath = "/ObjDetect_Images";
        dir = new File(getFilesDir(), filepath);
        dir.mkdirs();

        imgFile = new File(getFilesDir()+ filepath,"image.png");
        imgFile.createNewFile();

        return FileProvider.getUriForFile(getApplicationContext(),"com.example.recycle_app.fileProvider", imgFile);
    }



    private RelativeLayout createDot(RelativeLayout img_container,ImageView imageView,int offset,HorizontalScrollView items_detected)
    {

        RelativeLayout dot = (RelativeLayout) View.inflate(ActivityObjectDetect.this, R.layout.inflate_dot, null);

        Animation pulse = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_pulse);
        pulse.setStartOffset(offset);
        dot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dot.startAnimation(pulse);

        img_container.addView(dot);
        added_views.add(dot);


        if (offset==0)// if 1st dot
        {
            FloatingActionButton btn_dot = dot.findViewById(R.id.dot);
            ViewGroup.LayoutParams layoutParams = btn_dot.getLayoutParams();
            dot_size = layoutParams.width;
            layoutParams.width += 40;
            layoutParams.height += 40;
            btn_dot.setLayoutParams(layoutParams);

            focused_dot = btn_dot;
        }

        int container_width = img_container.getWidth();
        int container_height = img_container.getHeight();
        int img_width = calculate_actualWidth_Height(imageView,"width");
        int img_height = calculate_actualWidth_Height(imageView,"height");

        deltaX = (container_width - img_width) / 2;
        deltaY = (container_height - img_height) / 2;


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
        params.setMargins(0,deltaY,0,0);
        dot.setLayoutParams(params);

        //link items cards and dots

        View v = getCardView(offset/40,items_detected);

        if(v!=null)
            linkCard_Dot(dot.findViewById(R.id.dot),v);

        if(offset==0)
            dot.findViewById(R.id.dot).callOnClick();

        dot.findViewById(R.id.touch_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dot.findViewById(R.id.dot).callOnClick();
                setTooltip("Off");
            }
        });

        return dot;
    }

    private void linkCard_Dot(FloatingActionButton dot,View card)
    {
        card.setTag(dot);
        dot.setTag(dot);

        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(card!=null) {
                    registerScroll = false;
                    changeSize(dot);
//                    items_detected.smoothScrollTo((int) (v.getLeft()-25 * getApplicationContext().getResources().getDisplayMetrics().density),0);
                    ObjectAnimator anim = ObjectAnimator.ofInt(items_detected, "scrollX",  (int) (card.getLeft()-25 * getApplicationContext().getResources().getDisplayMetrics().density)).setDuration(500);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animator) {}

                        @Override
                        public void onAnimationEnd(@NonNull Animator animator) {
                            registerScroll = true;
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animator) {}

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animator) {}
                    });
                    anim.start();
                }
            }
        });
    }

    private void changeDotMargins(Rect boundingBox,RelativeLayout dot,ImageView imageView) {

        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
        int margin_left = Math.round(boundingBox.centerX() * scaleX) + deltaX;
        int margin_top = Math.round(boundingBox.centerY() * scaleY) + deltaY;

        params.setMargins(margin_left,margin_top,0,0);
        dot.setLayoutParams(params);

    }

    private void changeSize(FloatingActionButton clicked_dot)
    {
        if(clicked_dot!=null) {
            ViewGroup.LayoutParams params1 = clicked_dot.getLayoutParams();
            ViewGroup.LayoutParams params2 = focused_dot.getLayoutParams();

            if (focused_dot != clicked_dot && params1.width == dot_size) {

                params1.width += 40;
                params1.height += 40;

                params2.width = dot_size;
                params2.height = dot_size;

                clicked_dot.setLayoutParams(params1);
                focused_dot.setLayoutParams(params2);
                focused_dot = clicked_dot;
            }
        }
    }


    private int calculate_actualWidth_Height(ImageView imageView,String dimen) {
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        if(dimen.equals("width"))
            return actW;
        else
            return actH;

    }
    private void obj_detect(InputImage image,ObjectDetector objectDetector,RelativeLayout loading){

        objectDetector.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<DetectedObject>>() {
                            @Override
                            public void onSuccess(List<DetectedObject> detectedObjects) {
                                loading.setVisibility(View.GONE);

                                detectedObj = (ArrayList<DetectedObject>) detectedObjects;


                                int offset = 0;

                                for (DetectedObject detectedObject : detectedObjects) {
                                    Rect boundingBox = detectedObject.getBoundingBox();
                                    Integer trackingId = detectedObject.getTrackingId();

                                    for (DetectedObject.Label label : detectedObject.getLabels()) {
                                        String text = label.getText();
                                        Log.e("Tag",text);
                                    }

                                    RelativeLayout dot = createDot(img_container,img_camera,offset,items_detected);
                                    changeDotMargins(boundingBox, dot,img_camera);

                                    offset += 40;
                                }
                                setTooltip("On");

                                if(detectedObj.size()>0) {

                                    removeCards(detectedObj.size());
                                    addCards(detectedObj.size());

                                    RelativeLayout rl = (RelativeLayout) items_detected.getChildAt(0);
                                    items_detected.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                                        @Override
                                        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                                            if(registerScroll) {
                                                View v;
                                                for (int j = 0; j < rl.getChildCount(); j++) {
                                                    v = rl.getChildAt(j);
                                                    if (isViewVisible(v, items_detected)) {
                                                        v = img_container.findViewWithTag(v.getTag());
                                                        if(v!=null)
                                                            changeSize((FloatingActionButton) v);
                                                    }
                                                }
                                            }
                                        }
                                    });

                                }

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Tag","Faliure");
                            }
                        });
    }

    private View getCardView(int num,HorizontalScrollView scrollView){

        return ((RelativeLayout) scrollView.getChildAt(0)).getChildAt(num);
    }

    private void setTooltip(String mode)
    {
        if(mode.equals("On")) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tooltip.getLayoutParams();
            params.setMargins(0,0,0,deltaY + 40);

            tooltip.setVisibility(View.VISIBLE);
            tooltip.setLayoutParams(params);
            tooltip.setAlpha(0f);
            tooltip.animate().alpha(1f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {}

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    new CountDownTimer(4000,1000){

                        @Override
                        public void onTick(long l) {}

                        @Override
                        public void onFinish() {
                            tooltip.animate().alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(@NonNull Animator animator) {}

                                @Override
                                public void onAnimationEnd(@NonNull Animator animator) {
                                    tooltip.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(@NonNull Animator animator) {}

                                @Override
                                public void onAnimationRepeat(@NonNull Animator animator) {}
                            });
                        }
                    }.start();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {

                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });

        }
        else {

            tooltip.animate().alpha(0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {}

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    tooltip.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {}

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {}
            });
        }
    }

    private boolean isViewVisible(View view,HorizontalScrollView scrollView) {
        Rect scrollBounds = new Rect();
        scrollView.getDrawingRect(scrollBounds);

        float start = view.getX();
        float end = start + view.getWidth();

        if (scrollBounds.left < start && scrollBounds.right > end) {
            return true;
        } else {
            return false;
        }
    }

    private Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

}

