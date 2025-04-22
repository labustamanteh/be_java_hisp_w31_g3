package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.ProductDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.ConflictException;
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
import java.util.function.Predicate;
import java.util.Comparator;
import java.util.List;
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
                        .map(post -> PostDto.builder()
                                .postId(post.getPostId())
                                .userId(post.getUserId())
                                .date(post.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                                .product(mapper.convertValue(post.getProduct(), ProductDto.class))
                                .categoryId(post.getCategoryId())
                                .price(post.getPrice())
                                .build()
                        )).collect(Collectors.toList());

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
        validatePostDate(postDto.getDate());
        checkUserExists(postDto.getUserId());
        checkProductUniqueness(postDto);

        ProductDto productDto = postDto.getProduct();
        Post post = createPost(postDto, productDto);

        userRepository.addPost(postDto.getUserId(), post);
    }

    private void validatePostDate(String date) {
        if (date.trim().isEmpty()) {
            throw new BadRequestException("La fecha no puede estar vacía");
        }

        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeException e) {
            throw new BadRequestException("La fecha no está en el formato adecuado dd-MM-yyyy");
        } catch (Exception e) {
            throw new BadRequestException("Error: " + e.getMessage());
        }
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.isAnyMatch(user -> user.getUserId().equals(userId))) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
    }

    private void checkProductUniqueness(PostDto postDto) {
        Predicate<PostDto> postDtoPredicate = post -> post.getProduct().getProductName().equals(postDto.getProduct().getProductName())
                && post.getProduct().getBrand().equals(postDto.getProduct().getBrand())
                && post.getProduct().getType().equals(postDto.getProduct().getType())
                && post.getProduct().getColor().equals(postDto.getProduct().getColor())
                && post.getProduct().getNotes().equals(postDto.getProduct().getNotes());

        Optional<PostDto> exactMatch = getPostList().stream()
                .filter(postDtoPredicate.and(post -> !post.getProduct().getProductId().equals(postDto.getProduct().getProductId()))).findFirst();

        if (exactMatch.isPresent()) {
            throw new ConflictException("Un producto con las mismas características ya existe, id: " + exactMatch.get().getProduct().getProductId());
        }

        boolean hasProductWithDifferentCharacteristics = getPostList().stream()
                .anyMatch(post -> post.getProduct().getProductId().equals(postDto.getProduct().getProductId()))
                && getPostList().stream().noneMatch(postDtoPredicate);

        if (hasProductWithDifferentCharacteristics) {
            throw new ConflictException("Un producto con el id ingresado con diferentes características ya existe. Ingrese un id diferente.");
        }
    }

    private Post createPost(PostDto postDto, ProductDto productDto) {
        LocalDate formattedDate = LocalDate.parse(postDto.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Product product = Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .type(productDto.getType())
                .brand(productDto.getBrand())
                .color(productDto.getColor())
                .notes(productDto.getNotes())
                .build();

        return Post.builder()
                .postId(Post.getGeneratedId())
                .price(postDto.getPrice())
                .date(formattedDate)
                .categoryId(postDto.getCategoryId())
                .userId(postDto.getUserId())
                .product(product)
                .hasPromo(postDto.getHasPromo())
                .discount(postDto.getDiscount())
                .build();
    }

    @Override
    public UserDto getPromoPostByUserId(Long userId){
        Optional<User> userOptional = userRepository.getById(userId);
        if (userOptional.isEmpty()){
            throw new NotFoundException("Usuario no encontrado");
        }

        User user = userOptional.get();
        List<PostDto> promoPosts = user.getPosts().stream()
                .filter(Post::getHasPromo)
                .map(post -> mapper.convertValue(post, PostDto.class))
                .toList();

        if(promoPosts.isEmpty()){
            throw new NotFoundException("No hay Productos en promoción");
        }

        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .posts(promoPosts).build();
    }

    @Override
    public List<PostDto> getPostList(){
        return userRepository.getAll().stream()
                .flatMap(user -> user.getPosts().stream())
                .map(post -> PostDto.builder()
                        .postId(post.getPostId())
                        .userId(post.getUserId())
                        .date(post.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                        .product(mapper.convertValue(post.getProduct(), ProductDto.class))
                        .categoryId(post.getCategoryId())
                        .price(post.getPrice())
                        .hasPromo(post.getHasPromo())
                        .discount(post.getDiscount())
                        .build()
                ).toList();
    }

    @Override
    public UserDto getPromoPostCount(Long userId) {
        Optional<User> optionalUser = userRepository.getById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("No se encontró el usuario con el id ingresado");
        }
        User user = optionalUser.get();
        Long postWithPromoCount = user.getPosts().stream().filter(Post::getHasPromo).count();

        return UserDto.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .promoProductsCount(postWithPromoCount).build();
    }

    @Override
    public List<PostDto> getPostsByFilter(String discount, String categoryId, String color, String hasPromo) {
        if (discount.isEmpty() && categoryId.isEmpty() && color.isEmpty() ) {
            throw new BadRequestException("No hay ningún valor en alguno de los filtros para producir un resultado");
        }

        Predicate<PostDto> postPredicate = createPostFilterPredicate(discount, categoryId, color, hasPromo);

        List<PostDto> postDtos = getPostList().stream()
                                .filter(postPredicate)
                                .toList();

        if (postDtos.isEmpty()) {
            throw new NotFoundException("No se encontraron publicaciones con el filtro proporcionado.");
        }

        return postDtos;
    }

    private static Predicate<PostDto> createPostFilterPredicate(String discount, String categoryId, String color, String hasPromo) {
        Predicate<PostDto> postPredicate = p -> true;

        if (!hasPromo.isEmpty()) {
            boolean hasPromoBoolean = Boolean.parseBoolean(hasPromo);
            postPredicate = p -> p.getHasPromo().equals(hasPromoBoolean);
        }

        if (!discount.isEmpty()) {
            double discountDouble = Double.parseDouble(discount);
            if (discountDouble > 1 || discountDouble < 0) {
                throw new BadRequestException("El valor del descuento no es válido");
            } else {
                postPredicate = postPredicate.and(p -> p.getDiscount().equals(discountDouble));
            }
        }

        if (!categoryId.isEmpty()) {
            long categoryIdLong = Long.parseLong(categoryId);
            if (categoryIdLong != 0) {
                postPredicate = postPredicate.and(p -> p.getCategoryId().equals(categoryIdLong));
            }
        }

        if (!color.isEmpty()) {
            postPredicate = postPredicate.and(p -> p.getProduct().getColor().toLowerCase()
                    .contains(color.toLowerCase()));
        }
        return postPredicate;
    }

}
