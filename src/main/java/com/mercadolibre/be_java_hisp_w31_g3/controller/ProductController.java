package com.mercadolibre.be_java_hisp_w31_g3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g3.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g3.service.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @PostMapping("/promo-post")
    public ResponseEntity<?> createPromoPost(@RequestBody PostDto postDto){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
