package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public static User getUserWithFollowedListAndPosts() {
        LocalDate now = LocalDate.now();

        User principalUser = new User();
        principalUser.setUserId(6L);
        principalUser.setUserName("Principal User");

        User followedUser = new User();
        followedUser.setUserId(2L);
        followedUser.setUserName("Alice");

        List<Map.Entry<Long, LocalDate>> dates = List.of(
                Map.entry(1L, now.minusDays(1)),
                Map.entry(2L, now.minusDays(3)),
                Map.entry(3L, now.minusDays(7)),
                Map.entry(4L, now.minusDays(10)),
                Map.entry(5L, now.minusDays(13)),
                Map.entry(6L, now.minusWeeks(3)),
                Map.entry(7L, now.minusWeeks(4)),
                Map.entry(8L, now.minusMonths(1))
        );

        List<Post> allPosts = new ArrayList<>(dates.stream()
                .map(entry ->
                        Post.builder()
                                .postId(entry.getKey())
                                .userId(followedUser.getUserId())
                                .date(entry.getValue())
                                .product(Product.builder()
                                        .productId(entry.getKey())
                                        .productName("Producto " + entry.getKey())
                                        .type("tipo")
                                        .brand("marca")
                                        .color("color")
                                        .notes("nota")
                                        .build())
                                .price(10.0 * entry.getKey())
                                .build())
                .toList());
        Collections.shuffle(allPosts);
        followedUser.setPosts(new ArrayList<>(allPosts));
        principalUser.setFollowed(List.of(followedUser));
        return principalUser;
    }
}