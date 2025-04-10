package com.example.chat.services;

import com.example.chat.dao.ChatDao;
import com.example.chat.dao.MessageDao;
import com.example.chat.dto.ChatDto;
import com.example.chat.dto.MessageDto;
import com.example.chat.dto.MessageDto;
import com.example.chat.mapper.ChatMapper;
import com.example.chat.model.Chat;
import com.example.chat.model.Message;
import com.example.users.dao.UserDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.example.chat.mapper.MessageMapper;

import java.util.HashMap;
import java.util.List;

@ApplicationScoped
public class ChatService {

    @Inject
    private UserDao userDao;

    @Inject
    private ChatDao chatDao;

    @Inject
    private MessageDao messageDao;

    @Inject
    private ChatMapper chatMapper;

    @Inject
    private MessageMapper messageMapper;

    public void sendMessage(MessageDto dto) {
        try {
            if (dto.getContent() == null) {
                throw new IllegalArgumentException("Message content cannot be null");
            }

            HashMap<String, Long> chatdto = new HashMap<String, Long>() {
            };

            chatdto.put("senderId", dto.getSenderId());
            chatdto.put("receiverId", dto.getReceiverId());

            Chat chat = chatDao.findBySenderAndReceiver(chatdto.get("senderId"), chatdto.get("receiverId"));
            if (chat == null) {
                chat = new Chat();
                chat.setSender(userDao.findById(chatdto.get("senderId")));
                chat.setReceiver(userDao.findById(chatdto.get("receiverId")));
                chatDao.persist(chat);

                dto.setChatId(chat.getId());

                Message message = messageMapper.toEntity(dto);
                messageDao.persist(message);
            } else {
                dto.setChatId(chat.getId());
                Message message = messageMapper.toEntity(dto);
                messageDao.persist(message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    public List<ChatDto> getChats(Long userId, int page, int size) {
        try {
            List<Chat> chats = chatDao.findBySenderOrReceiverId(userId, page, size);
            if (chats == null) {
                return null;
            }
            return chats.stream().map(chatMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
