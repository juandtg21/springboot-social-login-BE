package com.greet.api.service;

import com.greet.api.dto.ChatRoomDto;
import com.greet.api.dto.CreateRoomRequest;
import com.greet.api.dto.MessageDto;
import com.greet.api.dto.MessagePostDto;
import com.greet.api.exception.ResourceNotFoundException;
import com.greet.api.model.AppUser;
import com.greet.api.model.ChatRoom;
import com.greet.api.model.Message;
import com.greet.api.repo.ChatRoomRepository;
import com.greet.api.repo.MessageRepository;
import com.greet.api.repo.UserRepository;
import com.greet.api.service.impl.UserServiceImpl;
import com.greet.api.util.ChatRoomSequenceUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatRoomServiceImpl {

    private Logger logger = LoggerFactory.getLogger(ChatRoomServiceImpl.class);

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final UserServiceImpl userService;

    public ChatRoomServiceImpl(ChatRoomRepository chatRoomRepository,
                               MessageRepository messageRepository,
                               UserRepository userRepository,
                               UserServiceImpl userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public List<ChatRoomDto> findChatRoomsByUser(Long userId) {
        List<ChatRoomDto> chatRoomsDtos = new ArrayList<>();
        try {
            List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByUserId(userId);
            if (chatRooms.isEmpty()) return chatRoomsDtos;
            for (ChatRoom chatRoom : chatRooms) {
                Set<AppUser> members = chatRoom.getMembers();
                for (AppUser member : members) {
                    if (!Objects.equals(member.getId(), userId)) {
                        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom, member);
                        chatRoomsDtos.add(chatRoomDto);
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Unexpected error happened trying to save the room: {}", e.getMessage());
            throw new ResourceNotFoundException("ChatRoom", "id", userId);
        }

        return chatRoomsDtos;
    }

    public List<ChatRoomDto> createChatRoom(CreateRoomRequest createRoomRequest) {
        List<ChatRoomDto> chatRoomsDtos = new ArrayList<>();
        List<AppUser> users = userRepository.findAllById(createRoomRequest.getMembers());
        if (!users.isEmpty()) {
            String chatRoomSequence = ChatRoomSequenceUtils.getSHA256Hash(users.stream()
                    .map(AppUser::getEmail)
                    .collect(Collectors.toList()));
            ChatRoom newChatRoom = createNewChatRoom();
            newChatRoom.setChatRoomSequenceId(chatRoomSequence);
            chatRoomRepository.save(newChatRoom);
            for (AppUser member : users) {
                member.getRooms().add(newChatRoom);
                userRepository.save(member);
                if (!Objects.equals(member.getId(), createRoomRequest.getCurrentUserId())) {
                    chatRoomsDtos.add(createChatRoomDto(newChatRoom, member));
                }
            }
        }
        return chatRoomsDtos;
    }

    public List<ChatRoomDto> findChatRoomByChatRoomSequence(CreateRoomRequest createRoomRequest) {
        List<ChatRoomDto> chatRoomsDtos = new ArrayList<>();
        List<AppUser> users = userRepository.findAllById(createRoomRequest.getMembers());
        if (!users.isEmpty()) {
            List<String> emails = users.stream()
                    .map(AppUser::getEmail)
                    .collect(Collectors.toList());
            String chatRoomSequence = ChatRoomSequenceUtils.getSHA256Hash(emails);
            ChatRoom chatRoom = chatRoomRepository.findByChatRoomSequenceId(chatRoomSequence);
            if (chatRoom == null) return chatRoomsDtos;
            chatRoomsDtos = createChatRoomDtos(chatRoom, users, createRoomRequest.getCurrentUserId());
        }
        return chatRoomsDtos;
    }

    public MessageDto saveMessage(MessagePostDto messagePostDto) {
        AppUser sender = userService.findUserById(messagePostDto.getSenderId()).orElseThrow();
        ChatRoom chatRoom = chatRoomRepository.findById(messagePostDto.getChatRoomId()).orElseThrow();
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(messagePostDto.getMessage())
                .build();
        messageRepository.save(message);
        messagePostDto.setCreatedDate(message.getCreatedDate());
        return convertToDto(message);
    }

    private ChatRoom createNewChatRoom() {
        return new ChatRoom();
    }

    private List<ChatRoomDto> createChatRoomDtos(ChatRoom chatRoom, List<AppUser> members, Long currentUserId) {
        List<ChatRoomDto> chatRoomsDtos = new ArrayList<>();
        for (AppUser member : members) {
            if (!Objects.equals(member.getId(), currentUserId)) {
                chatRoomsDtos.add(createChatRoomDto(chatRoom, member));
            }
        }
        return chatRoomsDtos;
    }

    private ChatRoomDto createChatRoomDto(ChatRoom chatRoom, AppUser member) {
        return new ChatRoomDto(chatRoom, member);
    }

    public List<MessageDto> getChatRoomMessages(Long chatRoomId) {
        List<Message> messages;
        messages = messageRepository.findAllByChatRoomIdOrderByCreatedDate(chatRoomId);
        if(messages.isEmpty()) return null;
        return messages.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private MessageDto convertToDto(Message message) {
        return MessageDto.builder()
                .message(message.getContent())
                .senderId(message.getSender().getId())
                .createdDate(message.getCreatedDate())
                .chatRoomId(message.getChatRoom().getChatRoomId())
                .build();
    }
}

