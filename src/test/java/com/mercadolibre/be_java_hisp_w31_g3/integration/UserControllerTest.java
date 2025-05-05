package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private long userId1;
    private long userId2;

    @BeforeEach
    void setUp() {
        userRepository.getAll().clear();

        User user1 = new User();
        User user2 = new User();

        userRepository.add(user1);
        userRepository.add(user2);

        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
    }

    @Test
    void addFollowerSuccess() throws Exception {
        mockMvc.perform(post("/users/" + userId2 + "/follow/" + userId1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addFollower_InvalidUserId_ThrowsNotFoundException() throws Exception {
        mockMvc.perform(post("/users/" + userId2 + "/follow/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Alguno de los usuarios no existe."))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @Test
    void unfollowUserSuccess() throws Exception {
        mockMvc.perform(post("/users/" + userId2 + "/follow/" + userId1))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/" + userId2 + "/unfollow/" + userId1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void unfollowUser_InvalidUserId_ThrowsNotFoundException() throws Exception {
        mockMvc.perform(put("/users/" + userId2 + "/unfollow/99"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("No se encontró el usuario con el id ingresado"))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @Test
    void followersCountSuccess() throws Exception {
        mockMvc.perform(post("/users/" + userId2 + "/follow/" + userId1))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/" + userId1 + "/followers/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followers_count").value(1));
    }
}
