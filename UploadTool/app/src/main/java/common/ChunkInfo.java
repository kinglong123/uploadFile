package common;

import java.io.Serializable;

/**
 * Created by lanjl on 2019/6/27.
 */
public class ChunkInfo implements Serializable {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件的MD5值
     */
    private String md5;

    /**
     * 文件长度
     */
    private long fileLength;


    /**
     * 是不是一个数据块
     * true 表示是一个文件，false表示是一个块（分片的数据）
     */
    private boolean isChunk;


    /**
     * 分块上传的开始位置，服务端需要根据这个位置来写入文件
     */
    private long start;

    /**
     * 文件的当前分片值
     */
    private int chunk=0;
    /**
     * 文件总分片值
     */
    private int chunks=1;
    /**
     * 下载进度值
     */
    private int progress=0;


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isChunk() {
        return isChunk;
    }

    public void setChunk(boolean chunk) {
        isChunk = chunk;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
