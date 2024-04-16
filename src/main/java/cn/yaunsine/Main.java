package cn.yaunsine;

import cn.yaunsine.core.Downloader;
import cn.yaunsine.core.DownloaderService;
import cn.yaunsine.utils.LogUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url = "";
        if (args == null || args.length == 0) {
            for (;;) {
                LogUtils.info("请输入下载连接：");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if (url != null) {
                    break;
                }
            }
        } else {
            url = args[0];
        }
        DownloaderService downloader = new Downloader();
        downloader.download(url);
        // https://dldir1v6.qq.com/weixin/Windows/WeChatSetup.exe
    }
}