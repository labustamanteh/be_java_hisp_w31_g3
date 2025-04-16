package com.mercadolibre.be_java_hisp_w31_g3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IPostRepository;
import com.mercadolibre.be_java_hisp_w31_g3.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private IPostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
}
