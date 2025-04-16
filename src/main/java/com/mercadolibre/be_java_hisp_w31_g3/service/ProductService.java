package com.mercadolibre.be_java_hisp_w31_g3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IProductRepository;
import com.mercadolibre.be_java_hisp_w31_g3.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private IProductRepository productRepository;
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
}
