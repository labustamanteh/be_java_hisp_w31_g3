package com.mercadolibre.be_java_hisp_w31_g3.controller;

import com.mercadolibre.be_java_hisp_w31_g3.service.IUserService;
import com.mercadolibre.be_java_hisp_w31_g3.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    IUserService userService;

    public Controller(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<?> addFollower(@PathVariable Long userId, @PathVariable Long userIdToFollow){
        userService.addFollower(userId, userIdToFollow);
        return new ResponseEntity<>("todo OK", HttpStatus.OK);
    }
}
