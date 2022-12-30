package com.example.libraryassistant.apiclient;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Response;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface BookInterface {
    @GET("api/book/")
    Call<List<Book>> getBook();

    @GET("api/book/{id}/")
    Call<Book> getBookDetail(@Path("id") int id);

    @Multipart
    @POST("api/book/")
    Call<Book> postBook(@Part("title") RequestBody title,
                        @Part("author") RequestBody author,
                        @Part("isbn") RequestBody isbn,
                        @Part("publisher") RequestBody publisher,
                        @Part("published_at") RequestBody published_at,
                        @Part("description") RequestBody description,
                        @Part MultipartBody.Part image);

    @Multipart
    @PUT("api/book/{id}/")
    Call<Book> updateBookWithImage(@Path("id") int id,
                                   @Part("title") RequestBody title,
                                   @Part("author") RequestBody author,
                                   @Part("isbn") RequestBody isbn,
                                   @Part("publisher") RequestBody publisher,
                                   @Part("published_at") RequestBody published_at,
                                   @Part("description") RequestBody description,
                                   @Part MultipartBody.Part image);

    @FormUrlEncoded
    @PUT("api/book/{id}/")
    Call<Book> updateBook(@Path("id") int id,
                          @Field("title") String title,
                          @Field("author") String author,
                          @Field("isbn") String isbn,
                          @Field("publisher") String publisher,
                          @Field("published_at") String published_at,
                          @Field("description") String description);

    @DELETE("api/book/{id}/")
    Call<Book> deleteBook(@Path("id") int id);
}
