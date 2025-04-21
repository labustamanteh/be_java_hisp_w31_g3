package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Product {
    private static Long generatedId = 0L;
    private Long productId;
    private String productName;
    private String type;
    private String brand;
    private String color;
    private String notes;

    public Product() {
        this.productId = ++generatedId;
    }
}
