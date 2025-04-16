package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;

public interface IUserService {
    UserDto getFollowersById(Long id, String order);
}
