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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        userRepository.getAll().clear();
        user1 = CustomFactory.getUserWithUserName("Spencer");
        user2 = CustomFactory.getUserWithUserName("Garret");
        user3 = CustomFactory.getUserWithUserName("Camille");
        userRepository.add(user1);
        userRepository.add(user2);
        userRepository.add(user3);
        userId1 = user1.getUserId();
        userId2 = user2.getUserId();
    }

    @Test
    void addFollower_ValidUsers_FollowerAdded() throws Exception {
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
    void unfollowUser_ValidUsers_UnfollowedSuccessfully() throws Exception {
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
    void followersCount_ValidUser_ReturnsCorrectCount() throws Exception {
        mockMvc.perform(post("/users/" + userId2 + "/follow/" + userId1))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/" + userId1 + "/followers/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.followers_count").value(1));
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
                .andExpect(jsonPath("$.message").value(expectedMessage));
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
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private void followUser(User user1, User user2) throws Exception {
        this.mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", user1.getUserId(),
                        user2.getUserId()))
                .andDo(print()).andExpect(status().isOk());
    }
}
