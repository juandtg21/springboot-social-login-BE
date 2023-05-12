package com.greet.api.controller;

import com.greet.api.dto.ChatRoomDto;
import com.greet.api.dto.MessageDto;
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
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.MessageFormat;
import java.util.List;

@Controller
public class MessageController {

    private static final String QUEUE_MESSAGES = "/queue/{0}/messages";

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
        String destination = MessageFormat.format(QUEUE_MESSAGES, chatRoomId);
        simpMessagingTemplate.convertAndSend(destination, messageDto);
    }
    @MessageMapping("/reload")
    @SendToUser("/queue/reload")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByUser(Long userId) {
        List<ChatRoomDto> chatRooms = chatRoomServiceImpl.findChatRoomsByUser(userId);
        return ResponseEntity.ok(chatRooms);
    }
    @MessageMapping("/reload/messages")
    @SendToUser("/queue/reload/messages")
    public List<MessageDto> getChatRoomMessages(@PathVariable Long chatRoomId) {
        return chatRoomServiceImpl.getChatRoomMessages(chatRoomId);
    }
}