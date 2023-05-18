package com.greet.api.repo;

import com.greet.api.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT * FROM message JOIN chat_room cr on message.chat_room_id = cr.chat_room_id WHERE cr.chat_room_id = :chatRoomId",
            nativeQuery = true)
    List<Message> findAllByChatRoomIdOrderByCreatedDate(@Param("chatRoomId") Long chatRoomId);
}
