package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void SetUp () {
        userRepository.getAll().clear();
    }

    @Test
    public void testAddFollower_Success() throws Exception {
        User user1 = new User();
        user1.setUserId(1L);

        User user2 = new User();
        user2.setUserId(2L);

        userRepository.add(user1);
        userRepository.add(user2);

        mockMvc.perform(post("/users/1/follow/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddFollower_InvalidUserId_ThrowsException() throws Exception {
        User user1 = new User();
        user1.setUserId(1L);

        userRepository.add(user1);

        mockMvc.perform(post("/users/1/follow/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUnfollowUser_Success() throws Exception {
        User user1 = new User();
        user1.setUserId(1L);

        User user2 = new User();
        user2.setUserId(2L);
        userRepository.add(user1);
        userRepository.add(user2);

        mockMvc.perform(post("/users/1/follow/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/1/unfollow/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUnfollowUser_InvalidUserId_ThrowsException() throws Exception {
        User user1 = new User();
        user1.setUserId(1L);

        userRepository.add(user1);

        mockMvc.perform(put("/users/1/unfollow/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}