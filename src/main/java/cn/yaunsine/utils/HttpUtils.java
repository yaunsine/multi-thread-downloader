package cn.yaunsine.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 编写http相关工具类
 */
public class HttpUtils {
    /**
     * 获取连接对象
     * @param url 文件的地址
     * @return
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
        // 向文件所在服务器发送标识信息
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36");


        return urlConnection;
    }

    /**
     * 获取下载文件的名字
     * @param url
     * @return
     */
    public static String getDownloadName(String url) {
        int index = url.lastIndexOf("/");
        return url.substring(index);
    }
}
