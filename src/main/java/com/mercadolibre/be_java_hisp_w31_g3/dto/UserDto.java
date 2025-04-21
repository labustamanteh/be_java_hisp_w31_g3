package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user_name")
    private String userName;
    private List<UserDto> followers = null;
    private List<UserDto> followed = null;
    @JsonProperty("followers_count")
    private Long followersCount = null;
    private List<PostDto> posts = null;

    public UserDto(){}

    public UserDto(Long userId, String userName, List<UserDto> followers, List<UserDto> followed, Long followersCount) {
        this.userId = userId;
        this.userName = userName;
        this.followers = followers;
        this.followed = followed;
        this.followersCount = followersCount;
    }
}
