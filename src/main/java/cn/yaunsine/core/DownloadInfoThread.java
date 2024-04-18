package cn.yaunsine.core;

import cn.yaunsine.constants.Constant;
import cn.yaunsine.utils.LogUtils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class DownloadInfoThread implements Runnable{
    /**
     * 下载文件的总大小
     */
    private long httpFileContentLength;
    /**
     * 本地已下载内容的大小
     */
    public volatile static LongAdder finishedSize = new LongAdder();
    /**
     * 前一次下载的大小
     */
    public double prevSize;
    /**
     * 本次累计下载的大小
     */
    public static volatile LongAdder downSize = new LongAdder();
    public DownloadInfoThread(long contentLength) {
        this.httpFileContentLength = contentLength;
    }
    @Override
    public void run() {
        // 计算文件总大小
        String httpFileSize = String.format("%.2f", httpFileContentLength / Constant.MB);
        // 计算每秒下载速度
        double speed = (downSize.doubleValue() - prevSize) / 1024d;
        prevSize = downSize.doubleValue();
        // 剩余文件大小
        double remainHttpFileSize = httpFileContentLength - finishedSize.doubleValue() - downSize.doubleValue();
        // 剩余时间
        String remainTime = String.format("%.1f", remainHttpFileSize / 1024d / speed);
        if ("Infinity".equalsIgnoreCase(remainTime)) {
            remainTime = "-";
        }
        // 已下载大小
        String finished = String.format("%.2f", (downSize.doubleValue() - finishedSize.doubleValue()) / Constant.MB);

        String downloadInfo = String.format("已下载 %s mb / %s mb, 速度 %.2f kb/s, 剩余时间 %s s", finished, httpFileSize, speed, remainTime);

        // 不换行更新下载状态
        System.out.print("\r");
        System.out.print(downloadInfo);

    }
}
