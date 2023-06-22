package team.boars.events;

public class LastEnemySpawnEvent implements StateEvent {
    @Override
    public void execute(StateHolder state) {
        state.markLastEnemySpawn();
    }
}
