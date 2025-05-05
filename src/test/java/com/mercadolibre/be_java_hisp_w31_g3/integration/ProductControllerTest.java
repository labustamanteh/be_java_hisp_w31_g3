package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

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