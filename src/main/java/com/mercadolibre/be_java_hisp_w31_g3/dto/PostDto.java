package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("post_id")
    private Long postId;
    private String date;
    private ProductDto product;
    private int category;
    private double price;

}

