package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Post {
    private static Long generatedId = 0L;
    private Long postId;
    private Long userId;
    private LocalDate date;
    private Product product;
    private Long category;
    private Double price;
    private Boolean hasPromo;
    private Double discount;

    public Post() {
        this.postId = ++generatedId;
    }
}
