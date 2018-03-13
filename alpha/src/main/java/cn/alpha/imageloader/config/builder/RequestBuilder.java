package cn.alpha.imageloader.config.builder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.telephony.TelephonyManager;

import java.io.File;

import cn.alpha.imageloader.config.Params;

/**
 * 设置加载地址或文件或其他
 * Created by Jungle on 2017/10/6.
 */

public class RequestBuilder {
    private Params params;

    public RequestBuilder(Params params) {
        this.params = params;
    }

    /**
     * @param resId 资源ID
     * @return
     */
    public ScaleBuilder load(@DrawableRes int resId) {
        params.resId = resId;
        return new ScaleBuilder(params);
    }

    /**
     * @param url 网络URL
     * @return
     */
    public ScaleBuilder load(String url) {
        params.url = url;
        return new ScaleBuilder(params);
    }

    /**
     * @param file 文件
     * @return
     */
    public ScaleBuilder load(File file) {
        params.file = file;
        return new ScaleBuilder(params);
    }

    /**
     * @param uri 文件URI
     * @return
     */
    public ScaleBuilder load(Uri uri) {
        params.uri = uri;
        return new ScaleBuilder(params);
    }


    /**
     * 获取网络情况
     *
     * @param context
     * @return <ul>
     * <li>-1.无网络</li>
     * <li>0.其他</li>
     * <li>1.wifi</li>
     * <li>2.2G</li>
     * <li>3.3G</li>
     * <li>4.4G</li>
     * </ul>
     * @author ben75(http://stackoverflow.com/users/1818045/ben75)
     */
    public static int getNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        // 未连接
        if (info == null || !info.isConnected())
            // 无网络
            return -1;
        if (info.getType() == ConnectivityManager.TYPE_WIFI)
            // wifi
            return 1;
        if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkType = info.getSubtype();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    // 2G
                    return 2;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    // 3G
                    return 3;
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                    // 4G
                    return 4;
                default:
                    // 其他
                    return 0;
            }
        }
        // 其他
        return 0;
    }
}
