package com.mercadolibre.be_java_hisp_w31_g3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    private static Long generatedId = 0L;
    private Long userId;
    @JsonProperty("user_name")
    private String userName;
    private List<User> followers = new ArrayList<>();
    private List<User> followed = new ArrayList<>();
    private List<Post> posts = new ArrayList<>();

    public User(){
        this.userId = ++generatedId;
    }
}
