package team.boars.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import team.boars.events.ActorDeathEvent;
import team.boars.events.AlterCurrencyEvent;
import team.boars.events.ConstructBuildingEvent;
import team.boars.events.UpgradeBuildingEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static team.boars.server.Main.*;

public class Server {
    private ServerSocket serverSocket;
    private static final ConcurrentHashMap<Integer, OneClientSocket> clients = new ConcurrentHashMap<>(); // список всех нитей
    private ClientHandler clientHandler;
    private static long last_time;
    private static final AtomicInteger restartCounter = new AtomicInteger(0);

    public void start(int port) throws IOException {
        InetAddress addr = InetAddress.getByName("10.244.176.152");
        serverSocket = new ServerSocket(port, 50, addr);
        clientHandler = new ClientHandler(serverSocket);
        clientHandler.start();

        while (true) {
            if (clients.size() > 1) {
                long time = System.currentTimeMillis();
                float delta = (float) (time - last_time) / 1000f;
                globalUpdate(delta);
                last_time = time;
            }
            if(restartCounter.get() >= 2){
                restartCounter.set(0);
                globalRestart();
            }
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public void restart() {
        clientHandler.restart();
    }

    private static class ClientHandler extends Thread {
        ServerSocket serverSocket;
        int id;

        ClientHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
            id = 0;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    id++;
                    last_time = System.currentTimeMillis();
                    clients.put(id, new OneClientSocket(clientSocket, id));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void restart() {
            for (int i = id; i > 0; i--) {
                clients.remove(i).stopSocket();
            }
            id = 0;
        }
    }

    private static class OneClientSocket extends Thread {
        private final Socket clientSocket;
        private final PrintWriter out;
        private final BufferedReader in;
        private final AtomicBoolean running;
        private final WriteMsg writerThread;
        private final ReadMsg readerThread;
        private final int id;

        public OneClientSocket(Socket socket, Integer id) throws IOException {
            clientSocket = socket;
            this.id = id;
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            writerThread = new WriteMsg();
            readerThread = new ReadMsg();
            running = new AtomicBoolean(false);
            this.start();
        }

        @Override
        public void run() {
                writerThread.start();
                readerThread.start();
                running.set(true);
                JsonObject json = new JsonObject();
                json.addProperty("cmd", "login");
                json.addProperty("id", id);
            try {
                messageQueue.put(json);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            if (clients.size() > 1) {
                while (running.get()) {

                }
//            }
        }

        public void stopSocket() {
            writerThread.stopWriting();
            readerThread.stopReading();
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            running.set(false);
        }

        private class ReadMsg extends Thread {
            Gson gson = new Gson();
            private final AtomicBoolean running = new AtomicBoolean(false);

            @Override
            public void run() {
                running.set(true);
                while (running.get()) {
                    try {
                        String line = in.readLine();
                        if (line != null) {
                            CommandObject command = gson.fromJson(line, CommandObject.class);
                            switch (command.getCmd()) {
                                case "constructBuilding" ->
                                        eventQueue.addStateEvent(new ConstructBuildingEvent(command.getId(), command.getGridX(), command.getGridY()));
                                case "demolishBuilding" -> {
                                    int id = controller.getLevelState().getBuildings().get(command.getRefID()).getID();
                                    int demolitionReturn = creator.getBuildingConfig(id).demolitionCurrency;
                                    eventQueue.addStateEvent(new ActorDeathEvent(command.getRefID(), false));
                                    eventQueue.addStateEvent(new AlterCurrencyEvent(demolitionReturn));
                                }
                                case "upgradeBuilding" ->
                                        eventQueue.addStateEvent(new UpgradeBuildingEvent(command.getRefID(), command.getUpgradeId()));
                            }
                        }
                    } catch (IOException e) {
//                        System.out.println("Player left!");
                        throw new RuntimeException(e);
                    }
                }
            }

            public void stopReading() {
                running.set(false);
            }
        }

        // нить отправляющая сообщения приходящие с консоли на сервер
        public class WriteMsg extends Thread {
            private final AtomicBoolean running = new AtomicBoolean(false);
            @Override
            public void run() {
                running.set(true);
                try {
                    while (running.get()) {
                        if (!messageQueue.isEmpty()) {
                            JsonObject json = messageQueue.take();
                            this.send(json.toString());
                            if (json.get("cmd").getAsString().equals("endGame")) {
                                restartCounter.incrementAndGet();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            private void send(String msg) {
                out.write(msg + "\n");
                out.flush();
            }

            public void stopWriting() {
                running.set(false);
            }
        }

    }
}