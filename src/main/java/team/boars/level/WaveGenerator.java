package team.boars.level;

import team.boars.config.config_classes.LevelConfig;
import team.boars.config.config_classes.WaveConfig;
import team.boars.events.LastEnemySpawnEvent;
import team.boars.events.SpawnEnemyEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static team.boars.server.Main.eventQueue;

public class WaveGenerator {
    private final Queue<WaveConfig> waves;
    private WaveConfig activeWave;
    private boolean isActive;
    private boolean isWaveActive = false;
    private float waveTimer;
    private float enemyTimer;
    private final Random random;
    private int enemiesDepleted;
    private final int spawnerGridX, spawnerGridY;

    public WaveGenerator(LevelConfig levelConfig) {
        waves = new LinkedList<>(levelConfig.waves);
        isActive = false;
        random = new Random();
        spawnerGridX = (int) levelConfig.spawnerCoords.x;
        spawnerGridY = (int) levelConfig.spawnerCoords.y;
    }

    public void start() {
        activeWave = waves.remove();
        waveTimer = activeWave.waveDelay;
        enemyTimer = activeWave.enemyInterval;
        isWaveActive = false;
        isActive = true;
        enemiesDepleted = 0;
    }

    public void update(float delta) {
        if (!isActive) return;

        if (isWaveActive) {
            enemyTimer -= delta;
        } else {
            waveTimer -= delta;

        }

        if (enemyTimer <= 0 && isWaveActive) {
            int index = random.nextInt(activeWave.enemyTypes.size());
            int enemyID = activeWave.enemyTypes.get(index);
            eventQueue.addStateEvent(new SpawnEnemyEvent(enemyID, spawnerGridX, spawnerGridY));

            enemiesDepleted++;
            if (enemiesDepleted >= activeWave.enemyCount) {
                isWaveActive = false;
                if (waves.isEmpty()) {
                    isActive = false;
                    eventQueue.addStateEvent(new LastEnemySpawnEvent());
                    return;
                }
                activeWave = waves.remove();
                waveTimer = activeWave.waveDelay;
            }
            enemyTimer = activeWave.enemyInterval;
        } else if (waveTimer <= 0 && !isWaveActive) {
            isWaveActive = true;
            enemiesDepleted = 0;
            enemyTimer = 0;
        }
    }

    public float getWaveTimer() {
        if (isWaveActive){
            return enemyTimer;
        }
        else {
            return waveTimer;
        }
    }
}
