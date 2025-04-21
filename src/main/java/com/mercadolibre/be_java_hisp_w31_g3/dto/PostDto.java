package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {
    private Long user_id;
    private Long post_id;
    private String date;
    private ProductDto product;
    private int category;
    private double price;

}

