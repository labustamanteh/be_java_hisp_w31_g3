package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    Long userId;
    String userName;
}
