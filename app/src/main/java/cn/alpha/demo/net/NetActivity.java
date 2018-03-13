package cn.alpha.demo.net;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.demo.net.http.HttpActivity;

/**
 * Created by Law on 2017/4/16.
 */

public class NetActivity extends AppBaseCompatActivity {
    @Override
    public int setContentViewLayout() {
        return R.layout.activity_net;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_http)
    void gotoHttp(View view) {
        startActivity(new Intent(NetActivity.this, HttpActivity.class));
    }
}
