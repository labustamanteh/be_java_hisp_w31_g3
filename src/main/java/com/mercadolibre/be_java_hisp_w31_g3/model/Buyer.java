package com.mercadolibre.be_java_hisp_w31_g3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
public class Buyer extends User{
    List<Seller> sellerList;

    public Buyer(String userName) {
        super(userName);
        this.sellerList = new ArrayList<>();
    }
}
