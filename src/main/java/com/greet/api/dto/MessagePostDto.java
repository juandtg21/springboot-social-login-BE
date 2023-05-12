package com.greet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessagePostDto {

    private Date createdDate;

    private String message;

    private Long recipientId;

    private Long chatRoomId;

    private Long senderId;

}