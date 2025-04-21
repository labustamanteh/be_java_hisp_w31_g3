package com.mercadolibre.be_java_hisp_w31_g3.controller;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.service.IPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final IPostService postService;

    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<UserDto> getFollowedByUserId(@PathVariable Long userId,
                                                       @RequestParam(required = false, defaultValue = "") String order) {
        return new ResponseEntity<>(postService.getPostFollowed(userId, order), HttpStatus.OK);
    }

    @PostMapping("/post")
    public ResponseEntity<Void> addPost(@RequestBody PostDto postDto){
        postService.addPost(postDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/promo-post")
    public ResponseEntity<?> createPromoPost(@RequestBody PostDto postDto){
        postService.addPost(postDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
