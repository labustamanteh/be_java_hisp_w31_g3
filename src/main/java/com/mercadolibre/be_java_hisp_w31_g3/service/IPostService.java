package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;

public interface IPostService {
    UserDto getPostFollowed(Long id, String order);
    UserDto getPromoPostCount(Long userId);
    void addPost(PostDto postDto);
}
