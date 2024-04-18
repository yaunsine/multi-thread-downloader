package cn.yaunsine.core;

import cn.yaunsine.constants.Constant;
import cn.yaunsine.utils.FileUtils;
import cn.yaunsine.utils.HttpUtils;
import cn.yaunsine.utils.LogUtils;
import sun.rmi.runtime.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * 下载器
 */
public class Downloader implements DownloaderService{
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Constant.THREAD_NUM, Constant.THREAD_NUM, 0,
                                                TimeUnit.SECONDS, new ArrayBlockingQueue<>(Constant.THREAD_NUM));
    private CountDownLatch countDownLatch = new CountDownLatch(Constant.THREAD_NUM);
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

            // 切分下载
            ArrayList<Future> list = new ArrayList<>();
            split(url, list);
//            list.forEach(future -> {
//                try {
//                    future.get();
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
//                }
//            });

            countDownLatch.await();
            if (merge(httpFileName)) {
                deleteTempFile(httpFileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            LogUtils.info("\r");
            LogUtils.info("下载完成");
            if (connection != null) {
                connection.disconnect();
            }
            // 关闭线程池
            executor.shutdownNow();
            threadPoolExecutor.shutdown();
        }
    }
    public void split(String url, ArrayList<Future> futureArrayList) {
        try {
            long contentLength = HttpUtils.getContentLength(url);
            // 计算切分后的文件大小
            long per_size = contentLength / Constant.THREAD_NUM;

            // 计算分块的个数
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                // 计算下载的起始位置
                long startPos = i * per_size;
                // 计算每块的结束位置
                long endPos;
                if (i == Constant.THREAD_NUM - 1) {
                    endPos = 0;
                } else {
                    endPos = startPos + per_size;
                }
                // 如果不是第一块， 起始位置+1
                if (startPos != 0) {
                    startPos++;
                }
                DownloaderTask downloaderTask = new DownloaderTask(url, startPos, endPos, i, countDownLatch);
                Future<Boolean> future = threadPoolExecutor.submit(downloaderTask);
                futureArrayList.add(future);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 清空临时文件
     * @param fileName
     * @return
     */
    public boolean deleteTempFile(String fileName) {
        for (int i = 0; i < Constant.THREAD_NUM; i++) {
            String tempFile = fileName + Constant.TEMP_FILE_SUFFIX + i;
            File file = new File(tempFile);
            boolean isDelete = file.delete();
            if (!isDelete) {
                LogUtils.error("清除临时文件出错{}", tempFile);
                return false;
            }
        }
        return true;
    }

    /**
     * 合并文件
     * @param fileName
     * @return
     */
    public boolean merge(String fileName) {
        LogUtils.info("开始合并文件{}", fileName);
        byte[] buffer = new byte[Constant.PER_SIZE];
        try(
                RandomAccessFile accessFile = new RandomAccessFile(fileName, "rw");

                ){
            int len = -1;
            for (int i = 0; i < Constant.THREAD_NUM; i++) {
                try (
                        FileInputStream fileInputStream = new FileInputStream(fileName + Constant.TEMP_FILE_SUFFIX + i);
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                        ){
                    while ((len = bufferedInputStream.read(buffer)) != -1) {
                        accessFile.write(buffer, 0, len);
                    }
                }
            }
            LogUtils.info("文件合并完成{}", fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
