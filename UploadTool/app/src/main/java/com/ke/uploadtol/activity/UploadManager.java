package com.ke.uploadtol.activity;


import android.content.Context;

import java.io.File;

import common.ChunkInfo;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by lanjl on 2019/6/27.
 */
public class UploadManager {

    private final String TAG = UploadManager.class.getName();

    private static UploadManager mUploadManager;

    private Context mContext;

    public static UploadManager newInstance(Context context) {
        if (mUploadManager == null) {
            synchronized (UploadManager.class) {
                if (mUploadManager == null) {
                    mUploadManager = new UploadManager(context);
                }
            }

        }
        return mUploadManager;
    }

    public static UploadManager getInstance() {
        if (null == mUploadManager) {
            throw new RuntimeException(
                    "UploadManager hasn't been initialized! Pls call init() method at first.");
        }
        return mUploadManager;
    }

    private UploadManager(Context context) {
        log.d(TAG, "UploadManager");
        mContext = context;
         //这边先开枪
      //  bindService(context);

    }
    public static void init(Context context) {
        if(mUploadManager ==null){
            mUploadManager =newInstance(context);
            //一些参数设置 下载保存路径之类的
        }
    }




    public void start(String filename) {
        File file = new File(filename);

        long fileLength=file.length();


        int chunks=(int)(fileLength/UpLoadAction.chunkLength+(fileLength%UpLoadAction.chunkLength>0?1:0));
        long start = 0;

        for (int i=0;i<chunks;i++){//当前分片值从1开始  //这边开启的
//            ChunkInfo chunkInfo=new ChunkInfo();
//            chunkInfo.setGguid(fileInfo.getGguid());
//            chunkInfo.setMd5(fileInfo.getMd5());
//            chunkInfo.setChunk(i);
//            chunkInfo.setChunks(chunks);
//            chunkInfo.setFilePath(fileInfo.getFilePath());
//            upResult= new UpFileToServer(UploadService.this).upToServer(chunkInfo,"upchunks");

            long size = 0;
            start = i*UpLoadAction.chunkLength;
            if(i == chunks-1){
                size =   fileLength - start;

            }else {
                size = UpLoadAction.chunkLength;
            }

            ChunkInfo chunkInfo = new  ChunkInfo();

            chunkInfo.setFilePath(filename);
            chunkInfo.setFileLength(size);
            chunkInfo.setChunks(chunks);
            chunkInfo.setChunk(i);
            chunkInfo.setStart(start);
            UpLoadAction upLoadAction = new  UpLoadAction(chunkInfo);
            upLoadAction.start();

        }

    }


}
