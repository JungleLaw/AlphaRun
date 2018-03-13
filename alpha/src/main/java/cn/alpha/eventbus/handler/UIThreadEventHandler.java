package cn.alpha.eventbus.handler;

import android.os.Handler;
import android.os.Looper;

import cn.alpha.eventbus.Subscription;

/**
 * 事件处理在UI线程,通过Handler将事件处理post到UI线程的消息队列
 *
 * @author mrsimple
 */
public class UIThreadEventHandler implements EventHandler {

    /**
     *
     */
    DefaultEventHandler mEventHandler = new DefaultEventHandler();
    /**
     * ui handler
     */
    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    /**
     * @param subscription
     * @param event
     */
    public void handleEvent(final Subscription subscription, final Object event) {
        mUIHandler.post(new Runnable() {

            @Override
            public void run() {
                mEventHandler.handleEvent(subscription, event);
            }
        });
    }

}
