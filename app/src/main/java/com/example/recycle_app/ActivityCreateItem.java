package com.example.recycle_app;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ActivityCreateItem extends AppCompatActivity {

    private RelativeLayout selected_item=null;
    private TextView selected_txt_category=null;
    private StringBuilder imgName = new StringBuilder("photo1");

    private ArrayList<File> delete_photos_on_ok = new ArrayList<>();
    private ArrayList<File> delete_photos_on_close = new ArrayList<>();
    private File imgFile=null,dir=null;
    private Uri imgUri = null;
    private String mode,undo_item_name;

    private ImageView close;
    int postion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        Bundle bundle = getIntent().getExtras();
        mode = (String) bundle.get("Mode");


        RelativeLayout rl = findViewById(R.id.activity_createItem);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        close = findViewById(R.id.btn_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dir!=null && mode.equals("create"))
                {
                    File[] photos = dir.listFiles();
                    if (photos != null) {
                        for (File photo : photos) {
                            photo.delete();
                        }
                    }
                    dir.delete();
                }
                else if(dir!=null && mode.equals("edit")){
                    File[] photos = dir.listFiles();
                    File oldDir = dir;

                    String filepath = "/Images"+"/"+undo_item_name;
                    dir = new File(getFilesDir(), filepath);
                    dir.mkdirs();

                    assert photos != null;
                    for(File photo: photos){
                        photo.renameTo(new File(dir + "/"+ photo.getName()));
                    }
                    oldDir.delete();

                    for (File photo : delete_photos_on_close)
                        photo.delete();
                }
                finish();
            }
        });


        EditText itemName = findViewById(R.id.edt_txt_itemName);
        EditText qty = findViewById(R.id.edt_txt_itemQty);
        EditText weight = findViewById(R.id.edt_txt_itemWeight);

        itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(findViewById(R.id.error_itemName).getVisibility() == View.VISIBLE)
                    findViewById(R.id.error_itemName).setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(dir!=null && checkEdtText(itemName))
                {
                    File[] photos = dir.listFiles();
                    File oldDir = dir;

                    // creating new dir
                    String filepath = "/Images"+"/"+itemName.getText().toString().trim();
                    dir = new File(getFilesDir(), filepath);
                    dir.mkdirs();

                    assert photos != null;
                    for(File photo: photos){
                        photo.renameTo(new File(dir + "/"+ photo.getName()));
                    }
                    oldDir.delete();
                }
            }
        });

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(findViewById(R.id.error_itemQty).getVisibility() == View.VISIBLE)
                    findViewById(R.id.error_itemQty).setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(findViewById(R.id.error_itemWeight).getVisibility() == View.VISIBLE)
                    findViewById(R.id.error_itemWeight).setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        Button ok = findViewById(R.id.btn_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (File photo : delete_photos_on_ok)
                    photo.delete();
                if (checkEdtText(itemName) && checkEdtText(qty) && checkEdtText(weight) && checkSelected() && checkDir()) {
                    Intent result_intent = new Intent();
                    result_intent.putExtra("Item_name", itemName.getText().toString().trim());
                    result_intent.putExtra("Item_category", selected_txt_category.getText().toString());
                    result_intent.putExtra("Item_qty", qty.getText().toString());
                    result_intent.putExtra("Item_weight", weight.getText().toString());
                    setResult(RESULT_OK, result_intent);
                    if(mode.equals("edit"))
                        result_intent.putExtra("Position", postion);
                    finish();
                }

            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        RelativeLayout container1 = findViewById(R.id.container_itemName);
        container1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemName.requestFocus();
                imm.showSoftInput(itemName, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        RelativeLayout container2 = findViewById(R.id.container_itemQty);
        container2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty.requestFocus();
                imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        RelativeLayout container3 = findViewById(R.id.container_itemWeight);
        container3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight.requestFocus();
                imm.showSoftInput(weight, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        RelativeLayout plastic = findViewById(R.id.container_plastic);
        plastic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(plastic,findViewById(R.id.txt_plastic));
            }
        });

        RelativeLayout paper = findViewById(R.id.container_paper);
        paper.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(paper,findViewById(R.id.txt_paper));
            }
        });

        RelativeLayout metal = findViewById(R.id.container_metal);
        metal.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(metal,findViewById(R.id.txt_metal));
            }
        });

        RelativeLayout rubber = findViewById(R.id.container_rubber);
        rubber.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(rubber,findViewById(R.id.txt_rubber));
            }
        });

        RelativeLayout glass = findViewById(R.id.container_glass);
        glass.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(glass,findViewById(R.id.txt_glass));
            }
        });

        RelativeLayout ewaste = findViewById(R.id.container_ewaste);
        ewaste.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setSelected(ewaste,findViewById(R.id.txt_ewaste));
            }
        });


        ImageView camera = findViewById(R.id.img_camera);
        RelativeLayout container = findViewById(R.id.camera_scroll_container);

        ActivityResultLauncher<Uri> contract = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {

                        // updating imgName
                        if(result) {
                            int imgNo = Integer.parseInt(imgName.substring(5));
                            imgNo++;
                            imgName.replace(5, imgName.length(), String.valueOf(imgNo));
                            setUpLayout(null,imgUri,container);
                        }
                        else
                            imgFile.delete();
                    }
                });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(itemName.getText().toString().trim().equals("") || itemName.getText().toString().trim().length()==1)
                    Toast.makeText(ActivityCreateItem.this, "Please set an appropriate Item Name first", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        imgUri = createImageURI(itemName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (camera_intent.resolveActivity(getPackageManager()) != null)
                        contract.launch(imgUri);
                    else
                        Toast.makeText(ActivityCreateItem.this, "A problem occurred while opening camera", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(mode.equals("edit"))
        {
            postion = (int) bundle.get("Position");

            String ItemName = (String) bundle.get("ItemName");
            itemName.setText(ItemName);
            undo_item_name = ItemName;

            String Qty = (String) bundle.get("Qty");
            qty.setText(Qty);

            String Wt = (String) bundle.get("Wt");
            weight.setText(Wt);

            String Category = (String) bundle.get("Category");
            switch (Category){
                case "#plastic":
                    plastic.callOnClick();
                    break;
                case "#paper":
                    paper.callOnClick();
                    break;
                case "#metal":
                    metal.callOnClick();
                    break;
                case "#rubber":
                    rubber.callOnClick();
                    break;
                case "#glass":
                    glass.callOnClick();
                    break;
                case "#e-waste":
                    ewaste.callOnClick();
                    break;
            }

            String filepath = "/Images"+"/"+itemName.getText().toString().trim();
            dir = new File(getFilesDir(), filepath);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            else{
                File[] f = dir.listFiles();
                Arrays.sort(f);
                for(File photo: Objects.requireNonNull(f)) {
                    setUpLayout(photo, null,container);
                    imgName.replace(0,imgName.length(),photo.getName());
                }
                imgName.delete(imgName.length()-4,imgName.length());
                int imgNo = Integer.parseInt(imgName.substring(5));
                imgNo++;
                imgName.replace(5, imgName.length(), String.valueOf(imgNo));
            }
        }

    }

    private void setSelected(RelativeLayout item, TextView txt_category){

        changeColor(getColor(R.color.light_grey_green),getColor(R.color.grey_green),item);
        txt_category.setTextColor(getColor(R.color.white));

        if(selected_item!=null && selected_item != item)
        {
            selected_txt_category.setTextColor(getColor(R.color.light_grey));
            changeColor(getColor(R.color.grey_green),getColor(R.color.light_grey_green),selected_item);
        }
        selected_item = item;
        selected_txt_category = txt_category;

        findViewById(R.id.error_itemCategory).setVisibility(View.INVISIBLE);// if visible then sets to invisible
    }

    private void setUpLayout(File photo,Uri uri,RelativeLayout container)
    {
        RelativeLayout img_layout = (RelativeLayout) View.inflate(ActivityCreateItem.this, R.layout.infate_img_layout, null);
        ImageView imageView = img_layout.findViewById(R.id.roundedImageView);

        if(photo!=null){
            Bitmap myBitmap = BitmapFactory.decodeFile(photo.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
            imageView.setTag(photo.getAbsolutePath());
        }
        else {
            imageView.setImageURI(uri);
            imageView.setTag(imgFile.getAbsolutePath());
            delete_photos_on_close.add(imgFile);
        }


        changeMargins(container,"increase",0,false);
        container.addView(img_layout);

        ImageView remove = img_layout.findViewById(R.id.img_remove);
        img_layout.findViewById(R.id.card).setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {

                remove.setVisibility(View.VISIBLE);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation zoom_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.zoom_in);
                        zoom_in.setDuration(200);
                        File img_to_be_deleted = new File((String) imageView.getTag());

                        if(mode.equals("create"))
                            img_to_be_deleted.delete();
                        else
                            delete_photos_on_ok.add(img_to_be_deleted);
                        changeMargins(container,"decrease",container.indexOfChild(img_layout),true);

                        img_layout.startAnimation(zoom_in);
                        container.removeView(img_layout);
                    }
                });
                return true;
            }
        });

        img_layout.findViewById(R.id.card).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                remove.setVisibility(View.GONE);
            }
        });

    }

    private void changeColor(int colorFrom, int colorTo, RelativeLayout item)
    {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(100); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                item.getBackground().setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.SRC);
            }
        });
        colorAnimation.start();
    }

    private boolean checkEdtText(EditText edt)
    {
        if(edt.getText().toString().trim().equals("")) {
            switch (edt.getId())
            {
                case R.id.edt_txt_itemName:
                    findViewById(R.id.error_itemName).setVisibility(View.VISIBLE);
                    break;
                case R.id.edt_txt_itemQty:
                    findViewById(R.id.error_itemQty).setVisibility(View.VISIBLE);
                    break;
                case R.id.edt_txt_itemWeight:
                    findViewById(R.id.error_itemWeight).setVisibility(View.VISIBLE);
                    break;
            }
            return false;
        } else
            return true;
    }

    private boolean checkSelected()
    {
        if(selected_item==null)
        {
            findViewById(R.id.error_itemCategory).setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    private boolean checkDir()
    {
        if(dir==null || Objects.requireNonNull(dir.listFiles()).length==0) {
            Toast.makeText(ActivityCreateItem.this, "Please upload at least one image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private Uri createImageURI(EditText itemName) throws IOException {

        //make a separate dir with name as itemName in files
        String filepath = "/Images"+"/"+itemName.getText().toString().trim();
        dir = new File(getFilesDir(), filepath);
        dir.mkdirs();

        imgFile = new File(getFilesDir()+ filepath,imgName.toString()+".png");
        imgFile.createNewFile();

        return FileProvider.getUriForFile(getApplicationContext(),"com.example.recycle_app.fileProvider", imgFile);
    }

    private void changeMargins(RelativeLayout container,String mode,int childIndex,boolean animate)
    {
        RelativeLayout.LayoutParams params;
        int margin_left;

        for(int i=0;i < container.getChildCount();i++)
        {
            params = (RelativeLayout.LayoutParams) container.getChildAt(i).getLayoutParams();

            if(mode.equals("increase"))
                margin_left = params.getMarginStart() + (int) convertDpToPx(ActivityCreateItem.this, 85f);
            else {

                if(i<=childIndex)
                    margin_left = params.getMarginStart() - (int) convertDpToPx(ActivityCreateItem.this, 85f);
                else
                    margin_left = params.getMarginStart();
            }
            if(animate)
                animateChange(container.getChildAt(i),params,margin_left,0);
            else {
                params.setMargins(margin_left, 0, 0, 0);
                container.getChildAt(i).setLayoutParams(params);
            }
        }

    }

    private void animateChange(View child,RelativeLayout.LayoutParams params,int leftMargin,int topMargin) {

        ValueAnimator animator = ValueAnimator.ofInt(params.getMarginStart(), leftMargin);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                params.setMargins( (Integer) valueAnimator.getAnimatedValue(), topMargin, 0, 0); //substitute parameters for left, top, right, bottom
                child.setLayoutParams(params);
            }
        });
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close.callOnClick();
    }
}