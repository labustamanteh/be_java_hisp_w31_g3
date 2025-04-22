package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IUserRepository userRepository;
    private final ObjectMapper mapper;

    @Override
    public UserDto getPostFollowed(Long id, String order) {
        Optional<User> user = userRepository.getById(id);
        if (user.isEmpty())
            throw new NotFoundException("No se encontró un usuario con el Id enviado.");
        LocalDate date = LocalDate.now().minusWeeks(2);

        List<PostDto> posts = user.get().getFollowed().stream()
                .flatMap(u -> u.getPosts().stream()
                        .filter(post -> post.getDate().isAfter(date))
                        .map(post -> PostDto
                                .builder()
                                .postId(post.getPostId())
                                .userId(post.getUserId())
                                .date(post.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                .product(mapper.convertValue(post.getProduct(), ProductDto.class))
                                .categoryId(post.getCategoryId())
                                .price(post.getPrice())
                                .build()

                        ))
                .collect(Collectors.toList());

        return UserDto.builder().userId(id).posts(getPostListOrderedByDate(order, posts)).build();
    }

    private List<PostDto> getPostListOrderedByDate(String order, List<PostDto> postList) {
        switch (order) {
            case "date_asc":
                postList = postList.stream().sorted(Comparator.comparing(PostDto::getDate)).toList();
                break;
            case "date_desc":
                postList = postList.stream().sorted(Comparator.comparing(PostDto::getDate).reversed()).toList();
                break;
        }
        return postList;
    }

    @Override
    public void addPost(PostDto postDto) {
        if (postDto.getDate().trim().isEmpty()) {
            throw new BadRequestException("La fecha no puede estar vacía");
        }

        LocalDate formattedDate;
        try {
            formattedDate = LocalDate.parse(postDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeException e) {
            throw new BadRequestException("La fecha no está en el formato adecuado dd-MM-yyyy");
        } catch (Exception e) {
            throw new BadRequestException("Error: " + e.getMessage());
        }

        if (!userRepository.isAnyMatch(user -> user.getUserId().equals(postDto.getUserId()))) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }

        ProductDto productDto = postDto.getProduct();
        Post post = Post.builder()
                .postId(Post.getGeneratedId())
                .price(postDto.getPrice())
                .date(formattedDate)
                .categoryId(postDto.getCategoryId())
                .userId(postDto.getUserId())
                .product(Product.builder()
                        .productId(productDto.getProductId())
                        .productName(productDto.getProductName())
                        .type(productDto.getType())
                        .brand(productDto.getBrand())
                        .color(productDto.getColor())
                        .notes(productDto.getNotes())
                        .build())
                .hasPromo(postDto.getHasPromo())
                .discount(postDto.getDiscount())
                .build();
        userRepository.addPost(postDto.getUserId(), post);
    }

    @Override
    public UserDto getPromoPostByUserId(Long userId) {
        Optional<User> userOptional = userRepository.getById(userId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Usuario no encontrado");
        }

        User user = userOptional.get();
        List<PostDto> promoPosts = user.getPosts().stream()
                .filter(Post::getHasPromo)
                .map(post -> {
                    String formattedDate = post.getDate().format(formatter);
                    return mapper.convertValue(PostDto.builder()
                            .postId(post.getPostId())
                            .userId(post.getUserId())
                            .date(formattedDate)
                            .product(mapper.convertValue(post.getProduct(), ProductDto.class))
                            .categoryId(post.getCategoryId())
                            .price(post.getPrice())
                            .hasPromo(post.getHasPromo())
                            .discount(post.getDiscount())
                            .build(), PostDto.class);
                })
                .toList();

        if (promoPosts.isEmpty()) {
            throw new NotFoundException("No hay Productos en promoción");
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .posts(promoPosts).build();
    }


    @Override
    public UserDto getPromoPostCount(Long userId) {
        Optional<User> optionalUser = userRepository.getById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
        User user = optionalUser.get();
        Long postWithPromoCount = user.getPosts().stream().filter(p -> p.getHasPromo()).count();

        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .promoProductsCount(postWithPromoCount).build();
    }
}
