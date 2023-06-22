package team.boars.server;

import com.google.gson.JsonObject;
import team.boars.config.Creator;
import team.boars.events.EventQueue;
import team.boars.level.LevelController;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static EventQueue eventQueue = new EventQueue();
    public static Creator creator = new Creator();
    public static LevelController controller = new LevelController(creator, 0);
    public static LinkedBlockingQueue<JsonObject> messageQueue = new LinkedBlockingQueue<>();
    public static Server server = new Server();

    public static void globalUpdate(float delta) {
        controller.update(delta);
        eventQueue.update();
    }

    public static void globalRestart(){
        eventQueue = new EventQueue();
        creator = new Creator();
        controller = new LevelController(creator, 0);
        messageQueue.clear();
        server.restart();
    }

    public static void main(String[] args) {
        eventQueue.subscribeState(controller.getLevelState());
        try {
            server.start(5555);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}