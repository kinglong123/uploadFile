package com.ke.uploadtol.activity;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import common.ChunkInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Source;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lanjl on 2019/6/27.
 */
public class UpLoadAction {

    public static final String TAG = UpLoadAction.class.getName();

    public static final int chunkLength = 1024 * 1024 * 1; //分片大小1MB


    ChunkInfo mChunkInfo;


    public UpLoadAction(ChunkInfo chunkInfo) {
        mChunkInfo = chunkInfo;
    }


    public void start() {

        //先创建 service

        //构建要上传的文件

        MultipartBody multipartBody = filesToMultipartBody("file",
                new String[]{mChunkInfo.getFilePath()}, MediaType
                        .parse("application/otcet-stream"));
        GitHubClient.INSTANCE.getApi().uploadFileWithRequestBodyfengkuai(multipartBody)
                .subscribeOn(Schedulers.io())//上面的 全部在异步线程中，subscribeOn放在的位置很重要，影响前面的doonnex和map的线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, "call: " + s);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: " + throwable.getMessage());
                    }
                });
    }

    /**
     * 其实也是将File封装成RequestBody，然后再封装成Part，<br>
     * 不同的是使用MultipartBody.Builder来构建MultipartBody
     *
     * @param key       同上
     * @param filePaths 同上
     * @param imageType 同上
     */
    public MultipartBody filesToMultipartBody(
            String key,
            String[] filePaths,
            MediaType imageType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            RequestBody requestBody = create(imageType, file);
//            RequestBody requestFileProgress = new ProgressRequestBody(requestBody,
//                    new ProgressRequestBody.UploadProgressListener() {
//                        @Override
//                        public void onProgress(long currentBytesCount, long totalBytesCount) {
//
////			 progressBar.setMax((int) totalBytesCount);
////			progressBar.setProgress((int) currentBytesCount);
//
//                            System.out.println("(int) totalBytesCount:"+(int) totalBytesCount);
//                            System.out.println("(int) currentBytesCount:"+(int) currentBytesCount);
//                        }
//                    });
            builder.addFormDataPart("start", mChunkInfo.getStart() + "");
            builder.addFormDataPart(key, file.getName(), requestBody);
        }

        builder.setType(MultipartBody.FORM);
        return builder.build();
    }


    /** Returns a new request body that transmits the content of {@code file}. */
    public RequestBody create(
            final MediaType contentType, final File file) {
        if (file == null) {
            throw new NullPointerException("content == null");
        }

        return new RequestBody() {

            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return mChunkInfo.getFileLength();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {

                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
//                    randomAccessFile.seek(100);
                    //不使用FileInputStream

                    try {
                        //int size = 1024 * 1;//1KB缓冲区读取数据
                        byte[] tmp = new byte[1024];
                        //randomAccessFile.seek(chunk * chunkLength);
                        if (mChunkInfo.getChunk() + 1 < mChunkInfo.getChunks()) {//中间分片
                            randomAccessFile.seek(mChunkInfo.getChunk() * chunkLength);
                            int n = 0;
                            long readLength = 0;//记录已读字节数
                            while (readLength <= chunkLength - 1024) {
                                n = randomAccessFile.read(tmp, 0, 1024);
                                readLength += 1024;
//                                out.write(tmp, 0, n);
                                sink.write(tmp, 0, n);
                            }
                            if (readLength <= chunkLength) {
                                n = randomAccessFile.read(tmp, 0, (int) (chunkLength - readLength));
//                                out.write(tmp, 0, n);
                                sink.write(tmp, 0, n);
                            }
                        } else {
                            randomAccessFile.seek(mChunkInfo.getChunk() * chunkLength);
                            int n = 0;
                            while ((n = randomAccessFile.read(tmp, 0, 1024)) != -1) {
//                                out.write(tmp, 0, n);
                                sink.write(tmp, 0, n);
                            }
                        }
                        sink.flush();
                    } finally {
                        randomAccessFile.close();
                    }

//                    source = Okio.source(file);
//                    sink.writeAll(source);
                } catch (Exception E) {
                    E.printStackTrace();
                } finally {
//                    Util.closeQuietly(source);
                }
            }
        };
    }

}
