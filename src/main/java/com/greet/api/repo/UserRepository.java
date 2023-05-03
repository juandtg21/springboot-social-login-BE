package com.greet.api.repo;

import com.greet.api.model.AppUser;
import com.greet.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    List<AppUser> findByIdNotAndRolesIn(Long id, Set<Role> roles);
}
