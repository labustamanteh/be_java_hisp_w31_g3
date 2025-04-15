package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.ISellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerService implements ISellerService{
    private final ISellerRepository sellerRepository;

    @Autowired
    public SellerService(ISellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }
}
