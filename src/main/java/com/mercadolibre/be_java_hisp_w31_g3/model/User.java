package com.mercadolibre.be_java_hisp_w31_g3.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private Long userId;
    private String userName;
    List<User> followed = new ArrayList<>();
    List<User> followers = new ArrayList<>();
}
