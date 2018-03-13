package cn.alpha.protocol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Unbinder;
import cn.alpha.eventbus.EventBus;
import cn.alpha.kit.KnifeKit;
import cn.alpha.protocol.interfaces.IFragmentProtocol;
//import com.alphax.rxbus.RxBus;


public abstract class ProtocolFragmentV4 extends Fragment implements IFragmentProtocol {
    private Unbinder mUnbinder;
    private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initVariables();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (setContentViewLayout() <= 0) {
            throw new IllegalArgumentException("contentview layout id must greater than 0");
        }
        if (mRootView == null) {
            mRootView = inflater.inflate(setContentViewLayout(), null);
            mUnbinder = KnifeKit.bind(this);
        } else {
            ViewGroup mViewGroup = (ViewGroup) mRootView.getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(mRootView);
            }
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews(mRootView);
        initListener();
        loadData();
        if (activateEventBus()) {
            EventBus.getDefault().register(getActivity());
        }
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        destroyTask();
        if (activateEventBus()) {
            EventBus.getDefault().unregister(getActivity());
        }
        KnifeKit.unbind(mUnbinder);
        super.onDestroy();
    }

    @Override
    public boolean activateEventBus() {
        return false;
    }
}
