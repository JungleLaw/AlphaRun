package cn.alpha.protocol.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;

public interface IActivityProtocol {
	public int setContentViewLayout();

	public abstract void initVariables();

	public abstract void initViews();

	public abstract void initListener();

	public abstract void loadData(@Nullable Bundle savedInstanceState);

	public abstract void destroyTask();

	public abstract int[] loadEntryAnimation();

	public abstract int[] loadExitAnimation();

	public abstract boolean activateEventBus();
}
