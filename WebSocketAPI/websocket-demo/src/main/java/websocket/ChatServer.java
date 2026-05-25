package websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat")
public class ChatServer {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);

        System.out.println("Client connected: "
                + session.getId());

        broadcast("New client connected: "
                + session.getId());
    }

    @OnMessage
    public void onMessage(String message,
                          Session sender) {

        System.out.println("Message: " + message);

        broadcast("Client "
                + sender.getId()
                + ": " + message);
    }

    @OnClose
    public void onClose(Session session) {

        sessions.remove(session);

        System.out.println("Client disconnected: "
                + session.getId());

        broadcast("Client disconnected: "
                + session.getId());
    }

    private void broadcast(String message) {

        for (Session session : sessions) {

            if (session.isOpen()) {

                session.getAsyncRemote()
                        .sendText(message);
            }
        }
    }
}
