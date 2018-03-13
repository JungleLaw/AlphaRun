package cn.alpha.eventbus.handler;

import java.lang.reflect.InvocationTargetException;

import cn.alpha.eventbus.Subscription;

/**
 * 事件在哪个线程post,事件的接收就在哪个线程
 * 
 * @author mrsimple
 */
public class DefaultEventHandler implements EventHandler {
    /**
     * handle the event
     * 
     * @param subscription
     * @param event
     */
    @Override
    public void handleEvent(Subscription subscription, Object event) {
        if (subscription == null
                || subscription.subscriber.get() == null) {
            return;
        }
        try {
            // 执行
            subscription.targetMethod.invoke(subscription.subscriber.get(), event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
