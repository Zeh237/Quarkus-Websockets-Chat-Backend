package com.example.users.services;

import com.example.users.dao.UserDao;
import com.example.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{id}")
@ApplicationScoped
public class WebSocketService {

    @Inject
    private UserDao userDao;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result ->  {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    @OnOpen
    public void onOpen(@PathParam("id") Long id, Session session) {
        User user = userDao.findById(id);
        if (user == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "User not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        broadcast("Online");
        sessions.put(session.getId(), session);
    }

    @OnClose
    public void onClose(@PathParam("id") Long id, Session session) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setLastSeen(LocalDateTime.now());
            userDao.persist(user);
        }
        sessions.remove(session.getId());
        broadcast(user.getLastSeen().toString());
    }

    @OnError
    public void handleError(@PathParam("id") Long id, Session session, Throwable throwable) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setLastSeen(LocalDateTime.now());
            userDao.persist(user);
        }
        sessions.remove(session.getId());
        System.out.println("WebSocket Error: " + throwable.getMessage());
    }

    public boolean isUserOnline(Long id) {
        return sessions.values().stream().anyMatch(session -> {
            User user = userDao.findById(id);
            if (user == null) {
                return false;
            }
            return session.getId().equals(sessions.get(session.getId()).getId());
        });
    }


}
