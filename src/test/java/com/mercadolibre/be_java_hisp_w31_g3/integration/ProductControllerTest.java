package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.BadRequestException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.ConflictException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import com.mercadolibre.be_java_hisp_w31_g3.util.PostMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.getAll().clear();
        user = CustomFactory.getUserWithUserName("Spencer");
        userRepository.add(user);
    }

    @Test
    public void addPost_ValidInput_CreatesPost() throws Exception {
        // Arrange
        Post post = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        PostDto postDto = PostMapper.convertToPostDto(post);
        String postPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postPayload))
                .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    public void addPost_EmptyDate_ThrowsBadRequestException() throws Exception {
        // Arrange
        Post post = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        PostDto postDto = PostMapper.convertToPostDto(post);
        postDto.setDate(null);
        String postPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.date[0]").value("La fecha no puede estar vacía"))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class,
                        result.getResolvedException()));
    }

    @Test
    public void addPost_WrongFormatInDate_ThrowsBadRequestException() throws Exception {
        // Arrange
        Post post = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        PostDto postDto = PostMapper.convertToPostDto(post);
        postDto.setDate("2025-05-05");
        String postPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors.date[0]").value("La fecha debe tener formato dd-MM-yyyy"))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class,
                        result.getResolvedException()));
    }

    @Test
    public void addPost_NonExistentUserId_ThrowsNotFoundException() throws Exception {
        // Arrange
        long id = 1000L;
        String expectedMessage = "No se encontró el usuario con el id ingresado";
        Post post = CustomFactory.getPostWithoutPromo(id, LocalDate.now());
        PostDto postDto = PostMapper.convertToPostDto(post);
        String postPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                        result.getResolvedException()));
    }

    @Test
    public void addPost_ExistingProductWithDifferentId_ThrowsConflictException() throws Exception {
        // Arrange
        Post post = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        String postPayload = objectMapper.writeValueAsString(PostMapper.convertToPostDto(post));
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andDo(print()).andExpect(status().isCreated());

        long productId = 1000L;
        String expectedMessage = "Un producto con las mismas características ya existe, id: " + post.getProduct().getProductId();
        post.getProduct().setProductId(productId);
        PostDto postDto = PostMapper.convertToPostDto(post);
        String postWithExistingProductPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postWithExistingProductPayload))
                .andDo(print()).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(result -> assertInstanceOf(ConflictException.class,
                        result.getResolvedException()));
    }

    @Test
    public void addPost_ExistingProductWithDifferentCharacteristics_ThrowsConflictException() throws Exception {
        // Arrange
        Post post = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        String postPayload = objectMapper.writeValueAsString(PostMapper.convertToPostDto(post));
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andDo(print()).andExpect(status().isCreated());

        String expectedMessage = "Un producto con el id ingresado con diferentes características ya existe. Ingrese un id diferente.";
        Post postWithExistingProduct = CustomFactory.getPostWithProductWithDifferentCharacteristics(user.getUserId(),
                post.getProduct().getProductId(), LocalDate.now());
        PostDto postDto = PostMapper.convertToPostDto(postWithExistingProduct);
        String postWithExistingProductPayload = objectMapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postWithExistingProductPayload))
                .andDo(print()).andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(result -> assertInstanceOf(ConflictException.class,
                        result.getResolvedException()));
    }

    @Test
    void createPost_ValidPost_ReturnTrue() throws Exception {
        // Arrange
        PostDto postDto = CustomFactory.getPost();
        String postJson = CustomFactory.generateFromDto(postDto);
        // Act & Assert
        mockMvc.perform(post("/products/promo-post").contentType(MediaType.APPLICATION_JSON).content(postJson)).andExpect(status().isCreated()).andExpect(content().string(""));
    }

     @Test
    void createPost_InvalidDateFormat_ReturnBadRequest() throws Exception {
        // Arrange
        PostDto postDto = CustomFactory.getPost();
        postDto.setDate("2025/04/01");
        String postJson = CustomFactory.generateFromDto(postDto);
        // Act & Assert
        mockMvc.perform(post("/products/promo-post").contentType(MediaType.APPLICATION_JSON).content(postJson)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.date[0]").value("La fecha debe tener formato dd-MM-yyyy"));
    }

    @Test
    void createPost_InvalidUserId_ReturnBadRequest() throws Exception {
        // Arrange
        PostDto postDto = CustomFactory.getPost();
        postDto.setUserId(null);

        String postJson = CustomFactory.generateFromDto(postDto);
        // Act & Assert
        mockMvc.perform(post("/products/promo-post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.user.getUserId()[0]").value("El id no puede estar vacío"));
    }

    @Test
    void createPost_InvalidNegativeUserId_ReturnBadRequest() throws Exception {
        // Arrange
        PostDto postDto = CustomFactory.getPost();
        postDto.setUserId(0L);

        String postJson = CustomFactory.generateFromDto(postDto);
        // Act & Assert
        mockMvc.perform(post("/products/promo-post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.user.getUserId()[0]").value("El id debe ser mayor a cero"));
    }

    @Test
    public void getPromoPostCount_ValidUserId_ReturnsPromoPostCount() throws Exception {
        // Arrange
        Post post1 = CustomFactory.getPostWithoutPromo(user.getUserId(), LocalDate.now());
        post1.setHasPromo(true);
        user.getPosts().add(post1);

        // Act
        MvcResult result = this.mockMvc.perform(get("/products/promo-post/count?user_id=" + user.getUserId())).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        UserDto expected = CustomFactory.generateFromJson(result.getResponse().getContentAsString(), UserDto.class);
        assertEquals(1L, expected.getPromoProductsCount());
    }

    @Test
    public void getPromoPostCount_InvalidId_ThrowsNotFoundException() throws Exception {
        // Act & Assert
        this.mockMvc.perform(get("/products/promo-post/count?user_id=1000"))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No se encontró el usuario con el id ingresado"))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @Test
        public void getPromoPosts_validId_returnPromoList() throws Exception {
                // arrange
                userRepository.getAll().clear();
                Post payLoadJson = CustomFactory.createPromoPost();
                String expectedResponse = CustomFactory.promoListResponse();
                user.setUserId(2L);
                user.setUserName("Jane Smith");
                user.setPosts(List.of(payLoadJson));
                userRepository.add(user);

                // act & assert
                MvcResult response = this.mockMvc.perform(get("/products/promo-post/list").param("user_id", "2"))
                                .andDo(print()).andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
                Assertions.assertEquals(expectedResponse, response.getResponse().getContentAsString());
        }

        @Test
        public void getPromoPostTest_invalidId_throwsNotFound() throws Exception {
                // arrange
                String userId = "220";
                String expectedErrorMessage = "No se encontró el usuario con el id ingresado";

                // act & assert
                this.mockMvc.perform(get("/products/promo-post/list").param("user_id", userId))
                                .andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
        }

        @Test
        public void getPromoTest_isEmptyPromo_throwsNotFound() throws Exception {
                // arrange
                String userId = "6";
                String expectedErrorMessage = "No hay Productos en promoción";
                System.out.print(user);
                // act & assert
                this.mockMvc.perform(get("/products/promo-post/list").param("user_id", userId))
                                .andDo(print()).andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedErrorMessage));
        }

        @Test
        public void getPostListWithFiltersTest_validFilters_returnFilteredList() throws Exception {
                // arrange
                String discount = "0.15";
                String category = "100";
                String color = "Black";
                String has_promo = "true";
                Post payLoad = CustomFactory.createPromoPost();
                user.setPosts(List.of(payLoad));
                String expectedResponse = CustomFactory.promoListWithFiltersResponse();

                // act & assert
                

                MvcResult result = this.mockMvc.perform(get("/products/post/list").param("discount", discount)
                                .param("category", category).param("color", color).param("hasPromo", has_promo))
                                .andDo(print())
                                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andReturn();
                Assertions.assertEquals(expectedResponse, result.getResponse().getContentAsString());
        }

        @Test
        public void getPostList_NotFoundPromoWithFilters_returnNotFoundException() throws Exception {
                // arrange
                String discount = "0.155";
                String category = "10034";
                String color = "Blacksss";
                String has_promo = "false";
                String expectedMessageResult = "No se encontraron publicaciones con el filtro proporcionado.";

                // act & assert
                this.mockMvc.perform(get("/products/post/list").param("discount", discount)
                                .param("category", category).param("color", color).param("hasPromo", has_promo))
                                .andDo(print())
                                .andExpect(status().isNotFound())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedMessageResult));

        }

        @Test
        public void getPostListTest_discountInvalid_returnBadRequestException() throws Exception {
                String discount = "-3";
                String expectedResultMessage = "El valor del descuento no es válido";

                // act & assert
                this.mockMvc.perform(get("/products/post/list").param("discount", discount))
                                .andDo(print()).andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(result -> assertInstanceOf(BadRequestException.class,
                                                result.getResolvedException()))
                                .andExpect(jsonPath("$.message").value(expectedResultMessage));
        }

    @Test
    @DisplayName("T-0012 - US-0006: obtiene los post de los seguidos por el usuario ")
    void getFollowedByUserId_ReturnJsonWithPost() throws Exception {
        //Arrange
        User user1 = CustomFactory.getUserWithFollowedListAndPosts();
        userRepository.add(user1);
        LocalDate threshold = LocalDate.now().minusWeeks(2);
        List<Long> expectedIdsLast2Weeks = user1.getFollowed().get(0).getPosts()
                .stream()
                .filter(p -> p.getDate().isAfter(threshold))
                .map(Post::getPostId)
                .toList();
        //Act & Assert
        mockMvc.perform(get("/products/followed/{userId}/list", user1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user1.getUserId()))
                .andExpect(jsonPath("$.posts.length()").value(expectedIdsLast2Weeks.size()));
    }

    @Test
    @DisplayName("T-0012 - US-0006: devuelve un error, no se encontró al usuario")
    void getFollowedByUserId_ReturnNotFoundUser() throws Exception {
        // Arrange
        long missingId = 999L;
        // Act & Assert
        mockMvc.perform(get("/products/followed/{userId}/list", missingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("No se encontró el usuario con el id ingresado"));
    }

    @Test
    @DisplayName("T-0012 - US-0006: devuelve una lista vacia")
    void getFollowedByUserId_ReturnEmptyListUser() throws Exception {
        //Arrange
        User user1 = new User();
        userRepository.add(user1);
        LocalDate threshold = LocalDate.now().minusWeeks(2);
        //Act & Assert
        mockMvc.perform(get("/products/followed/{userId}/list", user1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user1.getUserId()))
                .andExpect(jsonPath("$.posts").isEmpty());

        }
}