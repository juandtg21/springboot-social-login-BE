package com.greet.api.controller;

import com.greet.api.dto.*;
import com.greet.api.model.AppUser;
import com.greet.api.model.ChatRoom;
import com.greet.api.model.Message;
import com.greet.api.repo.ChatRoomRepository;
import com.greet.api.repo.MessageRepository;
import com.greet.api.service.ChatRoomServiceImpl;
import com.greet.api.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.MessageFormat;
import java.util.List;

@Controller
public class MessageController {

    private static final String QUEUE_MESSAGES = "/topic/{0}.{1}.{2}";

    private static final String MESSAGES = "messages";

    private static final String CHAT_ROOM = "chatroom";

    private final MessageRepository messageRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomServiceImpl chatRoomServiceImpl;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserServiceImpl userService;

    public MessageController(MessageRepository messageRepository,
                             ChatRoomRepository chatRoomRepository,
                             ChatRoomServiceImpl chatRoomServiceImpl,
                             SimpMessagingTemplate simpMessagingTemplate,
                             UserServiceImpl userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomServiceImpl = chatRoomServiceImpl;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/messages/{chatRoomId}")
    public void handleMessage(@DestinationVariable String chatRoomId, MessageDto messageDto) {
        AppUser sender = userService.findUserById(messageDto.getSenderId()).orElseThrow();
        ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatRoomId()).orElseThrow();
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(messageDto.getMessage());
        messageRepository.save(message);
        messageDto.setCreatedDate(message.getCreatedDate());
        String destination = MessageFormat.format(QUEUE_MESSAGES, MESSAGES, CHAT_ROOM, chatRoomId);
        simpMessagingTemplate.convertAndSend(destination, messageDto);
    }
    @MessageMapping("/reload")
    public void getChatRoomsByUser(Long userId) {
        AppUser appUser = userService.findUserById(userId).orElseThrow();
        String destination = MessageFormat.format(QUEUE_MESSAGES, "reload", "user", appUser.getEmail());
        List<ChatRoomDto> chatRooms = chatRoomServiceImpl.findChatRoomsByUser(userId);
        simpMessagingTemplate.convertAndSend(destination, ResponseEntity.ok(chatRooms));

    }
    @MessageMapping("/typing")
    public void getChatRoomMessages(NotificationDto notificationDto) {
        String destination = MessageFormat.format(QUEUE_MESSAGES, "typing", CHAT_ROOM, notificationDto.getChatRoomId());
        simpMessagingTemplate.convertAndSend(destination, notificationDto);
    }
}
