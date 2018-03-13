package cn.alpha.demo.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;

/**
 * Created by Law on 2017/4/16.
 */

public class WidgetsActivity extends AppBaseCompatActivity {
    @Override
    public int setContentViewLayout() {
        return R.layout.activity_widgets;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
    }

    @OnClick(R.id.btn_toast)
    void gotoToast(View view) {
        startActivity(new Intent(WidgetsActivity.this, ToastActivity.class));
    }
}
