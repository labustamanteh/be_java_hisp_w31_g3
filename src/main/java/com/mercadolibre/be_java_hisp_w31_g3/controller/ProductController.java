package com.mercadolibre.be_java_hisp_w31_g3.controller;


import com.mercadolibre.be_java_hisp_w31_g3.service.IProductService;
import com.mercadolibre.be_java_hisp_w31_g3.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    IProductService productService;

    public ProductController(UserService userService){
        this.productService = (IProductService) userService;
    }
}
