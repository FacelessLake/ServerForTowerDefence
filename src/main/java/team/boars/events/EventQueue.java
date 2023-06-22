package team.boars.events;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EventQueue {
    private final Queue<StateEvent> stateEvents;
    private final List<StateHolder> stateSubscribers;

    public EventQueue() {
        stateEvents = new LinkedList<>();
        stateSubscribers = new LinkedList<>();
    }

    public void update() {
        while (!stateEvents.isEmpty()) {
            StateEvent event = stateEvents.remove();
            notifyStates(event);
        }
    }

    public void addStateEvent(StateEvent event) { stateEvents.add(event); }

    public void subscribeState(StateHolder state) {
        stateSubscribers.add(state);
    }

    private void notifyStates(StateEvent event) {
        for (StateHolder state : stateSubscribers) {
            event.execute(state);
        }
    }
}
