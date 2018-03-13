package cn.alpha.demo.imageloader;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import butterknife.BindView;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.demo.jni.Host;
import cn.alpha.imageloader.ImageLoader;
import cn.alpha.utils.Logger;

/**
 * Created by Jungle on 2017/10/8.
 */

public class ImageLoaderActivity extends AppBaseCompatActivity {
    @BindView(R.id.image_1)
    ImageView imageView1;
    @BindView(R.id.image_2)
    ImageView imageView2;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_imageloader;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        Logger.e(Host.URL_IMG_PREFIX);
        ImageLoader.with(this).load(Host.URL_IMG_PREFIX + "images/remember.jpg").asBitmap().into(imageView1);
        ImageLoader.with(this).load(Host.URL_IMG_PREFIX + "images/remember.jpg").asBitmap().into(imageView2);
    }
}
