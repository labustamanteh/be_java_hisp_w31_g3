package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.util.List;

public class UserMapper {

    public static UserDto getUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .followers(convertToListUserDto(user.getFollowers()))
                .followed(convertToListUserDto(user.getFollowed()))
                .posts(PostMapper.convertPostsToDtos(user.getPosts()))
                .build();
    }

    public static List<UserDto> convertToListUserDto(List<User> users) {
        return users.stream()
                .map(UserMapper::convertToUserDto)
                .toList();
    }

    public static UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .build();
    }
}
