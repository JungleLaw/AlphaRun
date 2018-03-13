package cn.alpha.demo.jni;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;

/**
 * Created by Jungle on 2017/10/7.
 */

public class JNIActivity extends AppBaseCompatActivity {

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_jni;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        ((TextView) findViewById(R.id.text)).setText(Host.URL_SERVER_PREFIX);
    }
}
