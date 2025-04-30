package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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

    @NotNull(message = "El id no puede estar vacío")
    @Min(value = 1, message = "El id debe ser mayor a cero")
    @JsonProperty(value = "user_id", required = true)
    private Long userId;

    @NotBlank(message = "La fecha no puede estar vacía")
    @Pattern(
            regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$",
            message = "La fecha debe tener formato dd-MM-yyyy"
    )
    @JsonProperty(required = true)
    private String date;

    @Builder.Default
    @JsonProperty(required = true)
    @Valid
    private ProductDto product = null;

    @NotNull(message = "El categoryId es necesario")
    @Min(value = 1, message = "La categoría no puede ser 0")
    @JsonProperty(value = "category", required = true)
    private Long categoryId;

    @Max(value = 10000000, message = "El valor no puede ser mayor a 10'000.000")
    @NotNull(message = "El precio es necesario")
    @JsonProperty(required = true)
    private Double price;

    @JsonProperty(value = "has_promo")
    private Boolean hasPromo = false;

    @Max(value = 1, message = "El descuento no puede ser mayor a 1")
    @Min(value = 0, message = "El descuento no puede ser menor a 0")
    private Double discount = 0D;
}
