package cn.alpha.demo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseCompatActivity;

/**
 * Created by Jungle on 2018/3/17.
 */

public class MessageBarActivity extends AppBaseCompatActivity {
    @BindView(R.id.btn_show)
    Button mBtnShow;

    private MessageBar mMessageBar;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_messagebar;
    }

    @Override
    public void initListener() {
        super.initListener();

        mMessageBar = new MessageBar(this);

        mBtnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageBar.show("Message #");
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {

    }
}
