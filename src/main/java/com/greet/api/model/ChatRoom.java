package com.greet.api.model;

import com.greet.api.dto.RoomType;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    private String chatRoomSequenceId;

    @Enumerated(EnumType.STRING)
    private RoomType type = RoomType.PRIVATE;

    private String customChatName;

    @ManyToMany(mappedBy = "rooms")
    private Set<AppUser> members;

    @Override
    public String toString() {
        return "ChatRoom{" +
                "chatRoomId=" + chatRoomId +
                ", type=" + type +
                ", internalChatName=" + chatRoomSequenceId +
                "}";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ChatRoom chatRoom = (ChatRoom) obj;
        return Objects.equals(chatRoomId, chatRoom.chatRoomId)
                && Objects.equals(chatRoomSequenceId, chatRoom.chatRoomSequenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoomId, chatRoomSequenceId);
    }

}
