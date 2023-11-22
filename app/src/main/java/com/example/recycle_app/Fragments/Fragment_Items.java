package com.example.recycle_app.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.recycle_app.ActivityCreateItem;
import com.example.recycle_app.ActivityObjectDetect;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.example.recycle_app.My_Bin.Bin_Items_Adapter;
import com.example.recycle_app.Object_Detection.JavaObjectDetection;
import com.example.recycle_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class Fragment_Items extends Fragment {

    private Animation rotateOpen,rotateClose,fromBottom,toBottom;
    private boolean clicked = false;
    private FloatingActionButton options,edit,detection,delete;
    private Bin_Items_Adapter bin_items_adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//
//                        JavaObjectDetection objectDetection = new JavaObjectDetection();
//
//                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                            Bundle bundle = result.getData().getExtras();
//                            Bitmap bitmap = (Bitmap) bundle.get("data");
//                            File filesDir = getContext().getFilesDir();
//                            File imageFile = new File(filesDir, "Photo" + ".jpg");
//
//                            OutputStream os;
//                            try {
//                                os = new FileOutputStream(imageFile);
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//                                os.flush();
//                                os.close();
//                            } catch (Exception e) {
//                                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
//                            }
//                            objectDetection.detect(imageFile);
//                        }
//                    }
//                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__items, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ActivityResultLauncher<Intent> result_edit_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            bin_items_adapter.editItem(data);
                        }
                    }
                });


        rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(getContext(),R.anim.translate_from_bottom);
        toBottom = AnimationUtils.loadAnimation(getContext(),R.anim.translate_to_bottom );

        RecyclerView bin_recycler_view = view.findViewById(R.id.bin_items);
        ArrayList<Bin_Item> bin_items = getArguments().getParcelableArrayList("BinItems");
        bin_items_adapter = new Bin_Items_Adapter(bin_items,bin_recycler_view,result_edit_launcher);

        bin_recycler_view.setAdapter(bin_items_adapter);
        bin_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));


        options = view.findViewById(R.id.btn_options);
        edit = view.findViewById(R.id.btn_edit);
        detection = view.findViewById(R.id.btn_detect);
        delete = view.findViewById(R.id.btn_delete);

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(clicked);
                setAnimation(clicked);
                setClickable(clicked);
                clicked = !clicked;
                if(bin_items_adapter.getSelected_ItemsCount()==0)
                    delete.setImageAlpha(110);
                else
                    delete.setImageAlpha(255);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(options, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();

            layoutParams.bottomMargin = layoutParams.bottomMargin + insets.bottom;
            options.setLayoutParams(layoutParams);

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });


        ActivityResultLauncher<Intent> result_create_launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Bin_Item item = new Bin_Item(data.getStringExtra("Item_name"),data.getStringExtra("Item_category"),data.getStringExtra("Item_qty"), data.getStringExtra("Item_weight"));
                            bin_items_adapter.addBin_item(item);
                        }
                    }
                });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityCreateItem.class);
                intent.putExtra("Mode","create");
                result_create_launcher.launch(intent);
            }
        });

        ActivityResultLauncher<Intent> result_object_detect = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                        }
                    }
                });

        detection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent obj_detect = new Intent(getContext(), ActivityObjectDetect.class);
                result_object_detect.launch(obj_detect);
//                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if(camera_intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    activityResultLauncher.launch(camera_intent);
//                }
//                else
//                    Toast.makeText(getContext(),"A problem occured while opening detection",Toast.LENGTH_SHORT).show();
            }
        });

        bin_items_adapter.setDelete_items_btn(delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bin_items_adapter.removeSelectedItems();
            }
        });


    }


    void setVisibility(boolean clicked)
    {
        if(!clicked)
        {
            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            detection.setVisibility(View.VISIBLE);
        }
        else{
            edit.setVisibility(View.INVISIBLE);
            detection.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        }

    }

    void  setAnimation(boolean clicked){
        if(!clicked)
        {
            delete.startAnimation(fromBottom);
            edit.startAnimation(fromBottom);
            detection.startAnimation(fromBottom);
            options.startAnimation(rotateOpen);
        }
        else{
            delete.startAnimation(toBottom);
            edit.startAnimation(toBottom);
            detection.startAnimation(toBottom);
            options.startAnimation(rotateClose);
        }
    }

    public void setClickable(boolean clicked) {
        if(!clicked)
        {
            edit.setClickable(true);
            detection.setClickable(true);
            delete.setClickable(true);
        }
        else{
            edit.setClickable(false);
            detection.setClickable(false);
            delete.setClickable(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        bin_items_adapter.unSelectItems();
        if(clicked)
            options.callOnClick();
    }
}

