package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {
    private int product_id;
    private String type;
    private String brand;
    private String name;
    private String color;
    private String notes;

}
