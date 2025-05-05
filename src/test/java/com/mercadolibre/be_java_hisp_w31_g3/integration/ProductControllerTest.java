package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.ConflictException;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import com.mercadolibre.be_java_hisp_w31_g3.util.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

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

}
