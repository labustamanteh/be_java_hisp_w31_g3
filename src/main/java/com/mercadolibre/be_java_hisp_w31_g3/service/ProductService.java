package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostResponseDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IProductRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final IProductRepository productRepository;
    private final IUserRepository userRepository;


    @Override
    public PostResponseDto getPostFollowed(Long id, String order) {
        Optional<User> user = userRepository.getById(id);
        if (user.isEmpty())
            throw new NotFoundException("No se encontró un usuario con el Id enviado.");
        LocalDate date = LocalDate.now().minusWeeks(2);

        List<PostDto> posts = user.get().getFollowed().stream()
                .flatMap(u -> u.getPosts().stream()
                        .filter(post -> post.getDate().isAfter(date))
                        .map(post -> {
                            Product prod = post.getProduct();
                            ProductDto productDTO = new ProductDto(
                                    prod.getProductId(),
                                    prod.getType(),
                                    prod.getBrand(),
                                    prod.getProductName(),
                                    prod.getColor(),
                                    prod.getNotes()
                            );

                            return new PostDto(
                                    post.getUserId(),
                                    post.getPostId(),
                                    post.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                                    productDTO,
                                    post.getCategory(),
                                    post.getPrice()
                            );
                        })
                )
                .collect(Collectors.toList());

        return new PostResponseDto(user.get().getUserId(), getPostListOrderedByDate(order, posts));
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
}
