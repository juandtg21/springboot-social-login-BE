package com.greet.api.repo;

import com.greet.api.model.AppUser;
import com.greet.api.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT DISTINCT cr FROM ChatRoom cr JOIN FETCH cr.members WHERE cr.chatRoomId" +
            " IN (SELECT DISTINCT cr2.chatRoomId FROM ChatRoom cr2 JOIN cr2.members m WHERE m.id = :userId)")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);


    @Query("SELECT cr FROM ChatRoom cr WHERE EXISTS (" +
            "SELECT 1 FROM cr.members m WHERE m IN :users " +
            "GROUP BY cr HAVING COUNT(DISTINCT m) = :numUsers)")
    ChatRoom findChatRoomByMembers(@Param("users") Set<AppUser> users, @Param("numUsers") Long numUsers);

    ChatRoom findByChatRoomSequenceId(String chatRoomSequenceId);

}
