package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.FollowersCountDto;

public interface IUserService {
    FollowersCountDto getfollowersCount(Long userId);
}
