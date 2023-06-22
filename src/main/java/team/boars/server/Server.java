package team.boars.server;

import com.google.gson.Gson;
import team.boars.events.ActorDeathEvent;
import team.boars.events.AlterCurrencyEvent;
import team.boars.events.ConstructBuildingEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import static team.boars.server.Main.*;

public class Server {
    private ServerSocket serverSocket;
    public static ConcurrentHashMap<Integer, OneClientSocket> serverList = new ConcurrentHashMap<>(); // список всех нитей

    public void start(int port) throws IOException {
        InetAddress addr = InetAddress.getByName("10.244.176.152");
        serverSocket = new ServerSocket(port, 50, addr);
        new ClientHandler(serverSocket).start();

        long last_time = System.currentTimeMillis();
        while (true) {
            if (serverList.size() > 0) {
                long time = System.currentTimeMillis();
                float delta = (float) (time - last_time) / 1000f;
                globalUpdate(delta);
                last_time = time;
            }
        }

    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    private static class ClientHandler extends Thread {
        ServerSocket serverSocket;

        ClientHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            int id = 0;
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    serverList.put(id, new OneClientSocket(clientSocket));
                    id++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class OneClientSocket extends Thread {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
//        private JsonObject json;

        public OneClientSocket(Socket socket) throws IOException {
            this.clientSocket = socket;
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
//            json = new JsonObject();
//            json.addProperty("cmd", "login");
//            json.addProperty("mem", "memelord");
//            System.out.println(json);
            this.start();
        }

        @Override
        public void run() {
            if (serverList.size() > 0) {
                new WriteMsg().start();
                new ReadMsg().start();
            }

//                in.close();
//                out.close();
//                clientSocket.close();
        }

        private class ReadMsg extends Thread {
            Gson gson = new Gson();

            @Override
            public void run() {
                while (true) {
                    try {
                        String line = in.readLine();
                        if (line != null) {
                            CommandObject command = gson.fromJson(line, CommandObject.class);
                            switch (command.getCmd()) {
                                case "constructBuilding": {
                                    eventQueue.addStateEvent(new ConstructBuildingEvent(command.getId(), command.getGridX(), command.getGridY()));
                                    break;
                                }
                                case "demolishBuilding": {
                                    eventQueue.addStateEvent(new ActorDeathEvent(command.getRefID(), false));
                                    int id = controller.getLevelState().getBuildings().get(command.getRefID()).getID();
                                    int demolitionReturn = creator.getBuildingConfig(id).demolitionCurrency;
                                    eventQueue.addStateEvent(new AlterCurrencyEvent(demolitionReturn));
                                    break;
                                }
                                case "upgradeBuilding": {
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        // нить отправляющая сообщения приходящие с консоли на сервер
        public class WriteMsg extends Thread {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (!messageQueue.isEmpty()) {
                            this.send(Main.messageQueue.take().toString());
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
        }

    }
}