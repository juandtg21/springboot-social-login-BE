package com.greet.api.repo;

import com.greet.api.model.AppUser;
import com.greet.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByIdNotAndRolesIn(Long id, Set<Role> roles);

    List<AppUser> findAllByIdNot(Long myId);

    Optional<AppUser> findByDisplayName(String displayName);

    @Query("SELECT au from AppUser au join au.rooms r where r.chatRoomId in (:chatRoomsIds) and au.id != :userId")
    List<AppUser> findAppUsersByRoomsInAndIdNot(@Param("chatRoomsIds") List<Long> chatRoomsIds, @Param("userId") Long userId);
}
