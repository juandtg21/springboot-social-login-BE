package com.greet.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greet.api.dto.MessageStatus;
import com.greet.api.dto.MessageType;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", nullable = false)
    private String content;

    @Column(name = "created_date", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final Date createdDate = new Date();

    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.TEXT;

    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.DELIVERED;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private AppUser sender;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "chat_room_id", referencedColumnName = "chat_room_id")
    private ChatRoom chatRoom;

}

