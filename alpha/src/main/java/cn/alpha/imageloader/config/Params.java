package cn.alpha.imageloader.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import java.io.File;

/**
 * 图片加载参数
 * Created by Jungle on 2017/6/6.
 */

public class Params {
    /**
     * 上下文
     */
    public Context context;
    public boolean ignoreCertificateVerify = GlobalConfig.ignoreCertificateVerify;
    /**
     * 图片URL地址
     */
    public String url;
    /**
     * 图片文件
     */
    public File file = null;
    /**
     * 图片uri
     */
    public Uri uri;
    /**
     * 图片资源文件ID
     */
    public int resId;
    /**
     * 加载图片质量 0.0-1.0
     * -1，用于判断不做质量压缩
     */
    public float thumbnail = -1;
    /**
     * GIF
     */
    public boolean asGif = false;
    /**
     * BITMAP
     */
    public boolean asBitmap = false;
    /**
     * 显示的Imageview
     */
    public View target;
    /**
     * 加载BITMAP监听器
     */
    public BitmapListener bitmapListener;
    /**
     * 加载的view的宽度
     */
    public int width = -1;
    /**
     * 加载的view的高度
     */
    public int height = -1;
    /**
     * 裁剪模式，仅支持CENTER_CROP、FIT_CENTER，默认不设值，
     * 其他加载方式可以通过在XML设置Imageview的SCALETYPE属性,同时在此处使用默认值即不传值
     */
    public int mode;
    /**
     * 占位图资源ID
     */
    public int placeHolderResId;
    /**
     * 加载失败资源ID
     */
    public int errorResId;

    /**
     * 加载的裁剪方式
     */
    public Transform transform;

    public float getThumbnail() {
        return thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }

    public Uri getUri() {
        return uri;
    }

    public int getResId() {
        return resId;
    }

    public int getMode() {
        return mode;
    }

    public int getPlaceHolderResId() {
        return placeHolderResId;
    }

    public int getErrorResId() {
        return errorResId;
    }

    public View getTarget() {
        return target;
    }

    public BitmapListener getBitmapListener() {
        return bitmapListener;
    }

    public int getHeight() {
//        if (height <= 0) {
//            //先去imageview里取,如果为0,则赋值成matchparent
//            if (target != null) {
//                height = target.getMeasuredWidth();
//            }
//            if (height <= 0) {
//                height = GlobalConfig.getDisplayWidth();
//            }
//        }
        return height;
    }

    public int getWidth() {
//        if (width <= 0) {
//            //先去imageview里取,如果为0,则赋值成matchparent
//            if (target != null) {
//                width = target.getMeasuredWidth();
//            }
//            if (width <= 0) {
//                width = GlobalConfig.getDisplayWidth();
//            }
//        }
        return width;
    }

    public interface BitmapListener {
        void onSuccess(Bitmap bitmap);

        void onFail();
    }
}
