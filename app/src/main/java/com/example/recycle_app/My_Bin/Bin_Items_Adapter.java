package com.example.recycle_app.My_Bin;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.recycle_app.ActivityItem;
import com.example.recycle_app.Database.DatabaseHelper;
import com.example.recycle_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class Bin_Items_Adapter extends RecyclerView.Adapter<Bin_Items_Adapter.ViewHolder>{

    private DatabaseHelper dbHelper;
    private ArrayList<Bin_Item> bin_items;
    private final RecyclerView recyclerView;
    private int selected = 0;
    private Animation zoomIn, zoomOut,remove;
    private Drawable drawable=null;

    private ActivityResultLauncher<Intent> resultLauncher;

    private ArrayList<ViewHolder> selected_ViewHolder = new ArrayList<>();
    private ArrayList<Integer> selected_BinItems_position = new ArrayList<>();
    private LottieAnimationView empty_bin;
    private TextView txt_no_items;

    public void setEmpty_bin(LottieAnimationView empty_bin) {
        this.empty_bin = empty_bin;
    }

    public void setTxt_no_items(TextView txt_no_items) {
        this.txt_no_items = txt_no_items;
    }


    private ArrayList<Integer> drawableList = new ArrayList<>(Arrays.asList(R.drawable.bin_item_color_light_blue,R.drawable.bin_item_color_light_green,
            R.drawable.bin_item_color_pale_yellow,R.drawable.bin_item_color_peach,R.drawable.bin_item_color_light_purple,R.drawable.bin_item_color_light_red,
            R.drawable.bin_item_pale_color_grey));


    public void setDelete_items_btn(FloatingActionButton delete_items_btn) {
        this.delete_items_btn = delete_items_btn;
    }

    private FloatingActionButton delete_items_btn;


    public Bin_Items_Adapter(ArrayList<Bin_Item> items, RecyclerView recyclerView,ActivityResultLauncher<Intent> resultLauncher){
        this.bin_items = items;
        this.recyclerView = recyclerView;
        this.resultLauncher = resultLauncher;
        dbHelper = new DatabaseHelper(recyclerView.getContext());

        setUpAnimations();
    }

    public ArrayList<Bin_Item> getBin_items() {
        return bin_items;
    }
    public void setBin_items(ArrayList<Bin_Item> items) {
        this.bin_items = items;
    }


    public int getSelected_ItemsCount() {
        return selected_BinItems_position.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bin_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if(Objects.equals(bin_items.get(position).getCategory(), "#plastic")){
            holder.img_category.setImageResource(R.drawable.bin_ic_plastic_bag);
        } else if (Objects.equals(bin_items.get(position).getCategory(), "#paper")) {
            holder.img_category.setImageResource(R.drawable.bin_ic_paper);
        } else if (Objects.equals(bin_items.get(position).getCategory(), "#metal")) {
            holder.img_category.setImageResource(R.drawable.bin_ic_metal);
        } else if (Objects.equals(bin_items.get(position).getCategory(), "#rubber")) {
            holder.img_category.setImageResource(R.drawable.bin_ic_rubber);
        } else if (Objects.equals(bin_items.get(position).getCategory(), "#glass")) {
            holder.img_category.setImageResource(R.drawable.bin_ic_glass);
        } else {
            holder.img_category.setImageResource(R.drawable.bin_ic_ewaste);
        }

        if(getItemCount()==1) {
            holder.line_up.setVisibility(View.GONE);
            holder.line_down.setVisibility(View.GONE);
        }
        else if(position == 0) {
            holder.line_up.setVisibility(View.GONE);
            holder.line_down.setVisibility(View.VISIBLE);
        }
        else if (position == getItemCount()-1) {
            holder.line_up.setVisibility(View.VISIBLE);
            holder.line_down.setVisibility(View.GONE);
        }
        else {
            holder.line_up.setVisibility(View.VISIBLE);
            holder.line_down.setVisibility(View.VISIBLE);
        }

        holder.container.setBackgroundResource(getBackgroundResource());
        if(drawable!=null)// call when item is changed/edited
            holder.container.setBackground(drawable);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.selected && selected>0) {
                    holder.selected = true;
                    changeColor(Color.parseColor("#425C5A"), holder,100);
                    holder.remove.setVisibility(View.VISIBLE);
                    holder.remove.startAnimation(zoomIn);
                    selected_ViewHolder.add(holder);
                    selected_BinItems_position.add(holder.getAdapterPosition());
                    selected++;
                    changeDeleteAlpha();
                }
                else if(holder.selected)
                {
                    holder.selected = false;
                    changeColor(Color.parseColor("#59808080"), holder,0);
                    holder.remove.startAnimation(zoomOut);
                    holder.remove.setVisibility(View.GONE);
                    selected_ViewHolder.remove(holder);
                    selected_BinItems_position.remove(Integer.valueOf(holder.getAdapterPosition()));
                    selected--;
                    changeDeleteAlpha();
                }
                else{

                    Intent edit_item = new Intent(recyclerView.getContext(), ActivityItem.class);
                    edit_item.putExtra("Mode", "edit");
                    edit_item.putExtra("ItemName",holder.item_name.getText().toString());
                    edit_item.putExtra("Qty",holder.qty.getText().toString());
                    edit_item.putExtra("Wt",holder.wt.getText().toString());
                    edit_item.putExtra("Category",bin_items.get(holder.getAdapterPosition()).getCategory());
                    edit_item.putExtra("Position",holder.getAdapterPosition());
                    resultLauncher.launch(edit_item);
                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!holder.selected) {
                    holder.selected = true;
                    changeColor(Color.parseColor("#425C5A"), holder, 100);
                    holder.remove.setVisibility(View.VISIBLE);
                    holder.remove.startAnimation(zoomIn);
                    selected_ViewHolder.add(holder);
                    selected_BinItems_position.add(holder.getAdapterPosition());
                    selected++;
                    changeDeleteAlpha();
                }
                return true;
            }
        });

        holder.item_name.setText(bin_items.get(position).getItem_name());
        holder.qty.setText(bin_items.get(position).getQty());
        holder.wt.setText(bin_items.get(position).getWeight());

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int actual_position=holder.getAdapterPosition();

                if(actual_position == getItemCount()-1 && actual_position > 0)
                {
                    View item = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(actual_position - 1)).itemView;
                    item.findViewById(R.id.line_down).setVisibility(View.GONE);
                }

                if(actual_position == 0 && getItemCount()>1)
                {
                    View item = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(actual_position + 1)).itemView;
                    item.findViewById(R.id.line_up).setVisibility(View.GONE);
                }
                holder.container.callOnClick();
                holder.item.startAnimation(remove);
                bin_items.remove(actual_position);
                notifyItemRemoved(actual_position);

                checkBinItemSize();
                dbHelper.deleteBinItem(holder.item_name.getText().toString());

                String filepath = "/Images"+"/" + holder.item_name.getText().toString();
                File dir = new File(recyclerView.getContext().getFilesDir(), filepath);

                deleteDir(dir);

            }
        });

    }


    @Override
    public int getItemCount() {
        return bin_items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView item_name,qty,wt;
        ImageView img_category,indicator,remove;
        RelativeLayout container,item;
        Boolean selected = false;
        ProgressBar line_up,line_down;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.bin_item);
            item_name = itemView.findViewById(R.id.item_name);
            qty = itemView.findViewById(R.id.qty);
            wt = itemView.findViewById(R.id.wt);
            img_category = itemView.findViewById(R.id.img_waste_type);
            indicator = itemView.findViewById(R.id.indicator);
            remove = itemView.findViewById(R.id.btn_remove);
            container = itemView.findViewById(R.id.item_container);
            line_down = itemView.findViewById(R.id.line_down);
            line_up = itemView.findViewById(R.id.line_up);
            line_down.setRotation(180f);
        }
    }

    public void addBin_item(Bin_Item item) {
        this.bin_items.add(item);
        notifyItemInserted(getItemCount()-1);
        if(getItemCount()>1) {
            ProgressBar bar = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(getItemCount() - 2)).itemView.findViewById(R.id.line_down);
            bar.setVisibility(View.VISIBLE);
        }
    }

    public void editItem(Intent data){

        int position = data.getIntExtra("Position",0);
        bin_items.get(position).setItem_name(data.getStringExtra("Item_name"));
        bin_items.get(position).setCategory(data.getStringExtra("Item_category"));
        bin_items.get(position).setQty(data.getStringExtra("Item_qty"));
        bin_items.get(position).setWeight(data.getStringExtra("Item_weight"));
        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        drawable = holder.container.getBackground();
        notifyItemChanged(position);
    }

    public void removeSelectedItems() {

        ViewHolder holder;
        Collections.sort(selected_BinItems_position);
        File dir;


        for (int i = 0; i< selected_BinItems_position.size(); i++) {
            holder= (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selected_BinItems_position.get(i)-i);
            int actual_position = holder.getAdapterPosition();
            changeColor(Color.parseColor("#59808080"), holder,0);
            holder.selected =  false;
            holder.remove.setVisibility(View.GONE);

            if(actual_position == getItemCount()-1 && actual_position > 0)
            {
                View item = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(actual_position - 1)).itemView;
                item.findViewById(R.id.line_down).setVisibility(View.GONE);
            }

            if(actual_position == 0 && getItemCount()>1)
            {
                View item = Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(actual_position + 1)).itemView;
                item.findViewById(R.id.line_up).setVisibility(View.GONE);
            }
            bin_items.remove(actual_position);
            holder.item.startAnimation(remove);
            notifyItemRemoved(actual_position);

            dbHelper.deleteBinItem(holder.item_name.getText().toString());

            String filepath = "/Images"+"/" + holder.item_name.getText().toString();
            dir = new File(recyclerView.getContext().getFilesDir(), filepath);

            deleteDir(dir);

        }
        selected = 0;
        selected_ViewHolder.clear();
        selected_BinItems_position.clear();
        changeDeleteAlpha();
    }

    public void checkBinItemSize()
    {
        if (bin_items.size()==0) {
            empty_bin.setVisibility(View.VISIBLE);
            txt_no_items.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_bin.setVisibility(View.INVISIBLE);
            txt_no_items.setVisibility(View.INVISIBLE);
        }
    }

    int getBackgroundResource()
    {
        int Rid = drawableList.get(0);
        drawableList.remove(Integer.valueOf(Rid));
        drawableList.add(Rid);
        return Rid;

    }

    void changeColor(int colorTo,ViewHolder holder,int value)
    {
        ObjectAnimator animation1 = ObjectAnimator.ofInt(holder.line_up, "progress", value);
        ObjectAnimator animation2 = ObjectAnimator.ofInt(holder.line_down, "progress", value);

        animation1.setDuration(500); // 0.5 second
        animation2.setDuration(500); // 0.5 second
        animation1.start();
        animation2.start();

        holder.indicator.setColorFilter(colorTo);
    }

    public void unSelectItems()
    {

        for (int i=0;i<selected_ViewHolder.size();i++)
            selected_ViewHolder.get(i).container.callOnClick();
        for (int i=0;i<selected_ViewHolder.size();i++)
            selected_ViewHolder.get(i).container.callOnClick();

    }


    private void setUpAnimations()
    {

        zoomIn = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.zoom_out);
        zoomIn.setDuration(200);

        zoomOut = AnimationUtils.loadAnimation(recyclerView.getContext(), R.anim.zoom_in);
        zoomOut.setDuration(200);

        remove = new TranslateAnimation(0.0f,1500.0f,0.0f,0.0f);
        remove.setDuration(500);
    }

    public void changeDeleteAlpha(){
        if(selected==0)
            delete_items_btn.setImageAlpha(110);
        else
            delete_items_btn.setImageAlpha(255);
    }

    private boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}

