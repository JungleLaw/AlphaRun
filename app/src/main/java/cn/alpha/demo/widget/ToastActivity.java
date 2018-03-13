package cn.alpha.demo.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import butterknife.OnClick;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.ui.widget.ThinkToast;

/**
 * Created by Law on 2017/4/16.
 */

public class ToastActivity extends AppBaseCompatActivity {
    @Override
    public int setContentViewLayout() {
        return R.layout.activity_toast;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_toast_default, R.id.btn_toast_error, R.id.btn_toast_suc, R.id.btn_toast_help, R.id.btn_toast_warning, R.id.btn_toast_info})
    void toast(View view) {
        switch (view.getId()) {
            case R.id.btn_toast_suc:
                ThinkToast.showToast(ToastActivity.this, "suc", ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                break;
            case R.id.btn_toast_warning:
                ThinkToast.showToast(ToastActivity.this, "warning", ThinkToast.LENGTH_SHORT, ThinkToast.WARNING);
                break;
            case R.id.btn_toast_info:
                ThinkToast.showToast(ToastActivity.this, "info", ThinkToast.LENGTH_SHORT, ThinkToast.INFO);
                break;
            case R.id.btn_toast_error:
                ThinkToast.showToast(ToastActivity.this, "error", ThinkToast.LENGTH_SHORT, ThinkToast.ERROR);
                break;
            case R.id.btn_toast_help:
                ThinkToast.showToast(ToastActivity.this, "help", ThinkToast.LENGTH_SHORT, ThinkToast.HELP);
                break;
            case R.id.btn_toast_default:
                ThinkToast.showToast(ToastActivity.this, "default", ThinkToast.LENGTH_SHORT, ThinkToast.DEFAULT);
                break;
        }
    }
}
