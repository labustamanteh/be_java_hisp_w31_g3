package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private Long user_id;
    private Long post_id;
    private LocalDate date;
    private Product product;
    private int category;
    private double price;
}
