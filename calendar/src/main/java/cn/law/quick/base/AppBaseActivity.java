package cn.law.quick.base;

import cn.alpha.protocol.ProtocolActivity;
import cn.law.quick.R;

/**
 * Created by Law on 2017/3/28.
 */

public abstract class AppBaseActivity extends ProtocolActivity {

    public static final int[] entryAnims = {R.anim.slide_entry_right, R.anim.slide_exit_left};
    public static final int[] exitAnims = {R.anim.slide_entry_left, R.anim.slide_exit_right};


    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void destroyTask() {

    }

    @Override
    public int[] loadExitAnimation() {
        return exitAnims;
    }


    @Override
    public int[] loadEntryAnimation() {
        return entryAnims;
    }
}
