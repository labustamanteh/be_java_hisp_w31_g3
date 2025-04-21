package com.mercadolibre.be_java_hisp_w31_g3.controller;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mercadolibre.be_java_hisp_w31_g3.service.IPostService;
import com.mercadolibre.be_java_hisp_w31_g3.service.IProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final IPostService postService;

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<PostResponseDto> getFollowedByUserId(@PathVariable Long userId,
                                                               @RequestParam(required = false, defaultValue = "") String order) {
        return new ResponseEntity<>(productService.getPostFollowed(userId, order), HttpStatus.OK);
    }

}
