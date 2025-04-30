package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import com.mercadolibre.be_java_hisp_w31_g3.util.PostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    IUserRepository userRepository;

    @InjectMocks
    PostService postService;

    @Test
    public void getPostFollowed_FollowedWithOnePostFromLastTwoWeeks_ReturnsOnePostFromFollowed(){
        // Arrange - Given
        LocalDate date1 = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = LocalDate.now().minusWeeks(4);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));

        // Act - When
        UserDto response = postService.getPostFollowed(userId, "");

        // Assert - Then
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());

        String responsePostDate = response.getPosts().get(0).getDate();
        LocalDate responsePostLocalDate = LocalDate.parse(responsePostDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertTrue(responsePostLocalDate.isAfter(LocalDate.now().minusWeeks(2)));
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsLongerThanTwoWeeks_ReturnsNoPostsFromFollowed(){
        // Arrange - Given
        LocalDate date1 = LocalDate.now().minusWeeks(6);
        LocalDate date2 = LocalDate.now().minusWeeks(4);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));

        // Act - When
        UserDto response = postService.getPostFollowed(userId, "");

        // Assert - Then
        assertNotNull(response);
        assertEquals(0, response.getPosts().size());
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsInThePastTwoWeeks_ReturnsTwoPostsFromFollowedOrderedAsc(){
        // Arrange - Given
        LocalDate date1 = LocalDate.now().minusDays(3);
        LocalDate date2 = LocalDate.now().minusWeeks(1);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));
        List<PostDto> expectedPosts = PostMapper.convertPostsToDtos(user.getFollowed().get(0).getPosts());

        // Act - When
        UserDto response = postService.getPostFollowed(userId, "date_asc");

        // Assert - Then
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());
        assertEquals(expectedPosts.get(0).getDate(), response.getPosts().get(1).getDate());
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsInThePastTwoWeeks_ReturnsTwoPostsFromFollowedOrderedDesc(){
        // Arrange - Given
        LocalDate date1 = LocalDate.now().minusDays(3);
        LocalDate date2 = LocalDate.now().minusWeeks(1);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));
        List<PostDto> expectedPosts = PostMapper.convertPostsToDtos(user.getFollowed().get(0).getPosts());

        // Act - When
        UserDto response = postService.getPostFollowed(userId, "date_desc");

        // Assert - Then
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());
        assertEquals(expectedPosts.get(0).getDate(), response.getPosts().get(0).getDate());
    }
}
