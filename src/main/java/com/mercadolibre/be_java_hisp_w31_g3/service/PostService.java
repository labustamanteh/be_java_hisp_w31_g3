package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.Product;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mercadolibre.be_java_hisp_w31_g3.repository.IPostRepository;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final IPostRepository productRepository;
    private final IUserRepository userRepository;
    private final ObjectMapper mapper;

    @Override
    public void addPost(PostDto postDto) {
        if(postDto.getDate().trim().isEmpty()){
            throw new BadRequestException("La fecha no puede estar vacía");
        }

        LocalDate formattedDate;
        try{
            formattedDate = LocalDate.parse(postDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }catch (DateTimeException e){
            throw new BadRequestException("La fecha no está en el formato adecuado dd-MM-yyyy");
        }catch (Exception e){
            throw new BadRequestException("Error: "  + e.getMessage());
        }

        Optional<User> userOptional = userRepository.getById(postDto.getUserId());
        if(userOptional.isEmpty()){
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }

        ProductDto productDto = postDto.getProduct();
        productRepository.add(Post.builder()
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
                .build());
    }
}
