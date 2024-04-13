package com.example.recycle_app.My_Bin;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Bin_Item implements Parcelable {

    private String Item_name;
    private String Qty;
    private String Category; // category saved as #plastic, #glass, #e-waste etc
    private String Weight;
    private ArrayList<String> images;

    public Bin_Item(){}

    public Bin_Item(String itemName, String category,String qty, String weight) {
        this.Item_name = itemName;
        this.Category = category;
        this.Qty = qty;
        this.Weight = weight;
    }

    protected Bin_Item(Parcel in) {
        Item_name = in.readString();
        Qty = in.readString();
        Category = in.readString();
        Weight = in.readString();
    }

    public static final Creator<Bin_Item> CREATOR = new Creator<Bin_Item>() {
        @Override
        public Bin_Item createFromParcel(Parcel in) {
            return new Bin_Item(in);
        }

        @Override
        public Bin_Item[] newArray(int size) {
            return new Bin_Item[size];
        }
    };

    public String getItem_name() {
        return Item_name;
    }

    public void setItem_name(String item_name) {
        Item_name = item_name;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        this.Weight = weight;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        this.Qty = qty;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(Item_name);
        parcel.writeString(Qty);
        parcel.writeString(Category);
        parcel.writeString(Weight);
    }
}
