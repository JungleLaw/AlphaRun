package cn.alpha.demo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.BindView;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.imageloader.ImageLoader;
import cn.alpha.ui.view.ShapedImageView;

/**
 * Created by Law on 2017/4/16.
 */

public class ShapedImageViewActivity extends AppBaseCompatActivity {
//    public static final String IMAGE_URL = "http://tupian.qqjay.com/u/2013/1127/19_222949_6.jpg";
    public static final String IMAGE_URL = "http://192.168.1.117:3000/images/remember.jpg";

    @BindView(R.id.shape_img_round_rect)
    ShapedImageView mShapedImageViewRoundRect;
    @BindView(R.id.shape_img_circle)
    ShapedImageView mShapedImageViewCircle;

    @Override
    public int setContentViewLayout() {
        return R.layout.layout_shaped_imageview;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        ImageLoader.with(this).load(IMAGE_URL).centerCrop().placeHolder(R.drawable.alpha_thumb).into(mShapedImageViewRoundRect);
        ImageLoader.with(this).load(IMAGE_URL).centerCrop().placeHolder(R.drawable.alpha_thumb).into(mShapedImageViewCircle);
    }
}
