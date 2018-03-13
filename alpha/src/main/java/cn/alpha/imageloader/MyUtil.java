package cn.alpha.imageloader;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.alpha.imageloader.config.GlobalConfig;
import cn.alpha.imageloader.config.Params;
import cn.alpha.imageloader.config.SingleConfig;
import okhttp3.OkHttpClient;


/**
 * 工具类
 * Created by Administrator on 2017/3/15 0015.
 */
public class MyUtil {

    /**
     * 获取监听器代理
     * @param listener BitmapListener实现
     * @return
     */
    public static Params.BitmapListener getBitmapListenerProxy(final Params.BitmapListener listener){
        return (Params.BitmapListener) Proxy.newProxyInstance(SingleConfig.class.getClassLoader(),
                listener.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {

                runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Object object=  method.invoke(listener,args);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });



                return null;
            }
        });

    }


    /**
     * 主线程运行Runnable
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable){
        GlobalConfig.getMainHandler().post(runnable);
    }

    /**
     * 等比压缩（宽高等比缩放）
     * @param bitmap
     * @param needRecycle
     * @param targetWidth
     * @param targeHeight
     * @return
     */
    public static Bitmap compressBitmap(Bitmap bitmap, boolean needRecycle, int targetWidth, int targeHeight) {
        float sourceWidth = bitmap.getWidth();
        float sourceHeight = bitmap.getHeight();

        float scaleWidth = targetWidth / sourceWidth;
        float scaleHeight = targeHeight / sourceHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight); //长和宽放大缩小的比例
        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (needRecycle) {
            bitmap.recycle();
        }
        bitmap = bm;
        return bitmap;
    }


    /**
     * 获取文件类型
     * @param file
     * @return
     */
    public static String getRealType(File file){
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] b = new byte[4];
            try {
                is.read(b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            String type =  bytesToHexString(b).toUpperCase();
            if(type.contains("FFD8FF")){
                return "jpg";
            }else if(type.contains("89504E47")){
                return "png";
            }else if(type.contains("47494638")){
                return "gif";
            }else if(type.contains("49492A00")){
                return "tif";
            }else if(type.contains("424D")){
                return "bmp";
            }
            return type;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    /**
     * 字节数组转String
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 获取Http客户端实现
     * @param ignoreCertificateVerify 是否忽略验证
     * @return
     */
    public static OkHttpClient getClient(boolean ignoreCertificateVerify){
        if(ignoreCertificateVerify){
            return getAllPassClient();
        }else {
            return getNormalClient();
        }
    }

    /**
     * 不校验证书
     * @return
     */
    private static OkHttpClient getAllPassClient() {

        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[]{};
                return x509Certificates;
                // return null;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");

            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // sslSocketFactory和hostnameVerifier代码与httpsUtil中一模一样,只有这里不一样,但下面能行,这里就不行,见鬼了
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(DO_NOT_VERIFY)
                .readTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS) //设置超时
                .build();

        return client;
    }

    /**
     *
     * @return 返回一般Http客户端
     */
    private static OkHttpClient getNormalClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                //.sslSocketFactory(sslContext.getSocketFactory())
                //.hostnameVerifier(DO_NOT_VERIFY)
                .readTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).writeTimeout(0, TimeUnit.SECONDS) //设置超时
                .build();
        return client;
    }


    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    public static long getFolderSize(File file)  {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath filePath
     * @param deleteThisPath deleteThisPath
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getCacheSize() {
        File dir = new File(ImageLoader.context.getCacheDir(), GlobalConfig.cacheFolderName);
        if(dir!=null && dir.exists()){
            return MyUtil.getFolderSize(dir);
        }else {
            return 0;
        }

    }

}
