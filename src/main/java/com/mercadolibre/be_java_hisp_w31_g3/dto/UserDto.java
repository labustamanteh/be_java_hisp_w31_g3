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
    List<UserDto> followed = new ArrayList<>();
    List<UserDto> followers = new ArrayList<>();


    public UserDto(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.followed = new ArrayList<>();
        this.followers = new ArrayList<>();
    }

}

