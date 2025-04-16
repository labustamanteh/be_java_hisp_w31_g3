package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final IProductRepository productRepository;
}
