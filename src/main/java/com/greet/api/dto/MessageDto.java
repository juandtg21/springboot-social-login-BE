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
public class MessageDto {

    private Date createdDate;

    private String message;

    private Long chatRoomId;

    private Long senderId;

}
