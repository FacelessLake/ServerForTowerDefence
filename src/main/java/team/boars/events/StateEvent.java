package team.boars.events;

public interface StateEvent {
    void execute(StateHolder state);
}
