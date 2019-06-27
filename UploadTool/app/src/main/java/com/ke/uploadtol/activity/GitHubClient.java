package com.ke.uploadtol.activity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lanjl on 2016/12/3.
 */
//github api 测试
public enum GitHubClient {

    INSTANCE;

    private static uploadlientApi api;

    protected static String URL="http://10.0.3.2:8080/firstweb/";
    //    protected static String Url="http://dyapi.91open.com/v1/1021/app/getnewprojectinfo";


    //https://api.github.com/repos/kinglong123/baseLib/contents/json/string.json




    static {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build())
                .build();
        api = mRetrofit.create(uploadlientApi.class);
    }

    public uploadlientApi getApi() {
        return api;
    }


}
