package cn.alpha.demo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.BindView;
import cn.alpha.demo.R;
import cn.alpha.demo.base.AppBaseActivity;
import cn.alpha.ui.view.LabelView;

/**
 * Created by Law on 2017/4/16.
 */

public class LabelViewActivity extends AppBaseActivity {
    @BindView(R.id.labelview)
    protected LabelView mLabelView;

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_labelview;
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        mLabelView.setLabel("label");
    }
}
