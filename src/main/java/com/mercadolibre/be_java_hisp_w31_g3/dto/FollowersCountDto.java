package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FollowersCountDto {
    private Long userId;
    private String userName;
    private Long followersCount;
}
