package com.example.libraryassistant.apiclient;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookInterface {
    @GET("api/book/")
    Call<List<Book>> getBook();

    @GET("api/book/{id}/")
    Call<List<Book>> getBookDetail(@Path("id") int id);

    @FormUrlEncoded
    @POST("api/book/")
    Call<Book> postBook(@Body Book book);

    @FormUrlEncoded
    @PUT("api/book/{id}/")
    Call<Book> postBook(@Path("id") int id, @Body Book book);

    @DELETE("api/book/{id}/")
    Call<Book> deleteBook(@Path("id") int id);
}
