package cn.alpha.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * <p>
 * Created by Law on 2017/2/24.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static String VERSION = "Unknown";
    /**
     * android系统版本
     */
    private static String ANDROID = Build.VERSION.RELEASE;
    /**
     * 机型
     */
    private static String MODEL = Build.MODEL;
    /**
     * 手机牌子
     */
    private static String MANUFACTURER = Build.MANUFACTURER;
    private static CrashBuilder mBuilder;
    private UncaughtExceptionHandler mPrevious;
    private boolean isAppend;
    private boolean isSimple;

    private CrashHandler() {
        mPrevious = Thread.currentThread().getUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(this);
    }

    public static CrashHandler init(Context context, String dirName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION = info.versionName + info.versionCode;
            mBuilder = CrashBuilder.build(context, dirName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CrashHandler();
    }

    public static String formatNumber(int value) {
        return new DecimalFormat("00").format(value);
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        return calendar.get(Calendar.YEAR) + "-" + formatNumber((calendar.get(Calendar.MONTH) + 1)) + "-" + formatNumber(calendar.get(Calendar.DAY_OF_MONTH)) + "  " + formatNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + formatNumber(calendar.get(Calendar.MINUTE));
    }

    /**
     * 获取log 日志路径
     */
    public static String getLogFilePath() {
        if (mBuilder == null)
            return "Unknown";
        else
            return mBuilder.getCrash_log();
    }

    /**
     * 获取 LOG 记录的内容
     */
    public static String getLogContent() {
        if (TextUtils.isEmpty(getLogFilePath()))
            return null;

        File file = new File(getLogFilePath());
        if (file.exists() && file.isFile()) {
            BufferedReader bis = null;
            try {
                bis = new BufferedReader(new FileReader(file));
                String buffer = null;
                StringBuilder sb = new StringBuilder();
                while ((buffer = bis.readLine()) != null) {
                    sb.append(buffer);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null)
                        bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param isSimple 是否为简单的日志记录模式
     */
    public void setSimple(boolean isSimple) {
        this.isSimple = isSimple;
    }

    /**
     * @param isAppend 是否为日志追加模式
     */
    public CrashHandler setAppend(boolean isAppend) {
        this.isAppend = isAppend;
        return this;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        File f = new File(mBuilder.getCrash_log());
        if (f.exists()) {
            if (!isAppend)
                f.delete();
        } else {
            try {
                new File(mBuilder.getCrash_dir()).mkdirs();
                f.createNewFile();
            } catch (Exception e) {
                return;
            }
        }

        PrintWriter p;
        try {
            p = new PrintWriter(new FileWriter(f, true));
        } catch (Exception e) {
            return;
        }
        p.write("\n*************---------Crash  Log  Head ------------****************\n\n");
        p.write("Happend Time: " + getCurrentDate() + "\n");
        p.write("Android Version: " + ANDROID + "\n");
        p.write("Device Model: " + MODEL + "\n");
        p.write("Device Manufacturer: " + MANUFACTURER + "\n");
        p.write("App Version: v" + VERSION + "\n\n");
        p.write("*************---------Crash  Log  Head ------------****************\n\n");
        if (!isSimple)
            throwable.printStackTrace(p);
        else {
            p.write(throwable.getLocalizedMessage() + "\n");
        }
        p.close();
        try {
            new File(mBuilder.getCrash_tag()).createNewFile();
        } catch (Exception e) {
            return;
        }

        if (mPrevious != null) {
            mPrevious.uncaughtException(thread, throwable);
        }
    }

    public static class CrashBuilder {
        private String Crash_dir;

        public CrashBuilder(Context context, String dirName) {
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                this.Crash_dir = context.getCacheDir().getPath() + File.separator + dirName;
            } else
                this.Crash_dir = Environment.getExternalStorageDirectory().getPath() + File.separator + dirName;
        }

        public static CrashBuilder build(Context context, String dirName) {
            return new CrashBuilder(context, dirName);
        }

        public String getCrash_dir() {
            return Crash_dir;
        }

        public String getCrash_log() {
            return getCrash_dir() + File.separator + "CrashRecord.log";
        }

        public String getCrash_tag() {

            return getCrash_dir() + File.separator + ".Crashed";
        }

        @Override
        public String toString() {
            return "CrashBuilder [dir path: " + getCrash_dir() + "-- log path:" + getCrash_log() + "-- tag path:" + getCrash_tag() + "]";
        }
    }
}