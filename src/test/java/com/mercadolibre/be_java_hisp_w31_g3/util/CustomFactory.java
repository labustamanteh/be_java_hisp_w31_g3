package com.mercadolibre.be_java_hisp_w31_g3.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomFactory {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final ObjectWriter writer = mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false).writer();

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

    public static User getUserFollowedListAsc(){
        User user1 = new User();
        user1.setUserName("Jane Smith");

        User user2 = new User();
        user2.setUserName("Alice Johnson");

        User user3 = new User();
        user3.setUserName("Bob Brown");

        user3.getFollowed().add(user1);
        user3.getFollowed().add(user2);

        return user3;
    }

    public static User getUserFollowedListDesc(){
        User user1 = new User();
        user1.setUserName("Alice Johnson");

        User user2 = new User();
        user2.setUserName("Jane Smith");

        User user3 = new User();
        user3.setUserName("Bob Brown");

        user3.getFollowed().add(user1);
        user3.getFollowed().add(user2);

        return user3;
    }

    public static User getUserWithUserName(String userName) {
        User user = new User();
        user.setUserName(userName);
        return user;
    }

    public static Post getPostWithPromo(long userId, LocalDate promoDate) {
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
                .hasPromo(true)
                .build();
    }

    public static PostDto getPost(){
        ProductDto productDto = ProductDto.builder()
                .productId(5L)
                .productName("Silla")
                .type("Household")
                .brand("Racer")
                .color("Black")
                .notes("Special Edition")
                .build();

        return PostDto.builder()
                .userId(2L)
                .date("01-04-2025")
                .product(productDto)
                .categoryId(100L)
                .price(700.00)
                .hasPromo(true)
                .discount(0.15)
                .build();
    }
}
