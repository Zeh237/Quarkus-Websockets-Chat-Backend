package com.example.chat.dao;

import com.example.chat.model.Chat;
import com.example.chat.model.Message;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class MessageDao implements PanacheRepository<Message> {
    public Message findById(Long Id){
        return Message.find("id", Id).firstResult();
    }

    public List<Message> findByChatId(Long chatId, Integer limit, Integer offset) {
        return Message.find("chat.id", chatId)
                .page(Page.of(offset, limit))
                .list();
    }

    public Long countByChatId(Long chatId) {
        return Message.count("chat.id", chatId);
    }

    public Long countReceivedUnreadMessages(Long chatId, Long userId) {
        return Message.count("chat.id = ?1 and read = false and chat.receiver.id = ?2", chatId, userId);
    }

    public Message latestMessage(Long chatId) {
        return Message.find("chat.id=?1 ORDER BY createdAt DESC", chatId)
                .firstResult();
    }

    public List<Chat> chatsWithUnreadMessages(Long userId) {
        return Message.find("chat.receiver.id = ?1 and read = false", userId)
                .list();
    }


    public List<Message> findByReceiverIdAndIsRead(Long userId, boolean b, int page, int size) {
        return Message.find("receiver.id = ?1 and read = ?2", userId, b)
                .page(Page.of(page, size))
                .list();
    }

    public List<Message> findBySenderId(Long senderId, int page, int size) {
        return Message.find("sender.id", senderId)
                .page(Page.of(page, size))
                .list();
    }
}
