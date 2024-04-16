package cn.yaunsine.core;

import cn.yaunsine.constants.Constant;
import cn.yaunsine.utils.LogUtils;

public class DownloadInfoThread implements Runnable{
    /**
     * 下载文件的总大小
     */
    private long httpFileContentLength;
    /**
     * 本地已下载内容的大小
     */
    public double finishedSize;
    /**
     * 前一次下载的大小
     */
    public double prevSize;
    /**
     * 本次累计下载的大小
     */
    public volatile double downSize;
    public DownloadInfoThread(long contentLength) {
        this.httpFileContentLength = contentLength;
    }
    @Override
    public void run() {
        // 计算文件总大小
        String httpFileSize = String.format("%.2f", httpFileContentLength / Constant.MB);
        // 计算每秒下载速度
        int speed = (int) ((downSize - prevSize) / 1024d);
        prevSize = downSize;
        // 剩余文件大小
        double remainHttpFileSize = httpFileContentLength - finishedSize - downSize;
        // 剩余时间
        String remainTime = String.format("%.1f", remainHttpFileSize / 1024d / speed);
        if ("Infinity".equalsIgnoreCase(remainTime)) {
            remainTime = "-";
        }
        // 已下载大小
        String finished = String.format("%.2f", (downSize - finishedSize) / Constant.MB);
        String downloadInfo = String.format("已下载 %s mb/%s mb, 速度 %s kb/s, 剩余时间 %s s", finished, httpFileSize, speed, remainTime);

        System.out.print(downloadInfo);
        // 不换行更新下载状态
        System.out.print("\r");
    }
}
