package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long userId;
    private String userName;
    private List<User> followers = new ArrayList<>();
    private List<User> followed = new ArrayList<>();
    private Integer followersCount = null;

    private void setUserId(Long userId) {
        this.userId = userId;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    private void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    private void setFollowed(List<User> followed) {
        this.followed = followed;
    }

    private void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public static UserDtoBuilder getUserDtoBuilder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long userId;
        private String userName;
        private List<User> followers = null;
        private List<User> followed = null;
        private Integer followersCount = null;
        private UserDto dto;

        public UserDtoBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserDtoBuilder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserDtoBuilder withFollowers(List<User> followers) {
            this.followers = followers;
            return this;
        }

        public UserDtoBuilder withFollowed(List<User> followed) {
            this.followed = followed;
            return this;
        }

        public UserDtoBuilder withFollowersCount(Integer followersCount) {
            this.followersCount = followersCount;
            return this;
        }

        public UserDto build(){
            this.dto = new UserDto();
            dto.setUserId(userId);
            dto.setUserName(userName);
            dto.setFollowers(followers);
            dto.setFollowed(followed);
            dto.setFollowersCount(followersCount);
            return this.dto;
        }
    }
}
