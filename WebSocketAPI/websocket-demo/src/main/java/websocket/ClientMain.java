package websocket;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;

public class ClientMain {

    public static void main(String[] args)
            throws Exception {
                
        WebSocketContainer container =
                ContainerProvider.getWebSocketContainer();

        Session session =
                container.connectToServer(
                        ChatClient.class,
                        URI.create(
                          "ws://localhost:8080/chat"
                        )
                );

        session.getAsyncRemote()
                .sendText("Hello WebSocket!");

        Thread.sleep(5000);

        session.close();
    }
}