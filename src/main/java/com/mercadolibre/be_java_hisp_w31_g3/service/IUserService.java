package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;

import java.util.List;

public interface IUserService {
    List<UserDto> getUsers();
    void addFollower(Long userId, Long userToFollow);
    UserDto getFollowersCount(Long userId);
    UserDto getFollowersById(Long id, String order);
}
