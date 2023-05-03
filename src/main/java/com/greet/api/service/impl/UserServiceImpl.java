package com.greet.api.service.impl;

import com.greet.api.dto.*;
import com.greet.api.exception.OAuth2AuthenticationProcessingException;
import com.greet.api.exception.UserAlreadyExistAuthenticationException;
import com.greet.api.model.AppUser;
import com.greet.api.model.Role;
import com.greet.api.repo.RoleRepository;
import com.greet.api.repo.UserRepository;
import com.greet.api.security.oauth2.user.OAuth2UserInfo;
import com.greet.api.security.oauth2.user.OAuth2UserInfoFactory;
import com.greet.api.service.UserService;
import com.greet.api.util.CommonUtils;
import com.greet.api.util.PasswordGeneratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(value = "transactionManager")
    public AppUser registerNewUser(final SignUpRequest signUpRequest) throws UserAlreadyExistAuthenticationException {
        if (signUpRequest.getUserID() != null && userRepository.existsById(signUpRequest.getUserID())) {
            throw new UserAlreadyExistAuthenticationException("User with User id " + signUpRequest.getUserID() + " already exist");
        } else if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistAuthenticationException("User with email id " + signUpRequest.getEmail() + " already exist");
        }
        AppUser user = buildUser(signUpRequest);
        Date now = Calendar.getInstance().getTime();
        user.setCreatedDate(now);
        user.setModifiedDate(now);
        user = userRepository.save(user);
        userRepository.flush();
        return user;
    }

    private AppUser buildUser(final SignUpRequest formDTO) {
        String password = formDTO.getPassword() != null ? formDTO.getPassword() : PasswordGeneratorUtils.generatePassword(12);
        AppUser user = new AppUser();
        user.setDisplayName(formDTO.getDisplayName());
        user.setPicture(formDTO.getPicture());
        user.setEmail(formDTO.getEmail());
        user.setPassword(passwordEncoder.encode(password));
        final HashSet<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(Role.ROLE_USER));
        user.setRoles(roles);
        user.setProvider(formDTO.getSocialProvider().getProviderType());
        user.setEnabled(true);
        user.setProviderUserId(formDTO.getProviderUserId());
        return user;
    }

    @Override
    public AppUser findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public LocalUser processUserRegistration(String registrationId, Map<String, Object> attributes, OidcIdToken idToken, OidcUserInfo userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);
        if (ObjectUtils.isEmpty(oAuth2UserInfo.getName())) {
            throw new OAuth2AuthenticationProcessingException("Name not found from OAuth2 provider");
        } else if (ObjectUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }
        SignUpRequest userDetails = toUserRegistrationObject(registrationId, oAuth2UserInfo);
        AppUser user = findUserByEmail(oAuth2UserInfo.getEmail());
        if (user != null) {
            if (!user.getProvider().equals(registrationId) && !user.getProvider().equals(SocialProvider.LOCAL.getProviderType())) {
                throw new OAuth2AuthenticationProcessingException(
                        "Looks like you're signed up with " + user.getProvider() + " account. Please use your " + user.getProvider() + " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userDetails);
        }

        return LocalUser.create(user, attributes, idToken, userInfo);
    }

    private AppUser updateExistingUser(AppUser existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setDisplayName(oAuth2UserInfo.getName());
        existingUser.setPicture(oAuth2UserInfo.getImageUrl());
        return userRepository.save(existingUser);
    }

    private SignUpRequest toUserRegistrationObject(String registrationId, OAuth2UserInfo oAuth2UserInfo) {
        return SignUpRequest.getBuilder().addProviderUserID(oAuth2UserInfo.getId()).addDisplayName(oAuth2UserInfo.getName()).addEmail(oAuth2UserInfo.getEmail())
                .addSocialProvider(CommonUtils.toSocialProvider(registrationId)).addPicture(oAuth2UserInfo.getImageUrl()).build();
    }

    @Override
    public Optional<AppUser> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateUserStatus(long id) {
        Optional<AppUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setStatus(UserStatus.DISCONNECTED.name());
            userRepository.save(user.orElse(null));
        }
    }
    @Override
    public List<UserInfo> getAllUsers(Long id) {
        var roles = roleRepository.findAllByName(Role.ROLE_USER);
        return userRepository.findByIdNotAndRolesIn(id, roles)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo convertToDto(AppUser user) {
        return buildUserInfo(user);
    }

    private UserInfo buildUserInfo(AppUser user) {
        List<String> roleStrings = user.getRoles()
                .stream()
                .map(Role::toString)
                .collect(Collectors.toList());

        return new UserInfo(user.getId().toString(),
                user.getDisplayName(), user.getPicture(),
                user.getEmail(), user.getStatus(), roleStrings);
    }
}
