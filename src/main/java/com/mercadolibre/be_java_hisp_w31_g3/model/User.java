package com.mercadolibre.be_java_hisp_w31_g3.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private Long userId;
    private String userName;
    List<User> followers = new ArrayList<>();
    List<User> follows = new ArrayList<>();
    
}
