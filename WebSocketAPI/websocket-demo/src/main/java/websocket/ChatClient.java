package websocket;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnMessage;

@ClientEndpoint
public class ChatClient {

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }
}

