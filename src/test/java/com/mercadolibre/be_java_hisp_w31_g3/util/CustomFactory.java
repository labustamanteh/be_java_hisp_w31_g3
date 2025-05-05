package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final ObjectWriter writer = mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false).writer();

    public static Post getPostWithoutPromo(long userId, LocalDate postDate) {
        Product product = Product.builder()
                .productId(1L)
                .productName("product1")
                .brand("brand1")
                .color("red")
                .type("type1")
                .notes("notes")
                .build();

        return Post.builder()
                .postId(Post.getGeneratedId())
                .userId(userId)
                .date(postDate)
                .product(product)
                .categoryId(1L)
                .price(200.0)
                .build();
    }

    public static Post getPostWithProductWithDifferentCharacteristics(long userId, long productId, LocalDate postDate) {
        Product product = Product.builder()
                .productId(productId)
                .productName("product2")
                .brand("brand2")
                .color("black")
                .type("type2")
                .notes("notes2")
                .build();

        return Post.builder()
                .postId(Post.getGeneratedId())
                .userId(userId)
                .date(postDate)
                .product(product)
                .categoryId(1L)
                .price(200.0)
                .build();
    }

    public static User getUserWithOneFollowedWithTwoPosts(LocalDate datePost1, LocalDate datePost2) {
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

    public static User getUserWithUserName(String userName) {
        User user = new User();
        user.setUserName(userName);
        return user;
    }

    public static User  getFollowersCount(Long userId){
         User user = new User(userId, "Lady", List.of(new User(), new User()), new ArrayList<User>(),
                new ArrayList<Post>());

        return user;
    }

    public static <T> T generateFromJson(String data, Class<T> classType) throws JsonProcessingException {
        return mapper.readValue(data, classType);
    }

    public static String generateFromDto(Object dto) throws JsonProcessingException {
        return writer.writeValueAsString(dto);
    }

}
