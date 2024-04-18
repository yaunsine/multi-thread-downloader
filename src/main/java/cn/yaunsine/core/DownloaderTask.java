package cn.yaunsine.core;

import cn.yaunsine.constants.Constant;
import cn.yaunsine.utils.HttpUtils;
import cn.yaunsine.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 下载分片任务
 */
public class DownloaderTask implements Callable<Boolean> {
    private String url;
    /**
     * 下载的起始位置
     */
    private long startPos;
    /**
     * 下载的结束位置
     */
    private long endPos;
    /**
     * 标识当前是第几部分
     */
    private int part;

    private CountDownLatch countDownLatch;
    public DownloaderTask(String url, long startPos, long endPos, int part, CountDownLatch countDownLatch) {
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.part = part;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public Boolean call() throws Exception {
        // 获取文件名
        String downloadName = HttpUtils.getDownloadName(url);
        // 分块名称
        String partFileName = downloadName + ".temp" + part;
        // 文件下载路径
        String filePath = Constant.PATH + partFileName;
        HttpURLConnection httpURLConnection = HttpUtils.getHttpURLConnection(url, startPos, endPos);
//        int contentLength = httpURLConnection.getContentLength();
//        httpURLConnection.getRequestProperties().forEach((i, v)->{
//            System.out.println(i+" " +v);
//        });
        try (
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            RandomAccessFile accessFile = new RandomAccessFile(filePath, "rw");
        ) {
            byte[] buffer = new byte[Constant.PER_SIZE];
            int len = -1;
            // 循环读取数据
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                // 1秒内下载数据之和, 通过原子类进行操作
                DownloadInfoThread.downSize.add(len);
                accessFile.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("下载文件不存在{}", url);
            return false;
        } catch (Exception e) {
            LogUtils.error("下载出现异常");
            return false;
        } finally {
            httpURLConnection.disconnect();
            countDownLatch.countDown();
        }
        return true;
    }
}
