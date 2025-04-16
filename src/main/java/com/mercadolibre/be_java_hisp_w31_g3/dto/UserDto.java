package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private Long userId;
    private String userName;
    @Builder.Default
    private List<UserDto> followers = null;
    @Builder.Default
    private List<UserDto> followed = null;
    @Builder.Default
    private Long followersCount = null;

    public UserDto(){}

    public UserDto(Long userId, String userName, List<UserDto> followers, List<UserDto> followed, Long followersCount) {
        this.userId = userId;
        this.userName = userName;
        this.followers = followers;
        this.followed = followed;
        this.followersCount = followersCount;
    }
}
