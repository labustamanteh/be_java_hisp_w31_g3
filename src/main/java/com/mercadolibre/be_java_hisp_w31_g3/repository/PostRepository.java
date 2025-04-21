package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepository implements IPostRepository {
    private final List<Post> products = new ArrayList<>();

    @Override
    public void add(Post post) {
        products.add(post);
    }
}
