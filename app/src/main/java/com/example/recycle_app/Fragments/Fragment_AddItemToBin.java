package com.example.recycle_app.Fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycle_app.Database.DatabaseHelper;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.example.recycle_app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.blurry.Blurry;


public class Fragment_AddItemToBin extends Fragment {

    RelativeLayout mainLayout, dimmed_background ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__add_item_to_bin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnBackPressFuctionality(view);

        Bitmap image_bitmap = getArguments().getParcelable("image");
        String itemName = getArguments().getString("itemName");
        String itemCategory = getArguments().getString("itemCategory");;


        mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.enter_from_bottom));
        mainLayout.setOnClickListener(btn->closeFragment(view));

        dimmed_background = getActivity().findViewById(R.id.dimmed_background);
        dimmed_background.setVisibility(View.VISIBLE);
        dimmed_background.setOnClickListener(it->{
            closeFragment(view);
        });

        ImageView itemImage = view.findViewById(R.id.item_imageView);
        ImageView itemBackground = view.findViewById(R.id.item_background);
        ImageView categoryImage = view.findViewById(R.id.category_image_item);


        setCategoryImage(categoryImage,itemCategory);
        itemImage.setImageBitmap(image_bitmap);
        Blurry.with(getContext()).radius(8).sampling(8).from(image_bitmap).into(itemBackground);

        Bitmap backgroundBitmap = ((BitmapDrawable)itemBackground.getDrawable()).getBitmap();

        TextView txtItemName = view.findViewById(R.id.txt_itemName);
        TextView txtCategory = view.findViewById(R.id.txtCategory);

        txtItemName.setText(itemName);
        String unfiltered_Category = itemCategory;
        itemCategory = itemCategory.replace("#","");
        itemCategory = itemCategory.substring(0, 1).toUpperCase() + itemCategory.substring(1);
        txtCategory.setText(itemCategory);

        EditText edt_wt = view.findViewById(R.id.Wt);
        RelativeLayout wtContainer = view.findViewById(R.id.Wt_container);
        wtContainer.setOnClickListener(btn->{
            edt_wt.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edt_wt, InputMethodManager.SHOW_IMPLICIT);
        });

        Button addItem = view.findViewById(R.id.btn_add);
        addItem.setOnClickListener(btn->{
            Bitmap mergedBitmap = getMergerdBitmap(image_bitmap,backgroundBitmap);
            String wt = edt_wt.getText().toString();
            if(!wt.isEmpty() && !wt.equals("0")) {
                if(wt.startsWith("."))
                    wt = wt.replace(".","0.");
                addToDB(mergedBitmap, itemName, unfiltered_Category, wt);
                closeFragment(view);
            }
            else
                Toast.makeText(getContext(),"Please enter a valid weight",Toast.LENGTH_SHORT).show();

        });
    }


    void closeFragment(View view)
    {
        Animation slideDown = AnimationUtils.loadAnimation(getContext(),R.anim.exit_to_bottom);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                dimmed_background.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
                getActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mainLayout.startAnimation(slideDown);
    }
    private void setOnBackPressFuctionality(View view)
    {
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setEnabled(false);
                closeFragment(view);
            }
        });
    }

    private void setCategoryImage(ImageView imageView,String category)
    {

        switch (category){

            case "#plastic":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_plastic,null));
                break;

            case "#paper":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_paper,null));
                break;

            case "#metal":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_metal,null));
                break;

            case "#rubber":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_rubber,null));
                break;

            case "#glass":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_glass,null));
                break;

            case "#e-waste":
                imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_ai_ewaste,null));
                break;
        }
    }

    Bitmap getMergerdBitmap(Bitmap itemBitmap,Bitmap backgroundBitmap){
        int newDimen=0;

        if(itemBitmap.getWidth()>itemBitmap.getHeight())
            newDimen =  itemBitmap.getWidth();
        else
            newDimen =  itemBitmap.getHeight();
        backgroundBitmap = centerCropBitmap(backgroundBitmap);
        backgroundBitmap = createScaledBimap(backgroundBitmap,newDimen);

        Bitmap mergedBitmap = mergeBitmap(backgroundBitmap,itemBitmap);

        return mergedBitmap;
    }

    Bitmap centerCropBitmap(Bitmap srcBmp){

        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );

        }
        return dstBmp;
    }

    Bitmap createScaledBimap(Bitmap bitmap,int newDimen)
    {
        return Bitmap.createScaledBitmap(bitmap, newDimen, newDimen, true);
    }

    Bitmap mergeBitmap(Bitmap bitmap1, Bitmap overlayBitmap){
        int bitmap1Width = bitmap1.getWidth();
        int bitmap1Height = bitmap1.getHeight();
        int bitmap2Width = overlayBitmap.getWidth();
        int bitmap2Height = overlayBitmap.getHeight();

        float marginLeft = (float) (bitmap1Width * 0.5 - bitmap2Width * 0.5);
        float marginTop = (float) (bitmap1Height * 0.5 - bitmap2Height * 0.5);

        Bitmap finalBitmap = Bitmap.createBitmap(bitmap1Width, bitmap1Height, bitmap1.getConfig());
        Canvas canvas = new Canvas(finalBitmap);
        canvas.drawBitmap(bitmap1, new Matrix(), null);
        canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null);

        return finalBitmap;
    }

    void addToDB(Bitmap bitmap, String dirName,String itemCategory,String itemWt){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        DatabaseHelper db = new DatabaseHelper(getContext());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //make a separate dir with name as itemName in files
                File dir = new File(getContext().getFilesDir()+"/Images", dirName);

                if(!dir.exists()) {
                    //add to database
                    db.addBinItem(dirName,itemCategory,"1",itemWt,getContext());
                    dir.mkdirs();
                }
                else{
                    //update database
                    Bin_Item item= db.getItem(dirName,itemCategory);
                    String updatedQty = String.valueOf(Integer.parseInt(item.getQty()) + 1);
                    DecimalFormat decfor = new DecimalFormat("0.0");
                    String updatedWt = decfor.format((Float.parseFloat(item.getWeight()) + Float.parseFloat(itemWt)));
                    db.updateBinItem(dirName,dirName,itemCategory,updatedQty,updatedWt,getContext());
                }

                int num = dir.listFiles().length+1;

                File imgFile = new File(dir,"photo"+num+".png");

                try {
                    imgFile.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(imgFile);
                    // Compress the bitmap and write it to the output stream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}