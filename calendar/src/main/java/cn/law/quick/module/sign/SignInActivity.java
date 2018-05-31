package cn.law.quick.module.sign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import cn.alpha.net.http.Http;
import cn.alpha.net.http.HttpCallback;
import cn.alpha.net.http.client.HttpParams;
import cn.alpha.utils.Logger;
import cn.law.quick.R;
import cn.law.quick.base.AppBaseCompatActivity;

/**
 * Created by Jungle on 2018/3/10.
 */

public class SignInActivity extends AppBaseCompatActivity implements View.OnClickListener {
    private static final String SIGN_UP = "http://192.168.1.117:3000/signup";
    private static final String SIGN_IN = "http://192.168.1.117:3000/signin";

    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.btn_signup)
    Button mBtnSignUp;
    @BindView(R.id.et_signin_username)
    EditText mEtSignInUsername;
    @BindView(R.id.et_signin_password)
    EditText mEtSignInPassword;
    @BindView(R.id.btn_signin)
    Button mBtnSignIn;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_signin;
    }

    @Override
    public void initListener() {
        mBtnSignUp.setOnClickListener(this);
        mBtnSignIn.setOnClickListener(this);
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void destroyTask() {
        Http.cancel("mine");
    }

    @Override
    public void onClick(View v) {
        HttpParams params = new HttpParams();

        switch (v.getId()) {
            case R.id.btn_signup:
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Logger.i("empty");
                    return;
                }
                params.put("username", username);
                params.put("password", password);
                params.put("platform", "Android");
                Logger.i("alpha-http", "loadData");
                Http.post("mine", SIGN_UP, params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        Logger.i("alpha-http", t);
                    }
                });
                break;
            case R.id.btn_signin:
                String signinusername = mEtSignInUsername.getText().toString();
                String signinpassword = mEtSignInPassword.getText().toString();
                if (TextUtils.isEmpty(signinusername) || TextUtils.isEmpty(signinpassword)) {
                    Logger.i("empty");
                    return;
                }
                params.put("username", signinusername);
                params.put("password", signinpassword);
                Logger.i("alpha-http", "loadData");
                Http.post("mine", SIGN_IN, params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        Logger.i("alpha-http", t);
                    }
                });
                break;
        }
    }
}
