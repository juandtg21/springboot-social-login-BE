package com.greet.api.dto;

import com.greet.api.model.AppUser;
import com.greet.api.model.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {

    private Long roomId;
    private Long id;
    private String displayName;
    private String picture;
    private String email;
    private String status;

    public ChatRoomDto(ChatRoom chatRoom, AppUser member) {
        this.roomId = chatRoom.getChatRoomId();
        this.id = member.getId();
        this.displayName = member.getDisplayName();
        this.picture = member.getPicture();
        this.email = member.getEmail();
        this.status = member.getStatus();
    }

}







