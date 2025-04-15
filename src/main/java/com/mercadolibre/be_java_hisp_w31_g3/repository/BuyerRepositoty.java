package com.mercadolibre.be_java_hisp_w31_g3.repository;


import com.mercadolibre.be_java_hisp_w31_g3.model.Buyer;
import com.mercadolibre.be_java_hisp_w31_g3.model.Seller;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BuyerRepositoty implements IBuyerRepository {
    List<Buyer> buyerList = new ArrayList<>();


    public BuyerRepositoty() {
        buyerList.add(new Buyer("usuario1"));
        buyerList.add(new Buyer("usuario2"));
        buyerList.add(new Buyer("usuario3"));
    }


}
