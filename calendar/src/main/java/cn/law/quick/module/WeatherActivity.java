package cn.law.quick.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import cn.alpha.net.http.Http;
import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.client.HttpParams;
import cn.law.quick.R;
import cn.law.quick.base.AppBaseCompatActivity;

/**
 * Created by Jungle on 2018/3/11.
 */

public class WeatherActivity extends AppBaseCompatActivity implements View.OnClickListener {
    private static final String WEATHER = "http://192.168.1.117:3000/weather";

    @BindView(R.id.et_city_name)
    EditText mEtCityName;
    @BindView(R.id.et_token)
    EditText mEtToken;
    @BindView(R.id.btn_get_weather)
    Button mBtnGetWeather;
    @BindView(R.id.tv_weather)
    TextView mTvWeather;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_weather;
    }

    @Override
    public void initListener() {
        mBtnGetWeather.setOnClickListener(this);
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        HttpParams params = new HttpParams();

        switch (v.getId()) {
            case R.id.btn_get_weather:
                String city = mEtCityName.getText().toString();
                if (TextUtils.isEmpty(city)) {
                    return;
                }
                params.put("city", city);
//                params.put("token", "FD6554159B7D3CE78BCB12359C03B902");
                params.put("token", mEtToken.getText().toString());
                params.put("platform", "app");
                Http.post("mine", WEATHER, params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        mTvWeather.setText(t);
                    }
                });
                break;
        }
    }
}
