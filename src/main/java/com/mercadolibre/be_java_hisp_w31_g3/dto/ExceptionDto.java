package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionDto {
    private String message;
    private Map<String, List<String>> errors;

    public ExceptionDto(String message) {
        this.message = message;
    }

    public ExceptionDto(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
