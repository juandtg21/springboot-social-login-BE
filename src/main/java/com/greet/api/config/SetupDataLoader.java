package com.greet.api.config;

import com.greet.api.dto.SocialProvider;
import com.greet.api.model.AppUser;
import com.greet.api.model.Role;
import com.greet.api.repo.RoleRepository;
import com.greet.api.repo.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }
        // Create initial roles
        Role userRole = createRoleIfNotFound(Role.ROLE_USER);
        Role adminRole = createRoleIfNotFound(Role.ROLE_ADMIN);
        Role modRole = createRoleIfNotFound(Role.ROLE_MODERATOR);
        createUserIfNotFound("admin@test.com", Set.of(userRole, adminRole, modRole), "Admin");
        createUserIfNotFound("juandtg@test.com", Set.of(userRole), "juandtg");
        createUserIfNotFound("david@test.com", Set.of(userRole), "david");
        createUserIfNotFound("teban@test.com", Set.of(userRole), "teban");
        createUserIfNotFound("tavo@test.com", Set.of(userRole), "tavo");
        alreadySetup = true;
    }

    @Transactional
    AppUser createUserIfNotFound(final String email, Set<Role> roles, String displayName) {
        AppUser user = userRepository.findByEmail(email);
        if (user == null) {
            user = new AppUser();
            user.setDisplayName(displayName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRoles(roles);
            user.setProvider(SocialProvider.LOCAL);
            user.setEnabled(true);
            Date now = Calendar.getInstance().getTime();
            user.setCreatedDate(now);
            user.setModifiedDate(now);
            user = userRepository.save(user);
        }
        return user;
    }

    @Transactional
    Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = roleRepository.save(new Role(name));
        }
        return role;
    }
}
