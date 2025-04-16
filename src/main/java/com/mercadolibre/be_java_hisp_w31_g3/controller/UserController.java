package com.mercadolibre.be_java_hisp_w31_g3.controller;

import com.mercadolibre.be_java_hisp_w31_g3.dto.FollowersCountDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping()
    public ResponseEntity<List<UserDto>> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountDto> getFollowersCount(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getfollowersCount(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/follow/{userIdToFollow}")
    public ResponseEntity<?> addFollower(@PathVariable Long userId, @PathVariable Long userIdToFollow){
        userService.addFollower(userId, userIdToFollow);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<UserDto> getFollowersByUserId(@PathVariable Long userId,
                                                        @RequestParam String order) {
        return new ResponseEntity<>(userService.getFollowersById(userId, order), HttpStatus.OK);
    }
}
