package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PostMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<PostDto> convertPostsToDtos(List<Post> posts){
        return posts.stream().map(PostMapper::convertToPostDto).toList();
    }

    public static PostDto convertToPostDto(Post post){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return PostDto.builder()
                .postId(post.getPostId())
                .userId(post.getUserId())
                .date(post.getDate().format(formatter))
                .product(mapper.convertValue(post.getProduct(), ProductDto.class))
                .categoryId(post.getCategoryId())
                .price(post.getPrice())
                .hasPromo(post.getHasPromo())
                .discount(post.getDiscount())
                .build();
    }

}
