package com.greet.api.service;

import com.greet.api.dto.LocalUser;
import com.greet.api.dto.SignUpRequest;
import com.greet.api.dto.UserInfo;
import com.greet.api.exception.UserAlreadyExistAuthenticationException;
import com.greet.api.model.AppUser;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    AppUser registerNewUser(SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException;

    AppUser findUserByEmail(String email);

    Optional<AppUser> findUserById(Long id);

    LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo);

    void updateUserStatus(long id);

    List<UserInfo> getAllUsers(Long id);

    UserInfo convertToDto(AppUser user);
}
