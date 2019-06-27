package com.ke.uploadtol.activity;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by lanjl on 2019/6/5.
 */
public interface uploadlientApi {

    @Multipart
    @POST("UploadServlet")
    Observable<String> getStingTest(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("HelloWorld")
    Observable<String> getStingHelloWorld(
            @Query("username") String username
    );
    @FormUrlEncoded
    @POST("HelloWorld")
    Observable<String> posTStingHelloWorld(
            @Field("objectId") String objectId
    );


    @POST("HelloWorld")
    Observable<String> posTStingHelloWorld2(
            @Body Name name
    );

    @FormUrlEncoded
    @POST("UploadFileServlet")
    Observable<String> uploadFileServlet(
            @Field("json") String objectId,
            @Field("video") String objectType
    );

    /**
     * 通过 MultipartBody和@body作为参数来实现多文件上传
     * @param multipartBody MultipartBody包含多个Part
     * @return 状态信息
     */
    @POST("UploadServlet")
    Observable<String> uploadFileWithRequestBody(@Body MultipartBody multipartBody);




    /**
     * 通过 MultipartBody和@body作为参数来实现多文件上传
     * @param multipartBody MultipartBody包含多个Part
     * @return 状态信息
     */
    @POST("UploadServletfengkuai")
    Observable<String> uploadFileWithRequestBodyfengkuai(@Body MultipartBody multipartBody);
}
