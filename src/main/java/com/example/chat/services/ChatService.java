package com.example.chat.services;

import com.example.chat.dao.ChatDao;
import com.example.chat.dao.MessageDao;
import com.example.chat.dto.ChatDto;
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
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
        }
    }

    public String deleteChat(Long chatId) {
        try {
            Chat chat = chatDao.findById(chatId);
            if (chat == null) {
                return "Chat not found";
            }
            chatDao.delete(chat);
            return "Chat deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<MessageDto> getMessages(Long chatId, int page, int size) {
        try {
            List<Message> messages = messageDao.findByChatId(chatId, page, size);
            if (messages == null) {
                return null;
            }
            messages.forEach(message -> message.setRead(true));
            messageDao.persist(messages);

            return messages.stream().map(messageMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public MessageDto getMessage(Long messageId) {
        try {
            Message message = messageDao.findById(messageId);
            if (message == null) {
                return null;
            }
            return messageMapper.toDTO(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public MessageDto updateMessage(MessageDto dto) {
        try {
            Message message = messageDao.findById(dto.getId());
            if (message == null) {
                return null;
            }
            if (dto.getContent() != null) {
                message.setContent(dto.getContent());
            }
            if (dto.isRead()) {
                message.setRead(true);
            }
            messageDao.persist(message);
            return messageMapper.toDTO(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String deleteMessage(Long messageId) {
        try {
            Message message = messageDao.findById(messageId);
            if (message == null) {
                return "Message not found";
            }
            messageDao.delete(message);
            return "Message deleted successfully";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<MessageDto> getUnreadMessages(Long userId, int page, int size) {
        try {
            List<Message> messages = messageDao.findByReceiverIdAndIsRead(userId, false, page, size);
            if (messages == null) {
                return null;
            }
            return messages.stream().map(messageMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ChatDto> getChatsWithUnreadMessages(Long userId) {
        try {
            List<Chat> chats = messageDao.chatsWithUnreadMessages(userId);
            if (chats == null) {
                return null;
            }
            return chats.stream().map(chatMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countUnreadMessages(Long chatId, Long userId) {
        try {
            return messageDao.countReceivedUnreadMessages(chatId, userId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public MessageDto getLatestMessage(Long chatId) {
        try {
            Message message = messageDao.latestMessage(chatId);
            if (message == null) {
                return null;
            }
            return messageMapper.toDTO(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ChatDto getChatById(Long chatId) {
        try {
            Chat chat = chatDao.findById(chatId);
            if (chat == null) {
                return null;
            }
            return chatMapper.toDTO(chat);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ChatDto getChatBySenderAndReceiver(Long senderId, Long receiverId) {
        try {
            Chat chat = chatDao.findBySenderAndReceiver(senderId, receiverId);
            if (chat == null) {
                return null;
            }
            return chatMapper.toDTO(chat);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ChatDto> getChatBySenderOrReceiverIdAndChatId(Long userId, Long chatId) {
        try {
            List<Chat> chats = chatDao.findBySenderOrReceiverIdAndChatId(userId, chatId);
            if (chats == null) {
                return null;
            }
            return chats.stream().map(chatMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ChatDto> getChatBySenderId(Long senderId) {
        try {
            List<Chat> chats = chatDao.findBySenderId(senderId);
            if (chats == null) {
                return null;
            }
            return chats.stream().map(chatMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ChatDto> getChatByReceiverId(Long receiverId) {
        try {
            List<Chat> chats = chatDao.findByReceiverId(receiverId);
            if (chats == null) {
                return null;
            }
            return chats.stream().map(chatMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<MessageDto> getMessagesBySenderId(Long senderId, int page, int size) {
        try {
            List<Message> messages = messageDao.findBySenderId(senderId, page, size);
            if (messages == null) {
                return null;
            }
            return messages.stream().map(messageMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<MessageDto> getMessagesByReceiverIdAndIsRead(Long userId, boolean isRead, int page, int size) {
        try {
            List<Message> messages = messageDao.findByReceiverIdAndIsRead(userId, isRead, page, size);
            if (messages == null) {
                return null;
            }
            return messages.stream().map(messageMapper::toDTO).toList();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countMessagesByChatId(Long chatId) {
        try {
            return messageDao.countByChatId(chatId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countMessagesBySenderId(Long senderId) {
        try {
            return messageDao.countByChatId(senderId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countMessagesByReceiverId(Long receiverId) {
        try {
            return messageDao.countByChatId(receiverId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countMessagesBySenderOrReceiverId(Long userId) {
        try {
            return messageDao.countByChatId(userId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Long countMessagesBySenderOrReceiverIdAndChatId(Long chatId) {
        try {
            return messageDao.countByChatId(chatId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
