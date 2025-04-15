package com.mercadolibre.be_java_hisp_w31_g3.dto;

import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SellerDto extends UserDto{
    List<Buyer> buyerList;
    Integer followerCount;
    List<Post> postList;
}
