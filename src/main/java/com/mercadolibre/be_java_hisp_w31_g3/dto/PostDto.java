package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto implements Serializable {
    private Long postId;
    @JsonProperty(value = "user_id", required = true)
    private Long userId;
    @JsonProperty(required = true)
    private String date;
    @Builder.Default
    @JsonProperty(required = true)
    private ProductDto product = null;
    @JsonProperty(value = "category", required = true)
    private Long categoryId;
    @JsonProperty(required = true)
    private Double price;
    @JsonProperty(value = "has_promo")
    private Boolean hasPromo = false;
    private Double discount = 0D;
}
