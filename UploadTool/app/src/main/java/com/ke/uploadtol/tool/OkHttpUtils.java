package com.ke.uploadtol.tool;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by lanjl on 2019/6/18.
 */
public class OkHttpUtils {
    final MediaType MEDIA_OBJECT_STREAM = MediaType.parse("application/octet-stream");

    final String   BASE_URL ="";
    final String   TAG =OkHttpUtils.class.getName();
    OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    /**
     * 上传文件
     * @param actionUrl 接口地址
     * @param filePath  本地文件地址
     */
    public <T> void upLoadFile(String actionUrl, String filePath, final ReqCallBack<T> callBack) {
        //补全请求地址
        String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
        //创建File
        File file = new File(filePath);
        //创建RequestBody
        RequestBody body = RequestBody.create(MEDIA_OBJECT_STREAM, file);
        //创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
//                failedCallBack("上传失败", callBack);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.e(TAG, "response ----->" + string);
//                    successCallBack((T) string, callBack);
                } else {
//                    failedCallBack("上传失败", callBack);
                }
            }
        });
    }
    /**
     *上传文件
     * @param actionUrl 接口地址
     * @param paramsMap 参数
     * @param callBack 回调
     * @param <T>
     */
    public <T>void upLoadFile(String actionUrl, HashMap<String, Object> paramsMap, final ReqCallBack<T> callBack) {
        try {
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
//                    failedCallBack("上传失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
//                        successCallBack((T) string, callBack);
                    } else {
//                        failedCallBack("上传失败", callBack);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }



    }
    /**
     *上传文件
     * @param actionUrl 接口地址
     * @param paramsMap 参数
     * @param callBack 回调
     * @param <T>
     */
    public <T> void upLoadFile(String actionUrl, HashMap<String, Object> paramsMap, final ReqProgressCallBack<T> callBack) {
        try {
            //补全请求地址
            String requestUrl = String.format("%s/%s", BASE_URL, actionUrl);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置类型
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), createProgressRequestBody(MEDIA_OBJECT_STREAM, file, callBack));
                }
            }
            //创建RequestBody
            RequestBody body = builder.build();
            //创建Request
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, e.toString());
//                    failedCallBack("上传失败", callBack);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.e(TAG, "response ----->" + string);
//                        successCallBack((T) string, callBack);
                    } else {
//                        failedCallBack("上传失败", callBack);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    } /**
     * 创建带进度的RequestBody
     * @param contentType MediaType
     * @param file  准备上传的文件
     * @param callBack 回调
     * @param <T>
     * @return
     */
    public <T> RequestBody createProgressRequestBody(final MediaType contentType, final File file, final ReqProgressCallBack<T> callBack) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    long remaining = contentLength();
                    long current = 0;
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        current += readCount;
                        Log.e(TAG, "current------>" + current);
                        progressCallBack(remaining, current, callBack);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };



    }

    public interface ReqProgressCallBack<T>  extends ReqCallBack<T>{
        /**
         * 响应进度更新
         */
        void onProgress(long total, long current);
    }
    public interface  ReqCallBack<T>{
        /**
         * 响应进度更新
         */
        void onProgress(long total, long current);
    }
    /**
     * 统一处理进度信息
     * @param total    总计大小
     * @param current  当前进度
     * @param callBack
     * @param <T>
     */
    private <T> void progressCallBack(final long total, final long current, final ReqProgressCallBack<T> callBack) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onProgress(total, current);
                }
            }
        });
    }
}
