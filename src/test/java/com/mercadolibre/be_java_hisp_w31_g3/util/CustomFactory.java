package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class CustomFactory {

    public static Post getPostWithoutPromo(long userId, LocalDate promoDate) {
        Product product = Product.builder()
                .productId(1L)
                .productName("product1")
                .brand("brand1")
                .color("red")
                .type("type1")
                .build();

        return Post.builder()
                .postId(Post.getGeneratedId())
                .userId(userId)
                .date(promoDate)
                .product(product)
                .categoryId(1L)
                .price(200.0)
                .build();
    }

    public static User getUserWithFollowedWithTwoPosts(LocalDate datePost1, LocalDate datePost2) {
        User userInFollowed = new User();
        userInFollowed.setUserName("user1");
        userInFollowed.getPosts().add(getPostWithoutPromo(userInFollowed.getUserId(), datePost1));
        userInFollowed.getPosts().add(getPostWithoutPromo(userInFollowed.getUserId(), datePost2));

        User user = new User();
        user.setUserName("user2");

        user.getFollowed().add(userInFollowed);
        userInFollowed.getFollowers().add(user);
        return user;
    }
}
