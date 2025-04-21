package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Name;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {
    @JsonProperty("product_id")
    private Long productId;
    private String type;
    private String brand;
    private String name;
    private String color;
    private String notes;

}
