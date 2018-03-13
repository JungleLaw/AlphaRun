package cn.alpha.eventbus.matchpolicy;

import java.util.List;

import cn.alpha.eventbus.EventType;

/**
 * @author mrsimple
 */
public interface MatchPolicy {
    List<EventType> findMatchEventTypes(EventType type, Object aEvent);
}
