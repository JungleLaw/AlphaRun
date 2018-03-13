package cn.alpha.demo.others;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;
import cn.alpha.eventbus.EventBus;
import cn.alpha.eventbus.Subscriber;

/**
 * Created by Law on 2017/4/16.
 */

public class OthersActivity extends AppBaseCompatActivity {
    @BindView(R.id.btn_eventbus)
    Button mBtnEventBus;

    public static final String TAG_FOR_EVENTBUS = "eventbus_tag";

    private AlertDialog mEventbusDialog;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_others;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(OthersActivity.this).inflate(R.layout.dialog_eventbus, null);
        final EditText mEditText = (EditText) view.findViewById(R.id.ed_eventbus);
        mEventbusDialog = new AlertDialog.Builder(OthersActivity.this).setTitle("Eventbus").setView(view).setPositiveButton("Send Msg", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = mEditText.getText().toString();
                EventBus.getDefault().post(content, TAG_FOR_EVENTBUS);
            }
        }).create();
    }

    @Override
    public boolean activateEventBus() {
        return true;
    }

    @OnClick(R.id.btn_eventbus)
    void showEventbusDialog(View view) {
        if (!mEventbusDialog.isShowing())
            mEventbusDialog.show();
    }

//    @OnClick(R.id.btn_permission)
//    void gotoPermissionActivity(View view) {
//        startActivity(new Intent(this, PermissionActivity.class));
//    }

    @Subscriber(tag = TAG_FOR_EVENTBUS)
    void handlerEventBus(String msg) {
        mBtnEventBus.setText("Eventbus " + msg);
    }
}
