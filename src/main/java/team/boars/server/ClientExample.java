package team.boars.server;

import java.io.IOException;

public class ClientExample {
    public static String ipAddr = "localhost";
    public static int port = 5555;

    /**
     * создание клиент-соединения с узананными адресом и номером порта
     * @param args
     */

    public static void main(String[] args) {
        new Client(ipAddr, port);
    }
}
