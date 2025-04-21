package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Post {
    private static Long generatedId = 0L;
    private Long userId;
    private Long postId;
    private LocalDate date;
    private Product product;
    private int category;
    private double price;

    public Post() {
        this.postId = ++generatedId;
    }
}
