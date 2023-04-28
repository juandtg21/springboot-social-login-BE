package com.greet.api.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SocialAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    public SocialAuthenticationToken(Object principal) {
        super(null);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
