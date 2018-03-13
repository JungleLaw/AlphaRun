package cn.alpha.eventbus.matchpolicy;

import java.util.LinkedList;
import java.util.List;

import cn.alpha.eventbus.EventType;

public class StrictMatchPolicy implements MatchPolicy {

    @Override
    public List<EventType> findMatchEventTypes(EventType type, Object aEvent) {
        List<EventType> result = new LinkedList<EventType>();
        result.add(type);
        return result;
    }
}
