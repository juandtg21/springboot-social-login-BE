package com.greet.api.util;

import com.greet.api.dto.LocalUser;
import com.greet.api.dto.SocialProvider;
import com.greet.api.dto.UserInfo;
import com.greet.api.dto.UserStatus;
import com.greet.api.model.AppUser;
import com.greet.api.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonUtils {

    public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public static SocialProvider toSocialProvider(String providerId) {
        for (SocialProvider socialProvider : SocialProvider.values()) {
            if (socialProvider.getProviderType().equals(providerId)) {
                return socialProvider;
            }
        }
        return SocialProvider.LOCAL;
    }

    public static UserInfo buildUserInfo(LocalUser localUser) {
        List<String> roles = localUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        AppUser user = localUser.getUser();
        return new UserInfo(user.getId().toString(), user.getDisplayName(), user.getPicture(), user.getEmail(), UserStatus.ACTIVE.name(), roles);
    }
}
