package com.example.recycle_app.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.recycle_app.My_Bin.Bin_Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserData.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE BinData (" +
                                    "ItemName TEXT , " +
                                    "Category TEXT, " +
                                    "Quantity TEXT, " +
                                    "Weight TEXT, " +
                                    "Date TEXT," +
                                    "PRIMARY KEY (ItemName))";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public void addBinItem(String ItemName, String Category, String Qty, String Weight, Context context){

        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String Date = ft.format(new Date());

        ContentValues cv = new ContentValues();

        cv.put("ItemName", ItemName);
        cv.put("Category", Category);
        cv.put("Quantity", Qty);
        cv.put("Weight", Weight);
        cv.put("Date", Date);

//        long result = db.insert("BinData", null, cv);
        long result = db.insertWithOnConflict("BinData", null, cv,SQLiteDatabase.CONFLICT_REPLACE);
        if(result == -1)
            Toast.makeText(context, "Failed to add items", Toast.LENGTH_SHORT).show();

    }

    public void updateBinItem(String old_ItemName,String new_ItemName, String Category, String Qty, String Weight, Context context)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("ItemName", new_ItemName);
        cv.put("Category", Category);
        cv.put("Quantity", Qty);
        cv.put("Weight", Weight);

        long result = db.update("BinData",cv,"ItemName = ?", new String[]{old_ItemName} );

        if(result == -1)
            Toast.makeText(context, "Failed to update item", Toast.LENGTH_SHORT).show();
        db.close();

    }

    public void deleteBinItem(String itemName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("BinData","ItemName = ?",new String[]{itemName});
        db.close();

    }

    public Bin_Item getItem(String itemName,String itemCategory) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM BinData WHERE ItemName = ? AND Category = ?";

        Cursor cursor = null;
        Bin_Item bin_item = null;
        if(db!=null) {
            cursor = db.rawQuery(query, new String[]{itemName,itemCategory});

            while (cursor.moveToNext())
                bin_item = new Bin_Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));

            cursor.close();
            db.close();
        }
        return bin_item;
    }

    public ArrayList<Bin_Item> getBinData(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Bin_Item> bin_itemArrayList = new ArrayList<>();
        String query = "SELECT * FROM BinData";

        Cursor cursor = null;
        if(db!=null) {
            cursor = db.rawQuery(query, null);


            while (cursor.moveToNext())
                bin_itemArrayList.add(new Bin_Item(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));

            cursor.close();
            db.close();
        }

        return bin_itemArrayList;
    }


}