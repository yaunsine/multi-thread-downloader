package cn.yaunsine.core;

import cn.yaunsine.constants.Constant;
import cn.yaunsine.utils.FileUtils;
import cn.yaunsine.utils.HttpUtils;
import cn.yaunsine.utils.LogUtils;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 下载器
 */
public class Downloader implements DownloaderService{
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public void download(String url) {
        // 获取文件名
        String fileName = HttpUtils.getDownloadName(url);
        // 拼接得到文件下载路径
        String httpFileName = Constant.PATH + fileName;

        // 获取本地文件的大小
        long localFileLength = FileUtils.getFileContentLength(httpFileName);
        // 获取

        // 获取链接对象
        HttpURLConnection connection = null;
        DownloadInfoThread downloadInfoThread = null;
        try {
             connection = HttpUtils.getHttpURLConnection(url);
             // 获取下载文件的总大小
            int contentLength = connection.getContentLength();
            // 判断文件是否已经下载过
            if (localFileLength >= contentLength) {
                LogUtils.info("{}已下载完毕，无需重复下载", httpFileName);
                return ;
            }
            // 创建获取下载
            downloadInfoThread = new DownloadInfoThread(contentLength);

            executor.scheduleAtFixedRate(downloadInfoThread, 1, 1, TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream buffer = new BufferedInputStream(inputStream);
                FileOutputStream outputStream = new FileOutputStream(httpFileName);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                ){
            int len = -1;
            byte[] bufferRead = new byte[Constant.PER_SIZE];
            while((len = buffer.read(bufferRead)) != -1) {
                downloadInfoThread.downSize += len;
                bufferedOutputStream.write(bufferRead, 0, len);
            }
        } catch (FileNotFoundException e) {
            LogUtils.error("下载文件不存在{}", url);
        } catch (Exception e) {
            LogUtils.error("下载失败{}", url);
        } finally {
            LogUtils.info("\r");
            LogUtils.info("下载完成");
            if (connection != null) {
                connection.disconnect();
            }
            // 关闭线程池
            executor.shutdownNow();
        }
    }
}
