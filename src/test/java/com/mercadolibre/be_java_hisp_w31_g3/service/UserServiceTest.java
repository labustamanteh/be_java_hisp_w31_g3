package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp(){}

    @Test
    void testAddFollower() {
        // Arrange
        User user1 = new User();
        user1.setUserName("User1");
        User user2 = new User();
        user2.setUserName("User2");

        when(userRepository.getById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.getById(2L)).thenReturn(Optional.of(user2));

        // Act
        userService.addFollower(1L, 2L);

        // Assert
        assertTrue(user2.getFollowers().contains(user1));
        assertTrue(user1.getFollowed().contains(user2));
    }
}
