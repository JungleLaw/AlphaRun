package cn.alpha.protocol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.Unbinder;
import cn.alpha.eventbus.EventBus;
import cn.alpha.kit.KnifeKit;
import cn.alpha.protocol.interfaces.IActivityProtocol;


public abstract class ProtocolAppCompatActivity extends AppCompatActivity implements IActivityProtocol {
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        if (setContentViewLayout() <= 0) {
            throw new IllegalArgumentException("contentview layout id must greater than 0");
        }
        setContentView(setContentViewLayout());
        mUnbinder = KnifeKit.bind(this);
        initVariables();
        initViews();
        initListener();
        loadData(savedInstanceState);
        if (activateEventBus()) {
            EventBus.getDefault().register(this);
        }
        if (loadEntryAnimation() != null) {
            overridePendingTransition(loadEntryAnimation()[0], loadEntryAnimation()[1]);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        destroyTask();
        if (activateEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        KnifeKit.unbind(mUnbinder);
        super.onDestroy();
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        if (loadExitAnimation() != null) {
            overridePendingTransition(loadExitAnimation()[0], loadExitAnimation()[1]);
        }
    }

    @Override
    public boolean activateEventBus() {
        return false;
    }
}
