package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostResponseDto;

import java.util.Optional;

public interface IProductService {
    PostResponseDto getPostFollowed(Long id, String order);
}
