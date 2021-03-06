package cn.alpha.protocol.interfaces;

import android.support.annotation.Nullable;
import android.view.View;

public interface IFragmentProtocol {
	public int setContentViewLayout();

	public abstract void initVariables();

	public abstract void initViews(@Nullable View view);

	public abstract void initListener();

	public abstract void loadData();

	public abstract void destroyTask();

	public abstract boolean activateEventBus();
}
