package com.example.recycle_app.Server;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.recycle_app.Database.DatabaseHelper;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class UserInfo {
    private String email;
    private String username;
    private ArrayList<Bin_Item> items;

    private transient int item_uploaded_count = 0;
    private transient Boolean upload=false;

    public UserInfo()
    {}

    public Boolean getUpload() {
        return upload;
    }

    public void collect(Context context){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseHelper db = new DatabaseHelper(context);

        this.email = user.getEmail();
        this.username = user.getDisplayName();
        this.items = db.getBinData();

        uploadImagesToFireStore(context);

    }

    void uploadImagesToFireStore(Context context)
    {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://e-waste-22842.appspot.com");
        StorageReference storageRef = storage.getReference();


        for(int i=0;i<items.size();i++)
        {
            Bin_Item item = items.get(i);
            item.setImages(new ArrayList<>());
            File[] images = getImages(item.getItem_name(),context);

            for (File image: images) {
                String firestorageName = email+"_"+item.getItem_name()+"_"+image.getName();
                UploadTask task = storageRef.child(firestorageName).putFile(Uri.fromFile(image));
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child(firestorageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                item.getImages().add(uri.toString());
                                Log.e("Tag",uri.toString());
                                if(item.getImages().size() == images.length)
                                    item_uploaded_count++;
                                if(item_uploaded_count == items.size())
                                    upload=true;
                            }
                        });
                    }
                });

            }
        }
    }

    File[] getImages(String itemName,Context context)
    {
        String filepath = "/Images"+"/"+itemName;
        File dir = new File(context.getFilesDir(), filepath);
        File[] images = dir.listFiles();

        Arrays.sort(images);
        return images;
    }

    void runNewThread(StorageReference storageRef,File image)
    {



    }

}
