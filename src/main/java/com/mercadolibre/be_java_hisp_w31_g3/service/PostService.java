package com.mercadolibre.be_java_hisp_w31_g3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IPostRepository;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository postRepository;

    
}
