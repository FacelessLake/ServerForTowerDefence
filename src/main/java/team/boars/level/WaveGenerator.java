package team.boars.level;

import team.boars.config.LevelConfig;
import team.boars.config.WaveConfig;
//import com.mygdx.towerdefence.events.SpawnEnemyEvent;
//import com.mygdx.towerdefence.framework.screens.LevelScreen;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
//            LevelScreen.eventQueue.addStateEvent(new SpawnEnemyEvent(enemyID, spawnerGridX, spawnerGridY));

            enemiesDepleted++;
            if (enemiesDepleted >= activeWave.enemyCount) {
                isWaveActive = false;
                if (waves.isEmpty()) {
                    isActive = false;
                    return;
                }
                activeWave = waves.remove();
                waveTimer = activeWave.waveDelay;
            }
            enemyTimer = activeWave.enemyInterval;
        } else if (waveTimer <= 0 && !isWaveActive) {
            isWaveActive = true;
            enemiesDepleted = 0;
            enemyTimer = activeWave.enemyInterval;
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
