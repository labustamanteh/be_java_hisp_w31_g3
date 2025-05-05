package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user1;

    @BeforeEach
    void setUp() {
        user1 = CustomFactory.getUserWithUserName("Spencer");
        userRepository.add(user1);
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
                .andExpect(jsonPath("$.errors.userId[0]").value("El id no puede estar vacío"));
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
                .andExpect(jsonPath("$.errors.userId[0]").value("El id debe ser mayor a cero"));
    }

    @Test
    public void getPromoPostCount_ValidUserId_ReturnsPromoPostCount() throws Exception {
        // Arrange
        Post post1 = CustomFactory.getPostWithoutPromo(user1.getUserId(), LocalDate.now());
        post1.setHasPromo(true);
        user1.getPosts().add(post1);

        // Act
        MvcResult result = this.mockMvc.perform(get("/products/promo-post/count?user_id=" + user1.getUserId())).andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

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
