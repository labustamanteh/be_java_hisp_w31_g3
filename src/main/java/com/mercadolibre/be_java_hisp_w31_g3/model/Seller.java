package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Seller extends User {
    List<Buyer> buyerList;
    Integer followerCount;
    List<Post> postList;

    public Seller(String userName) {
        super(userName);
        this.buyerList = new ArrayList<>();
    }
}
