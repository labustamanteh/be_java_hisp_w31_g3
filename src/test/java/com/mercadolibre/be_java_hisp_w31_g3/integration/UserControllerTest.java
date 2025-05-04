package com.mercadolibre.be_java_hisp_w31_g3.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import com.mercadolibre.be_java_hisp_w31_g3.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = CustomFactory.getUserWithUserName("Spencer");
        user2 = CustomFactory.getUserWithUserName("Garret");
        user3 = CustomFactory.getUserWithUserName("Camille");
        userRepository.add(user1);
        userRepository.add(user2);
        userRepository.add(user3);
    }

    @Test
    public void getFollowersByUserId_ValidUserId_ReturnsFollowersList() throws Exception {
        // Arrange
        followUser(user1, user2);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followers/list", user2.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user2.getUserId()))
                .andExpect(jsonPath("$.user_name").value(user2.getUserName()))
                .andExpect(jsonPath("$.followers[0].user_id").value(user2.getFollowers().get(0).getUserId()))
                .andExpect(jsonPath("$.followers[0].user_name").value(user2.getFollowers().get(0).getUserName()))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user2);

        assertEquals(expectedUserDto.getFollowers().size(), resultingUserDto.getFollowers().size());
    }

    @Test
    public void getFollowersByUserId_ValidUserId_ReturnsFollowersListOrderedByNameAsc() throws Exception {
        // Arrange
        followUser(user1, user2);
        followUser(user3, user2);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followers/list?order=name_asc",
                        user2.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user2);

        assertEquals(expectedUserDto.getFollowers().size(), resultingUserDto.getFollowers().size());
        assertTrue(expectedUserDto.getFollowers().containsAll(resultingUserDto.getFollowers()));
        assertEquals(user3.getUserName(), resultingUserDto.getFollowers().get(0).getUserName());
        assertEquals(user1.getUserName(), resultingUserDto.getFollowers().get(1).getUserName());
    }

    @Test
    public void getFollowersByUserId_ValidUserId_ReturnsFollowersListOrderedByNameDesc() throws Exception {
        // Arrange
        followUser(user1, user2);
        followUser(user3, user2);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followers/list?order=name_desc",
                        user2.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user2);

        assertEquals(expectedUserDto.getFollowers().size(), resultingUserDto.getFollowers().size());
        assertTrue(expectedUserDto.getFollowers().containsAll(resultingUserDto.getFollowers()));
        assertEquals(user1.getUserName(), resultingUserDto.getFollowers().get(0).getUserName());
        assertEquals(user3.getUserName(), resultingUserDto.getFollowers().get(1).getUserName());
    }

    @Test
    public void getFollowersByUserId_NonExistentUserId_ThrowsNotFoundException() throws Exception {
        // Arrange
        long id = 1000L;
        String expectedMessage = "No se encontró el usuario con el id ingresado";

        // Act & Assert
        this.mockMvc.perform(get("/users/{userId}/followers/list", id))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    @Test
    public void getFollowedByUserId_ValidUserId_ReturnsFollowedList() throws Exception {
        // Arrange
        followUser(user1, user2);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followed/list", user1.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user1.getUserId()))
                .andExpect(jsonPath("$.user_name").value(user1.getUserName()))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user1);

        assertEquals(expectedUserDto.getFollowed().size(), resultingUserDto.getFollowed().size());
        assertTrue(expectedUserDto.getFollowed().containsAll(resultingUserDto.getFollowed()));
    }

    @Test
    public void getFollowedByUserId_ValidUserId_ReturnsFollowedListOrderedAsc() throws Exception {
        // Arrange
        followUser(user1, user2);
        followUser(user1, user3);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followed/list?order=name_asc",
                        user1.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user1.getUserId()))
                .andExpect(jsonPath("$.user_name").value(user1.getUserName()))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user1);

        assertEquals(expectedUserDto.getFollowed().size(), resultingUserDto.getFollowed().size());
        assertTrue(expectedUserDto.getFollowed().containsAll(resultingUserDto.getFollowed()));
        assertEquals(user3.getUserName(), resultingUserDto.getFollowed().get(0).getUserName());
        assertEquals(user2.getUserName(), resultingUserDto.getFollowed().get(1).getUserName());
    }

    @Test
    public void getFollowedByUserId_ValidUserId_ReturnsFollowedListOrderedDesc() throws Exception {
        // Arrange
        followUser(user1, user2);
        followUser(user1, user3);

        // Act
        MvcResult result = this.mockMvc.perform(get("/users/{userId}/followed/list?order=name_desc",
                        user1.getUserId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.user_id").value(user1.getUserId()))
                .andExpect(jsonPath("$.user_name").value(user1.getUserName()))
                .andReturn();

        // Assert
        UserDto resultingUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        UserDto expectedUserDto = UserMapper.getUserDto(user1);

        assertEquals(expectedUserDto.getFollowed().size(), resultingUserDto.getFollowed().size());
        assertTrue(expectedUserDto.getFollowed().containsAll(resultingUserDto.getFollowed()));
        assertEquals(user2.getUserName(), resultingUserDto.getFollowed().get(0).getUserName());
        assertEquals(user3.getUserName(), resultingUserDto.getFollowed().get(1).getUserName());
    }

    @Test
    public void getFollowedByUserId_NonExistentUserId_ThrowsNotFoundException() throws Exception {
        // Arrange
        long id = 1000L;
        String expectedMessage = "No se encontró el usuario con el id ingresado";

        // Act & Assert
        this.mockMvc.perform(get("/users/{userId}/followed/list", id))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()));
    }

    private void followUser(User user1, User user2) throws Exception {
        this.mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", user1.getUserId(),
                        user2.getUserId()))
                .andDo(print()).andExpect(status().isOk());
    }
}
