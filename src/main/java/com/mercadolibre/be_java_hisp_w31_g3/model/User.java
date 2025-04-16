package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private Long userId;
    private String userName;
    private List<User> followed = new ArrayList<>();
    private List<User> followers = new ArrayList<>();
}
