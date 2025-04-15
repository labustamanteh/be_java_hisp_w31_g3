package com.mercadolibre.be_java_hisp_w31_g3.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SellerRepository implements ISellerRepository {
    List<Buyer> buyerList = new ArrayList<>();
    List<Seller> sellerList = new ArrayList<>();

    BuyerRepositoty buyerRepositoty = new BuyerRepositoty();

    public SellerRepository(){
        sellerList.add(new Seller("vendedor1"));
        sellerList.add(new Seller("vendedor2"));
        sellerList.add(new Seller("vendedor3"));
    }



}
