package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto implements Serializable {

    @NotNull(message = "El productId es necesario")
    @Min(value = 1, message = "El productId debe ser mayor a 0")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "El nombre del producto es necesario")
    @NotBlank(message = "El campo no puede estar vacío")
    @Size(max = 40, message = "La longitud no puede superar los 40 caracteres")
    @Pattern(
            regexp  = "^[\\p{L}\\p{N} ]+$",
            message = "El campo no puede poseer caracteres especiales"
    )
    @JsonProperty("product_name")
    private String productName;

    @NotNull(message = "El tipo del producto es necesario")
    @NotBlank(message = "El campo no puede estar vacío")
    @Size(max = 15, message = "La longitud no puede superar los 15 caracteres")
    @Pattern(
            regexp  = "^[\\p{L}\\p{N} ]+$",
            message = "El campo no puede poseer caracteres especiales"
    )
    private String type;

    @NotNull(message = "La marca del producto es necesario")
    @NotBlank(message = "El campo no puede estar vacío")
    @Size(max = 25, message = "La longitud no puede superar los 25 caracteres")
    @Pattern(
            regexp  = "^[\\p{L}\\p{N} ]+$",
            message = "El campo no puede poseer caracteres especiales"
    )
    private String brand;

    @NotNull(message = "El color del producto es necesario")
    @NotBlank(message = "El campo no puede estar vacío")
    @Size(max = 15, message = "La longitud no puede superar los 15 caracteres")
    @Pattern(
            regexp  = "^[\\p{L}\\p{N} ]+$",
            message = "El campo no puede poseer caracteres especiales"
    )
    private String color;

    @Size(max = 80, message = "La longitud no puede superar los 80 caracteres")
    @Pattern(
            regexp  = "^[\\p{L}\\p{N} ]+$",
            message = "El campo no puede poseer caracteres especiales"
    )
    private String notes;
}
