package com.example.recycle_app.Server;

import com.example.recycle_app.My_Bin.Bin_Item;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitCaller_Interface {
    @POST("/add-bin")
    Call<MyResponse> sendBinDataToSever(@Body UserInfo user);
}
