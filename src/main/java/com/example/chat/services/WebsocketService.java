package com.example.chat.services;
import com.example.chat.dto.MessageDto;
import com.example.users.dao.UserDao;
import com.example.users.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.quarkus.arc.impl.UncaughtExceptions.LOGGER;

@ServerEndpoint("/wsm/{userId}")
@ApplicationScoped
public class WebsocketService {

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

    public void sendMessageToUser(Long userId, MessageDto messageDto) {
        try {
            Session session = userSessions.get(userId);
            if (session != null && session.isOpen()) {
                // Create a lightweight map with only the needed fields
                Map<String, Object> messagePayload = new HashMap<>();
                messagePayload.put("id", messageDto.getId());
                messagePayload.put("content", messageDto.getContent());
                messagePayload.put("senderId", messageDto.getSenderId());
                messagePayload.put("receiverId", messageDto.getReceiverId());
                messagePayload.put("chatId", messageDto.getChatId());
                // Convert LocalDateTime to ISO-8601 string format
                messagePayload.put("createdAt", messageDto.getCreatedAt() != null
                        ? messageDto.getCreatedAt().toString()
                        : null);

                ObjectMapper mapper = new ObjectMapper();
                String jsonMessage = mapper.writeValueAsString(messagePayload);

                session.getAsyncRemote().sendText(jsonMessage, result -> {
                    if (result.getException() != null) {
                        LOGGER.error("Failed to send WebSocket message", result.getException());
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error sending WebSocket message", e);
        }
    }
}