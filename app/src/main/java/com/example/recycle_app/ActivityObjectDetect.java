package com.example.recycle_app;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.recycle_app.Fragments.Fragment_AddItemToBin;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.example.recycle_app.Server.MultipartServer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class ActivityObjectDetect extends AppCompatActivity {

    private Uri imgUri;
    private File dir=null,imgFile=null;
    private int deltaY,deltaX,dot_size;
    private ArrayList<RelativeLayout> added_views = new ArrayList<>();
    private FloatingActionButton focused_dot = null;
    private boolean registerScroll=true;
    private HorizontalScrollView items_detected;
    private RelativeLayout img_container,tooltip;
    private ImageView img_camera;
    private String json_response;
    private boolean flag_changed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detect);


        img_camera = findViewById(R.id.img_camera);
        ImageView btn_camera = findViewById(R.id.btn_camera);
        ImageView btn_close = findViewById(R.id.close_btn);
        RelativeLayout loading = findViewById(R.id.loading);
        tooltip = findViewById(R.id.tooltip);
        img_container = findViewById(R.id.img_container);
        items_detected = findViewById(R.id.items_detected);



        ActivityResultLauncher<Uri> image_contract = registerForActivityResult(
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
                            added_views.clear();
                            items_detected.setOnScrollChangeListener(null);

                            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            if(bitmap.getWidth() > bitmap.getHeight())
                                Log.e("Tag","Landscape");

                            img_camera.setImageBitmap(bitmap);
                            img_camera.setFocusable(false);
                            img_camera.setClickable(false);
                            start_stop_shimmer("Start");
                            loading.setVisibility(View.VISIBLE);

                            btn_camera.setClickable(false);
                            btn_camera.setAlpha(0.5f);

                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            Handler handler = new Handler(Looper.getMainLooper());

                            executor.execute(new Runnable() {

                                @Override
                                public void run() {

                                    boolean result;
                                    result = obj_detect(btn_camera, loading);

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            btn_camera.setClickable(true);
                                            btn_camera.setAlpha(1f);
                                            loading.setVisibility(View.GONE);
                                            start_stop_shimmer("Stop");

                                            if (!result) {
                                                Toast.makeText(ActivityObjectDetect.this, "Oops! Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                            } else {
                                                try {
                                                    int response = interpret_response();

                                                    if(response == 0)
                                                    {
                                                        //ToDo later
                                                    }
                                                    else if(response==1){
                                                        fill_cards_with_info();
                                                    }

                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                            executor.shutdownNow();
                                        }
                                    });
                                }
                            });
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
                    image_contract.launch(imgUri);
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

        setOnBackPressFunctionality();
        btn_close.setOnClickListener(view -> {
            onBackPressed();
        });



    }

    private void setOnBackPressFunctionality()
    {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void start_stop_shimmer(String mode) {

        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        ShimmerFrameLayout shimmerFrameLayout1 = findViewById(R.id.shimmer1);
        ShimmerFrameLayout shimmerFrameLayout2 = findViewById(R.id.shimmer2);
        ShimmerFrameLayout shimmerFrameLayout3 = findViewById(R.id.shimmer3);

        int num = 3;


        if(shimmerFrameLayout1!=null) {
            shimmerFrameLayout1.setBaseAlpha(0.5f);

            ImageView img_view = shimmerFrameLayout1.findViewById(R.id.image);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) img_view.getLayoutParams();
            if (flag_changed) {
                layoutParams.width += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.height += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.setMargins(0, 0, 0, 0);
            }

            img_view.setImageDrawable(null);
            img_view.setBackgroundColor(Color.parseColor("#ECECEC"));

            if (mode.equals("Start")) {
                shimmerFrameLayout1.findViewById(R.id.text_view1).setVisibility(View.INVISIBLE);
                shimmerFrameLayout1.findViewById(R.id.container_tag).setVisibility(View.INVISIBLE);
                shimmerFrameLayout1.findViewById(R.id.view1).setVisibility(View.VISIBLE);
                shimmerFrameLayout1.findViewById(R.id.view2).setVisibility(View.VISIBLE);

                shimmerFrameLayout1.startShimmerAnimation();
            } else {// Stop
                shimmerFrameLayout1.stopShimmerAnimation();
            }

        }else
            num--; // card no 0 was removed

        if(shimmerFrameLayout2!=null) {
            shimmerFrameLayout2.setBaseAlpha(0.5f);

            ImageView img_view = shimmerFrameLayout2.findViewById(R.id.image);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) img_view.getLayoutParams();
            if (flag_changed) {
                layoutParams.width += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.height += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.setMargins(0, 0, 0, 0);

            }

            img_view.setImageDrawable(null);
            img_view.setBackgroundColor(Color.parseColor("#ECECEC"));

            if(mode.equals("Start")) {
                shimmerFrameLayout2.findViewById(R.id.text_view1).setVisibility(View.INVISIBLE);
                shimmerFrameLayout2.findViewById(R.id.container_tag).setVisibility(View.INVISIBLE);
                shimmerFrameLayout2.findViewById(R.id.view1).setVisibility(View.VISIBLE);
                shimmerFrameLayout2.findViewById(R.id.view2).setVisibility(View.VISIBLE);

                shimmerFrameLayout2.startShimmerAnimation();
            }
            else
                shimmerFrameLayout2.stopShimmerAnimation();
        }else
            num--; // card no 1 was removed

        if(shimmerFrameLayout3!=null) {
            shimmerFrameLayout3.setBaseAlpha(0.5f);

            ImageView img_view = shimmerFrameLayout3.findViewById(R.id.image);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) img_view.getLayoutParams();
            if (flag_changed) {
                layoutParams.width += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.height += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                layoutParams.setMargins(0, 0, 0, 0);
            }

            img_view.setImageDrawable(null);
            img_view.setBackgroundColor(Color.parseColor("#ECECEC"));

            if(mode.equals("Start")) {
                shimmerFrameLayout3.findViewById(R.id.text_view1).setVisibility(View.INVISIBLE);
                shimmerFrameLayout3.findViewById(R.id.container_tag).setVisibility(View.INVISIBLE);
                shimmerFrameLayout3.findViewById(R.id.view1).setVisibility(View.VISIBLE);
                shimmerFrameLayout3.findViewById(R.id.view2).setVisibility(View.VISIBLE);

                shimmerFrameLayout3.startShimmerAnimation();
            }
            else
                shimmerFrameLayout3.stopShimmerAnimation();
        }
        else
            num--; // card no 2 was removed


        for (int i = num ;i<container.getChildCount(); i++) {

                View card = container.getChildAt(i);
                ShimmerFrameLayout shimmerFrameLayout = card.findViewById(R.id.shimmer);
                shimmerFrameLayout.setBaseAlpha(0.5f);

                ImageView img_view = shimmerFrameLayout.findViewById(R.id.image);
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) img_view.getLayoutParams();
                if (flag_changed) {
                    layoutParams.width += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                    layoutParams.height += (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
                    layoutParams.setMargins(0, 0, 0, 0);
                }

                img_view.setImageDrawable(null);
                img_view.setBackgroundColor(Color.parseColor("#ECECEC"));

                if(mode.equals("Start")) {
                    shimmerFrameLayout.findViewById(R.id.text_view1).setVisibility(View.INVISIBLE);
                    shimmerFrameLayout.findViewById(R.id.container_tag).setVisibility(View.INVISIBLE);
                    shimmerFrameLayout.findViewById(R.id.view1).setVisibility(View.VISIBLE);
                    shimmerFrameLayout.findViewById(R.id.view2).setVisibility(View.VISIBLE);

                    shimmerFrameLayout.startShimmerAnimation();
                }
                else
                    shimmerFrameLayout.stopShimmerAnimation();
        }
        flag_changed = false;
    }

    private void removeCards(int num) {//only remove cards if necessary
        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        if(num<container.getChildCount()) { // condition for being necessary

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

    private void addCards(int num) //only add cards if necessary
    {
        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        if(num>container.getChildCount()){// condition for being necessary

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



    private RelativeLayout createDot(RelativeLayout img_container,ImageView imageView,int offset, String obj_name,String category,Rect box)
    {

        RelativeLayout dot = (RelativeLayout) View.inflate(ActivityObjectDetect.this, R.layout.inflate_dot, null);

        Animation pulse = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoom_pulse);
        pulse.setStartOffset(offset);
        dot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dot.startAnimation(pulse);
        dot.findViewById(R.id.dot).setTag(R.id.obj_name,obj_name);
        dot.findViewById(R.id.dot).setTag(R.id.category,category);
        dot.findViewById(R.id.dot).setTag(R.id.b_box,box);

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
                setTooltip("Off",null);
            }
        });

        return dot;
    }

    private void linkCard_Dot(FloatingActionButton dot,View card)
    {
        card.setTag(dot);
        dot.setTag(dot);

        setCardOnClickLogic(card, (String) dot.getTag(R.id.obj_name), (String) dot.getTag(R.id.category),(Rect) dot.getTag(R.id.b_box));

        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(card!=null && registerScroll) {
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
        int margin_left = Math.round((boundingBox.centerX() - 140) * scaleX);
        int margin_top = Math.round((boundingBox.centerY() + deltaY) * scaleY);


        params.setMargins(margin_left,margin_top,0,0);
        dot.setLayoutParams(params);

    }

    private void changeSize(FloatingActionButton clicked_dot)
    {
        if(clicked_dot!=null ) {
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

    void setCardOnClickLogic(View v,String itemName,String category,Rect b_box)
    {
        Fragment addItem = new Fragment_AddItemToBin();
        Bitmap imageBitmap = getImageForItem(b_box);

        Bundle bundle = new Bundle();
        bundle.putParcelable("image",imageBitmap);
        bundle.putString("itemName",itemName);
        bundle.putString("itemCategory",category);
        addItem.setArguments(bundle);

        v.setOnClickListener(card->{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_AddItem_Container,addItem);
            transaction.commit();
        });

    }

    Bitmap getImageForItem(Rect b_box){
        Bitmap og_bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        Bitmap cropped_bitmap = Bitmap.createBitmap(og_bitmap,b_box.left,b_box.top,b_box.width(),b_box.height());

        return cropped_bitmap;
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
    private boolean obj_detect(ImageView btn_camera,RelativeLayout loading) {

//        String url = "http://192.168.0.131:5000/predict";
        String url = "https://brezserver.pythonanywhere.com";

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("file", imgFile.getAbsolutePath()));

        try {
            json_response = MultipartServer.postData(new URL(url), nameValuePairs);
        } catch (IOException io) {
            Log.e("Tag", io.toString());
            return false;
        }

        return true;
    }

    private int interpret_response() throws JSONException {

//        Log.e("Tag",json_response);

        JSONObject jsonObject = new JSONObject(json_response);
        JSONArray jsonArray = jsonObject.getJSONArray("response");
        JSONArray box;
        JSONObject obj;
        RelativeLayout dot;
        Rect bounding_box;
        Bin_Item item;
        int offset = 0;

        for (int i = 0; i < jsonArray.length(); i++) {
            obj = jsonArray.getJSONObject(i);
            box = obj.getJSONArray("box");

            bounding_box = new Rect(box.getInt(0), box.getInt(1), box.getInt(2), box.getInt(3));
            item = generateBinItem(obj.getString("name"));
            dot = createDot(img_container, img_camera, offset,item.getItem_name(),item.getCategory(),bounding_box);
            changeDotMargins(bounding_box, dot, img_camera);

            offset += 40;
        }

        if(jsonArray.length()>0)
            setTooltip("On","Tap on a dot to inspect");
        else if(jsonArray.length()==0)
            setTooltip("On","No results found");

        if (jsonArray.length() > 0) {

            removeCards(jsonArray.length());
            addCards(jsonArray.length());

            RelativeLayout rl = (RelativeLayout) items_detected.getChildAt(0);
            items_detected.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (registerScroll) {
                        View v;
                        for (int j = 0; j < rl.getChildCount(); j++) {
                            v = rl.getChildAt(j);
                            if (isViewVisible(v, items_detected)) {
                                v = img_container.findViewWithTag(v.getTag());
                                if (v != null)
                                    changeSize((FloatingActionButton) v);
                            }
                        }
                    }
                }
            });
        }

        if(jsonArray.length()>0)
            return 1;

        return 0;//if no elements detected
    }


    private Bin_Item generateBinItem(String name) {

        String[] category_name = name.split("_");
        String item_name = category_name[1];
        String category = category_name[0];

        switch (category) {
            case "plastic":
                category = "#plastic";
                break;

            case "paper":
                category = "#paper";
                break;

            case "metal":
                category = "metal";
                break;

            case "rubber":
                category = "#rubber";
                break;

            case "glass":
                category = "#glass";
                break;

            case "e":
                category = "#e-waste";
                break;

        }

        item_name = item_name.substring(0, 1).toUpperCase() + item_name.substring(1);
        return new Bin_Item(item_name, category, "1", "0");
    }

    private View getCardView(int num,HorizontalScrollView scrollView){

        return ((RelativeLayout) scrollView.getChildAt(0)).getChildAt(num);
    }

    private void setTooltip(String mode,String text)
    {
        if(mode.equals("On")) {

            TextView txt = findViewById(R.id.txt_tooltip);
            txt.setText(text);

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tooltip.getLayoutParams();
//            params.setMargins(0,0,0,deltaY + 40);

            tooltip.setVisibility(View.VISIBLE);
//            tooltip.setLayoutParams(params);
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
                public void onAnimationCancel(@NonNull Animator animator) {}
                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {}
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

    private void fill_cards_with_info()
    {
        RelativeLayout container = (RelativeLayout) items_detected.getChildAt(0);

        for(int i = 0; i< container.getChildCount();i++)
        {
            View card = container.getChildAt(i);
            View dot = img_container.findViewWithTag(card.getTag());

            ShimmerFrameLayout shimmer = findShimmerLayout(card);
            shimmer.findViewById(R.id.view1).setVisibility(View.INVISIBLE);

            setImageDrawable(shimmer.findViewById(R.id.image), (String) dot.getTag(R.id.category));

            TextView txt_objName = shimmer.findViewById(R.id.text_view1);
            txt_objName.setVisibility(View.VISIBLE);
            txt_objName.setText((String)dot.getTag(R.id.obj_name));

            shimmer.findViewById(R.id.view2).setVisibility(View.INVISIBLE);

            RelativeLayout tag = shimmer.findViewById(R.id.container_tag);
            tag.setVisibility(View.VISIBLE);
            TextView txt_tag = tag.findViewById(R.id.txt_tag);
            txt_tag.setText((String) dot.getTag(R.id.category));
        }

    }

    private ShimmerFrameLayout findShimmerLayout(View card) {

        ShimmerFrameLayout layout = card.findViewById(R.id.shimmer);

        if( layout == null)
            layout = card.findViewById(R.id.shimmer1);

        if( layout == null)
            layout = card.findViewById(R.id.shimmer2);

        if( layout == null)
            layout = card.findViewById(R.id.shimmer3);

        return layout;

    }

    private void setImageDrawable(ImageView imageView,String category){

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        layoutParams.width -= (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
        layoutParams.height -= (int) (20 * getApplicationContext().getResources().getDisplayMetrics().density);
        layoutParams.setMargins((int) (5 * getApplicationContext().getResources().getDisplayMetrics().density), (int) (10 * getApplicationContext().getResources().getDisplayMetrics().density), 0 ,0 );

        imageView.setLayoutParams(layoutParams);
        imageView.setBackground(null);


        if(Objects.equals(category, "#plastic")){
            imageView.setImageResource(R.drawable.bin_ic_plastic_bag);
        } else if (Objects.equals(category, "#paper")) {
            imageView.setImageResource(R.drawable.bin_ic_paper);
        } else if (Objects.equals(category, "#metal")) {
            imageView.setImageResource(R.drawable.bin_ic_metal);
        } else if (Objects.equals(category, "#rubber")) {
            imageView.setImageResource(R.drawable.bin_ic_rubber);
        } else if (Objects.equals(category, "#glass")) {
            imageView.setImageResource(R.drawable.bin_ic_glass);
        } else {
            imageView.setImageResource(R.drawable.bin_ic_ewaste);
        }

        flag_changed = true;

    }

}

