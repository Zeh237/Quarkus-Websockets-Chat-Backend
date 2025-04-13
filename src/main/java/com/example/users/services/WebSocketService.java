package com.example.users.services;

import com.example.users.dao.UserDao;
import com.example.users.model.User;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/{userId}")
@ApplicationScoped
public class WebSocketService {

    @Inject
    private UserDao userDao;

    private final Map<Long, Session> userSessions = new ConcurrentHashMap<>();

    private final Map<Long, LocalDateTime> lastSeenCache = new ConcurrentHashMap<>();

    private void broadcast(String message) {
        userSessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) {
        userSessions.put(userId, session);
        lastSeenCache.remove(userId);
        broadcast("User " + userId + " is online");
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session) {
        updateLastSeen(userId);
        userSessions.remove(userId);
        broadcast("User " + userId + " went offline");
    }

    @OnError
    public void handleError(@PathParam("userId") Long userId, Session session, Throwable throwable) {
        updateLastSeen(userId);
        userSessions.remove(userId);
        System.out.println("WebSocket error for user " + userId + ": " + throwable.getMessage());
    }

    private void updateLastSeen(Long userId) {
        lastSeenCache.put(userId, LocalDateTime.now());
    }

    @Scheduled(every = "5s")
    @Transactional
    public void persistLastSeen() {
        lastSeenCache.forEach((userId, timestamp) -> {
            User user = userDao.findById(userId);
            if (user != null) {
                user.setLastSeen(timestamp);
                userDao.persist(user);
            }
        });
        lastSeenCache.clear();
    }

    public boolean isUserOnline(Long userId) {
        return userSessions.containsKey(userId);
    }
}