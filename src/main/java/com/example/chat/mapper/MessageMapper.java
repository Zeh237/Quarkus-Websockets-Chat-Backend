package com.example.chat.mapper;

import com.example.chat.dao.ChatDao;
import com.example.chat.dto.MessageDto;
import com.example.chat.model.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageMapper {

    @Inject
    private ChatDao chatDao;

     public MessageDto toDTO(Message message) {
         if (message == null) return null;

         MessageDto messageDto = new MessageDto();
         messageDto.setId(message.getId());
         messageDto.setChatId(message.getChat().getId());
         messageDto.setContent(message.getContent());
         messageDto.setRead(message.isRead());
         messageDto.setCreatedAt(message.getCreatedAt());
         messageDto.setUpdatedAt(message.getUpdatedAt());
         return messageDto;
     }

     public Message toEntity(MessageDto dto) {
         if (dto == null) return null;

         Message message = new Message();
         message.setChat(chatDao.findById(dto.getChatId()));
         message.setContent(dto.getContent());
         message.setRead(dto.isRead());
         message.setCreatedAt(dto.getCreatedAt());
         message.setUpdatedAt(dto.getUpdatedAt());
         return message;
     }
}
