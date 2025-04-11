package com.example.chat.services;

import com.example.chat.dao.ChatDao;
import com.example.chat.dao.MessageDao;
import com.example.chat.dto.MessageDto;
import com.example.users.dao.UserDao;
import com.example.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/wsm/{userId}")
@ApplicationScoped
public class WebsocketService {

    @Inject
    private UserDao userDao;

    @Inject
    private MessageDao messageDao;

    @Inject
    private ChatDao chatDao;

    @Inject
    private ChatService chatService;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void broadcastToUser(Long userId, String message) {
        boolean userOnline = sessions.values().stream()
                .anyMatch(session -> session.getUserProperties().get("userId").equals(userId));

        if (!userOnline) {
            System.out.println("User with ID " + userId + " is not online. Message not sent.");
            return;
        }

        sessions.values().stream()
                .filter(session -> session.getUserProperties().get("userId").equals(userId))
                .forEach(s -> {
                    s.getAsyncRemote().sendObject(message, result -> {
                        if (result.getException() != null) {
                            System.out.println("Unable to send message: " + result.getException());
                        }
                    });
                });
    }

    @OnOpen
    public void onOpen(@PathParam("userId") Long id, Session session) {
        User user = userDao.findById(id);
        if (user == null) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "User not found"));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return;
        }

        session.getUserProperties().put("userId", id);
        sessions.put(session.getId(), session);
        broadcastToUser(user.getId(), "User " + user.getUsername() + " has connected.");
    }

//    @OnMessage
//    public void sendMessage(MessageDto messageDto, Session session) {
//        User sender = userDao.findById(messageDto.getSenderId());
//        if (sender == null) {
//            try {
//                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "User not found"));
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage());
//            }
//            return;
//        }
//        try {
//            boolean userOnline = sessions.values().stream()
//                    .anyMatch(s -> s.getUserProperties().get("userId").equals(messageDto.getReceiverId()));
//
//            if (userOnline) {
//                broadcastToUser(messageDto.getReceiverId(), messageDto.getContent());
//            } else {
//                System.out.println("User with ID " + messageDto.getReceiverId() + " is not online. Saving message for later retrieval.");
//            }
//
//            chatService.sendMessage(messageDto);
//
//            sessions.put(session.getId(), session);
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @OnClose
    public void onClose(@PathParam("userId") Long id, Session session) {
        User user = userDao.findById(id);
        if (user != null) {
            user.setLastSeen(LocalDateTime.now());
            userDao.persist(user);
        }
        sessions.remove(session.getId());
        broadcastToUser(user.getId(), "User " + user.getUsername() + " has disconnected.");
    }

    @OnError
    public void handleError(@PathParam("userId") Long id, Session session, Throwable throwable) {
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
            return session.getUserProperties().get("userId").equals(user.getId());
        });
    }


}
