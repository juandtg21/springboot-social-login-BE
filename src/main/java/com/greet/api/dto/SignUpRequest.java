package com.greet.api.dto;

import com.greet.api.util.PasswordMatchesUtils;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@PasswordMatchesUtils
public class SignUpRequest {

    private Long userID;

    private String providerUserId;

    @NotEmpty
    private String displayName;

    private String picture;

    @NotEmpty
    private String email;

    private SocialProvider socialProvider;

    @Size(min = 6, message = "{Size.userDto.password}")
    private String password;

    @NotEmpty
    private String matchingPassword;

    public SignUpRequest(String providerUserId,
                         String displayName,
                         String email,
                         String password,
                         SocialProvider socialProvider,
                         String picture) {
        this.providerUserId = providerUserId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.socialProvider = socialProvider;
        this.picture = picture;
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String providerUserID;
        private String displayName;
        private String picture;
        private String email;
        private String password;
        private SocialProvider socialProvider;

        public Builder addProviderUserID(final String userID) {
            this.providerUserID = userID;
            return this;
        }

        public Builder addDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder addPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder addSocialProvider(final SocialProvider socialProvider) {
            this.socialProvider = socialProvider;
            return this;
        }

        public Builder addPicture(final String picture) {
            this.picture = picture;
            return this;
        }

        public SignUpRequest build() {
            return new SignUpRequest(providerUserID, displayName, email, password, socialProvider, picture);
        }
    }
}
