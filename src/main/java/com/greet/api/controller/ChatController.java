package com.greet.api.controller;

import com.greet.api.dto.ChatRoomDto;
import com.greet.api.dto.CreateRoomRequest;
import com.greet.api.dto.MessageDto;
import com.greet.api.service.ChatRoomServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatroom")
public class ChatController {

    private final ChatRoomServiceImpl chatRoomServiceImpl;

    public ChatController(ChatRoomServiceImpl chatRoomServiceImpl) {
        this.chatRoomServiceImpl = chatRoomServiceImpl;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoomDto>> getChatRoomsByUser(@PathVariable Long userId) {
        List<ChatRoomDto> chatRooms = chatRoomServiceImpl.findChatRoomsByUser(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping()
    public ResponseEntity<List<ChatRoomDto>> creatChatRoom(@RequestBody CreateRoomRequest createRoomRequest) {
        List<ChatRoomDto> chatRooms = chatRoomServiceImpl.createChatRoom(createRoomRequest);
        return ResponseEntity.ok(chatRooms);
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> findChatRoomByMembers(@RequestParam("currentUserId") Long currentUserId,
                                                                   @RequestParam("members") List<Long> userIds) {
        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
                .members(userIds)
                .currentUserId(currentUserId)
                .build();
        List<ChatRoomDto> chatRooms = chatRoomServiceImpl.findChatRoomByChatRoomSequence(createRoomRequest);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/chats/{chatRoomId}")
    public List<MessageDto> getChatRoomMessages(@PathVariable Long chatRoomId) {
        return chatRoomServiceImpl.getChatRoomMessages(chatRoomId);
    }
}
