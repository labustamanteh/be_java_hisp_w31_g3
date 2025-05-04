package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
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

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.getAll().clear();

        user1 = CustomFactory.getUserWithIdAndName(1L, "User1");
        user2 = CustomFactory.getUserWithIdAndName(2L, "User2");

        userRepository.add(user1);
        userRepository.add(user2);
    }

    @Test
    public void testAddFollower_Success() throws Exception {
        mockMvc.perform(post("/users/" + user2.getUserId() + "/follow/" + user1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddFollower_InvalidUserId_ThrowsException() throws Exception {
        mockMvc.perform(post("/users/" + user2.getUserId() + "/follow/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUnfollowUser_Success() throws Exception {
        mockMvc.perform(post("/users/" + user2.getUserId() + "/follow/" + user1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/" + user2.getUserId() + "/unfollow/" + user1.getUserId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUnfollowUser_InvalidUserId_ThrowsException() throws Exception {
        mockMvc.perform(put("/users/" + user2.getUserId() + "/unfollow/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}