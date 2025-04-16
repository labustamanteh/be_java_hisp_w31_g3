package com.mercadolibre.be_java_hisp_w31_g3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g3.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g3.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g3.service.PostService;
import com.mercadolibre.be_java_hisp_w31_g3.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private IProductService productService;
    private IPostService postService; 
    
    public ProductController(ProductService productService, PostService postService){
        this.postService = postService;
        this.productService = productService;
    }

    

}
