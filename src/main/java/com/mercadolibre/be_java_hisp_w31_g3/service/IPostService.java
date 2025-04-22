package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;

import java.util.List;

public interface IPostService {
    UserDto getPostFollowed(Long id, String order);
    UserDto getPromoPostCount(Long userId);
    void addPost(PostDto postDto);
    UserDto getPromoPostByUserId(Long userId);
    List<PostDto> getPostList();
    List<PostDto> getPostsByFilter(Double discount, Long categoryId, String color, Boolean hasPromo);
}
