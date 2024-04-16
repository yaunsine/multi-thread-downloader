package cn.yaunsine.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志工具类
 */
public class LogUtils {
    public static void info(String msg, Object... args) {
        print(msg, "INFO", args);
    }
    public static void error(String msg, Object... args) {
        print(msg, "ERROR", args);
    }
    public static void debug(String msg, Object... args) {
        print(msg, "DEBUG", args);
    }
    public static void warn(String msg, Object... args) {
        print(msg, "WARN", args);
    }
    private static void print(String msg, String level, Object ... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg.replace("{}", "%s"), args);
        }
        String name = Thread.currentThread().getName();
        String logLine = String.format("%s [%s] %s - %s", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")), name, level, msg);
        System.out.println(logLine);
    }
}
