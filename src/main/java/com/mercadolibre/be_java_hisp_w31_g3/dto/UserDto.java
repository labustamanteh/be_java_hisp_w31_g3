package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {
    private Long userId;
    private String userName;
    private List<UserDto> followed = new ArrayList<>();
    private List<UserDto> followers = new ArrayList<>();
}

