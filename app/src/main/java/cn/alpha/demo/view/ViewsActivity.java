package cn.alpha.demo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.OnClick;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;

/**
 * Created by Law on 2017/4/16.
 */

public class ViewsActivity extends AppBaseCompatActivity {
    @Override
    public int setContentViewLayout() {
        return R.layout.activity_views;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_sharped_img)
    void gotoSharpedImageViewActivity() {
        startActivity(new Intent(ViewsActivity.this, ShapedImageViewActivity.class));
    }

    @OnClick(R.id.btn_think_progressor)
    void gotoThinkProgressorActivity() {
        startActivity(new Intent(ViewsActivity.this, ThinkProgressorActivity.class));
    }

    @OnClick(R.id.btn_labelview)
    void gotoLabelViewActivity() {
        startActivity(new Intent(ViewsActivity.this, LabelViewActivity.class));
    }
}
