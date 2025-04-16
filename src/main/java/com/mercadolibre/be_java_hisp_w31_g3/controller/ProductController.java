package com.mercadolibre.be_java_hisp_w31_g3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g3.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g3.service.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor

public class ProductController {
    private final  IProductService productService;
    private final  IPostService postService; 
    
   
    

}
