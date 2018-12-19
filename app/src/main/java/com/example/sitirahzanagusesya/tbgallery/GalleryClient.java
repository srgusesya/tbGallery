package com.example.sitirahzanagusesya.tbgallery;

import com.example.sitirahzanagusesya.tbgallery.model.GalleryItem;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GalleryClient {

    @GET("gallery2")
    Call<List<GalleryItem>> getData();

    @Multipart
    @POST("gallery2/insert")
    Call<ResponseBody> saveData(
            @Part("name")RequestBody nama,
            @Part("location")RequestBody lokasi,
            @Part("latitude")RequestBody lat,
            @Part("longitude")RequestBody longi,
            @Part("description")RequestBody desc,
            @Part MultipartBody.Part filePart,
            @Part("photo") RequestBody photo);
}
