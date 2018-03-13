package cn.law.calendar.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import cn.law.calendar.R;
import cn.law.calendar.base.AppBaseCompatActivity;
import cn.law.calendar.module.hybird.HybirdActivity;
import cn.law.calendar.module.sign.SignInActivity;

public class MainActivity extends AppBaseCompatActivity implements View.OnClickListener {
    @BindView(R.id.btn_sign)
    public Button mBtnSign;
    @BindView(R.id.btn_weather)
    public Button mBtnWeather;
    @BindView(R.id.btn_hybird)
    public Button mBtnHybird;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initListener() {
        mBtnSign.setOnClickListener(this);
        mBtnWeather.setOnClickListener(this);
        mBtnHybird.setOnClickListener(this);
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public int[] loadExitAnimation() {
        return null;
    }

    @Override
    public int[] loadEntryAnimation() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.btn_weather:
                startActivity(new Intent(this, WeatherActivity.class));
                break;
            case R.id.btn_hybird:
                startActivity(new Intent(this, HybirdActivity.class));
                break;
        }
    }
}
