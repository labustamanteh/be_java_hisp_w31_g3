package com.mercadolibre.be_java_hisp_w31_g3.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercadolibre.be_java_hisp_w31_g3.dto.FollowersCountDto;
import com.mercadolibre.be_java_hisp_w31_g3.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g3.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private IUserService userService;

    public UserController(UserService UserService) {
        this.userService = UserService;
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountDto> getFollowersCount(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getfollowersCount(userId), HttpStatus.OK);
    }

}
