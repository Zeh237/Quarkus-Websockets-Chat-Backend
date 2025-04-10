package com.example.chat.mapper;

import com.example.chat.dao.ChatDao;
import com.example.chat.dao.MessageDao;
import com.example.chat.dto.ChatDto;
import com.example.chat.model.Chat;
import com.example.users.dao.UserDao;
import com.example.users.mapper.UserMapper;
import com.example.users.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ChatMapper {

    @Inject
    private UserDao userDao;

    @Inject
    private UserMapper userMapper;

    @Inject
    private MessageDao messageDao;

    @Inject
    private MessageMapper messageMapper;

    public ChatDto toDTO(Chat chat) {
        if (chat == null) return null;

        ChatDto chatDto = new ChatDto();
        chatDto.setId(chat.getId());
        chatDto.setSenderId(chat.getSender().getId());
        chatDto.setReceiverId(chat.getReceiver().getId());
        chatDto.setSender(userMapper.toDTO(chat.getSender()));
        chatDto.setReceiver(userMapper.toDTO(chat.getSender()));
        chatDto.setLastMessage(messageMapper.toDTO(messageDao.latestMessage(chat.getId())));
        chatDto.setCreatedAt(chat.getCreatedAt());
        chatDto.setUpdatedAt(chat.getUpdatedAt());
        return chatDto;
    }

    public Chat toEntity(ChatDto dto) {
        if (dto == null) return null;
        User sender = userDao.findById(dto.getSenderId());
        User receiver = userDao.findById(dto.getReceiverId());

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setReceiver(receiver);

        return chat;
    }
}
