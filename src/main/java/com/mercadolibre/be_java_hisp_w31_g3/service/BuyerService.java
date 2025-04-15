package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IBuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BuyerService implements IBuyerService{
    private final IBuyerRepository buyerRepository;
    private final ObjectMapper mapper;

    @Autowired
    public BuyerService(IBuyerRepository buyerRepository, ObjectMapper mapper) {
        this.mapper = mapper;
        this.buyerRepository = buyerRepository;
    }
}
