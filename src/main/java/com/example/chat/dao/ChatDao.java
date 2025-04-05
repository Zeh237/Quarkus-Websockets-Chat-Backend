package com.example.chat.dao;

import com.example.chat.model.Chat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ChatDao implements PanacheRepository<Chat> {
    public List<Chat> findBySenderId(Long senderId) {
        return find("sender.id", senderId).list();
    }

    public List<Chat> findByReceiverId(Long receiverId) {
        return find("receiver.id", receiverId).list();
    }

    public List<Chat> findBySenderAndReceiverId(Long senderId, Long receiverId) {
        return find("sender.id = ?1 and receiver.id = ?2", senderId, receiverId).list();
    }

    public List<Chat> findBySenderOrReceiverId(Long userId) {
        return find("sender.id = ?1 or receiver.id = ?1", userId).list();
    }

    public List<Chat> findBySenderOrReceiverIdAndChatId(Long userId, Long chatId) {
        return find("sender.id = ?1 or receiver.id = ?1 and id = ?2", userId, chatId).list();
    }

    public Chat findById(Long id) {
        return find("id", id).firstResult();
    }

}
