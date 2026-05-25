package websocket;

import org.glassfish.tyrus.server.Server;

public class ServerMain {

    public static void main(String[] args)
            throws Exception {

        Server server = new Server(
                "localhost",
                8080,
                "/",
                null,
                ChatServer.class
        );

        server.start();

        System.out.println("WebSocket server started");

        Thread.currentThread().join();
    }
}
