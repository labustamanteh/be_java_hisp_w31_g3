package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private static Long generatedId = 0L;
    private Long postId;
    private Long userId;
    private LocalDate date;
    private Product product;
    private Long categoryId;
    private Double price;

    public static Long getGeneratedId(){
        return ++generatedId;
    }

}
