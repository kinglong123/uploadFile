package com.ke.uploadtol.tool;

import com.ke.uploadtol.activity.GitHubClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Source;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HttpUtils {
        public  static String TAG = HttpUtils.class.getName();
	public static void cutFileUpload(String fileType, String filePath) {
		try {
			FileAccessI fileAccessI = new FileAccessI(filePath, 0);
			Long nStartPos = 0l;
			Long length = fileAccessI.getFileLength();
			int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
			byte[] buffer = new byte[mBufferSize];
			FileAccessI.Detail detail;
			long nRead = 0l;
			String vedioFileName = generatePicName(); // 分配一个文件名
			long nStart = nStartPos;
			while (nStart < length) {
				detail = fileAccessI.getContent(nStart);
				nRead = detail.length;
				buffer = detail.b;
				final JSONObject mInDataJson = new JSONObject();
				mInDataJson.put("FileName", vedioFileName);
				mInDataJson.put("start", nStart); // 服务端获取开始文章进行写文件
				mInDataJson.put("filetype", fileType);
				nStart += nRead;
				nStartPos = nStart;
				String url = "http://10.0.3.2:8080/firstweb/UploadFileServlet";

				RequestParams params = new RequestParams();
				params.put("json", mInDataJson.toString());
				params.put("video", encodeByte(buffer));

				AsyncHttpClient client = new AsyncHttpClient();
				System.out.println("AsyncHttpClient:111"+nStart);
				client.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						System.out.println("AsyncHttpClient:111");
					}

					@Override
					public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

					}
				});


			}
		} catch (Exception e) {
		}
	}

	private final static byte[] encodePostFileu(String name, byte[] file) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(file);
		return out.toByteArray();
	}

	private static String encodeByte(byte[] buffer) {
		Log.d("lzx","encodeByte");
		return Base64.encodeToString(buffer, 0, buffer.length, Base64.DEFAULT);
	}

	public static String generatePicName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS", Locale.CHINA);
		String filename = sdf.format(new Date(System.currentTimeMillis())) + ".mp4";
		return filename;
	}



    public static void startUploadfenkuai(String fileType, String filename){

        try {
            FileAccessI fileAccessI = new FileAccessI(filename, 0);
            Long nStartPos = 0l;
            Long length = fileAccessI.getFileLength();
            int mBufferSize = 1024 * 100; // 每次处理1024 * 100字节
            byte[] buffer = new byte[mBufferSize];
            FileAccessI.Detail detail;
            long nRead = 0l;
            String vedioFileName = generatePicName(); // 分配一个文件名
            long nStart = nStartPos;
            while (nStart < length) {
                detail = fileAccessI.getContent(nStart);
                nRead = detail.length;
                buffer = detail.b;
                final JSONObject mInDataJson = new JSONObject();
                mInDataJson.put("FileName", vedioFileName);
                mInDataJson.put("start", nStart); // 服务端获取开始文章进行写文件
                mInDataJson.put("filetype", fileType);
                nStart += nRead;
                nStartPos = nStart;
                String url = "http://10.0.3.2:8080/firstweb/UploadFileServlet";

//                RequestParams params = new RequestParams();
//                params.put("json", mInDataJson.toString());
//                params.put("video", encodeByte(buffer));


                GitHubClient.INSTANCE.getApi().uploadFileServlet(mInDataJson.toString(),encodeByte(buffer))
                        .subscribeOn(Schedulers.io())//上面的 全部在异步线程中，subscribeOn放在的位置很重要，影响前面的doonnex和map的线程
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.i(TAG, "call: "+s);

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, "call: "+throwable.getMessage() );
                            }
                        });




            }
        } catch (Exception e) {
        }

        //先创建 service

        //构建要上传的文件

    }

    public  void startUploadfenkuai2(String fileType, String filename){

        File file = new File(filename);
        Long nStartPos = 0l;
//        Long length = fileAccessI.getFileLength();
        long fileLength=file.length();
        chunks=(int)(fileLength/1048576+(fileLength%1048576>0?1:0));
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
            start = i*chunkLength;
            if(i == chunks-1){
                size =   fileLength - start;

            }else {
                size = chunkLength;
            }

            uploadMonofile6(i,size,start,filename);


        }



        //先创建 service

        //构建要上传的文件

    }

    private  void uploadMonofile6(int chunk,long size,long start,String filename){
        //先创建 service

        //构建要上传的文件
        File file = new File(filename);
        MultipartBody multipartBody = filesToMultipartBody(chunk, size,start,"file",new String[]{filename}, MediaType.parse("application/otcet-stream"));
        GitHubClient.INSTANCE.getApi().uploadFileWithRequestBodyfengkuai(multipartBody)
                .subscribeOn(Schedulers.io())//上面的 全部在异步线程中，subscribeOn放在的位置很重要，影响前面的doonnex和map的线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, "call: "+s);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: "+throwable.getMessage() );
                    }
                });


    }
    /**
     * 其实也是将File封装成RequestBody，然后再封装成Part，<br>
     * 不同的是使用MultipartBody.Builder来构建MultipartBody
     * @param key 同上
     * @param filePaths 同上
     * @param imageType 同上
     */
    public  MultipartBody filesToMultipartBody(int chunk,long size,long start,String key,
            String[] filePaths,
            MediaType imageType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            RequestBody requestBody = create(chunk, size,imageType, file);
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
            builder.addFormDataPart("start", start+"");
            builder.addFormDataPart(key, file.getName(), requestBody);
        }

        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    int chunks = 1;  //总分片数
    int chunkLength = 1024 * 1024 * 1; //分片大小1MB

    /** Returns a new request body that transmits the content of {@code file}. */
    public  RequestBody create(final int chunk, final long size,final MediaType contentType, final File file) {
        if (file == null) throw new NullPointerException("content == null");



        return new RequestBody() {

            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return size;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {


                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
//                    randomAccessFile.seek(100);
                    //不使用FileInputStream

                    try {
                        //int size = 1024 * 1;//1KB缓冲区读取数据
                        byte[] tmp = new byte[1024];
                        //randomAccessFile.seek(chunk * chunkLength);
                        if (chunk+1<chunks){//中间分片
                            randomAccessFile.seek(chunk*chunkLength);
                            int n = 0;
                            long readLength = 0;//记录已读字节数
                            while (readLength <= chunkLength - 1024) {
                                n = randomAccessFile.read(tmp, 0, 1024);
                                readLength += 1024;
//                                out.write(tmp, 0, n);
                                sink.write(tmp, 0, n);
                            }
                            if (readLength <= chunkLength) {
                                n = randomAccessFile.read(tmp, 0, (int)(chunkLength - readLength));
//                                out.write(tmp, 0, n);
                                sink.write(tmp, 0, n);
                            }
                        }else{
                            randomAccessFile.seek(chunk*chunkLength);
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
                }catch (Exception E){
                    E.printStackTrace();
                }finally {
//                    Util.closeQuietly(source);
                }
            }
        };
    }

}
