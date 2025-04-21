package com.mercadolibre.be_java_hisp_w31_g3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseDto {
    private Long user_id;
    private List<PostDto> posts;

}

