package com.mercadolibre.be_java_hisp_w31_g3.controller;

import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<UserDto> getFollowersByUserId(@PathVariable Long userId,
                                                        @RequestParam String order) {
        return new ResponseEntity<>(userService.getFollowersById(userId, order), HttpStatus.OK);
    }
}
