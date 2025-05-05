package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.PostDto;
import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.exception.NotFoundException;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import com.mercadolibre.be_java_hisp_w31_g3.util.CustomFactory;
import com.mercadolibre.be_java_hisp_w31_g3.util.PostMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
        // Arrange
        LocalDate date1 = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = LocalDate.now().minusWeeks(4);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDto response = postService.getPostFollowed(userId, "date_asc");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getPosts().size());

        String responsePostDate = response.getPosts().get(0).getDate();
        LocalDate responsePostLocalDate = LocalDate.parse(responsePostDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        assertTrue(responsePostLocalDate.isAfter(LocalDate.now().minusWeeks(2)));
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsLongerThanTwoWeeks_ReturnsNoPostsFromFollowed(){
        // Arrange
        LocalDate date1 = LocalDate.now().minusWeeks(6);
        LocalDate date2 = LocalDate.now().minusWeeks(4);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDto response = postService.getPostFollowed(userId, "date_asc");

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getPosts().size());
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsInThePastTwoWeeks_ReturnsTwoPostsFromFollowedOrderedAsc() {
        // Arrange
        LocalDate date1 = LocalDate.now().minusDays(3);
        LocalDate date2 = LocalDate.now().minusWeeks(1);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));
        List<PostDto> expectedPosts = PostMapper.convertPostsToDtos(user.getFollowed().get(0).getPosts());

        // Act
        UserDto response = postService.getPostFollowed(userId, "date_asc");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());
        List<PostDto> orderedExpected = expectedPosts.stream()
                .sorted(Comparator.comparing(PostDto::getDate))
                .toList();
        assertEquals(orderedExpected.get(0).getDate(), response.getPosts().get(0).getDate());
    }

    @Test
    public void getPostFollowed_FollowedWithTwoPostsInThePastTwoWeeks_ReturnsTwoPostsFromFollowedOrderedDesc() {
        // Arrange
        LocalDate date1 = LocalDate.now().minusDays(3);
        LocalDate date2 = LocalDate.now().minusWeeks(1);
        User user = CustomFactory.getUserWithFollowedWithTwoPosts(date1, date2);
        long userId = user.getUserId();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(userId)).thenReturn(Optional.of(user));
        List<PostDto> expectedPosts = PostMapper.convertPostsToDtos(user.getFollowed().get(0).getPosts());

        // Act
        UserDto response = postService.getPostFollowed(userId, "date_desc");

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getPosts().size());
        List<PostDto> orderedExpected = expectedPosts.stream()
                .sorted(Comparator.comparing(PostDto::getDate).reversed())
                .toList();
        assertEquals(orderedExpected.get(0).getDate(), response.getPosts().get(0).getDate());
    }

    @ParameterizedTest(name = "T-0005 & T-0006: Ordenamiento {0} devuelve posts ordenados por fecha")
    @MethodSource("com.mercadolibre.be_java_hisp_w31_g3.util.TestUtils#getCorrectDateOrders")
    public void getPostFollowed_FollowedPostsOrderByDate_ReturnsCorrectOrder(String order, Comparator<String> expectedComparator) {
        // Arrange
        User user = CustomFactory.getUserWithFollowedListAndPosts();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user));
        int expectedPost = 5;
        // Act
        UserDto response = postService.getPostFollowed(anyLong(), order);
        // Assert
        assertThat(response.getPosts())
                .extracting(PostDto::getDate)
                .as("Las fechas deberían venir ordenadas según %s", order)
                .isSortedAccordingTo(expectedComparator);

        assertEquals(expectedPost, response.getPosts().size());
        verify(userRepository).getById(anyLong());
    }

    @ParameterizedTest(name = "T-0005 & T-0006: Ordenamiento {0} devuelve posts ordenados mal")
    @MethodSource("com.mercadolibre.be_java_hisp_w31_g3.util.TestUtils#getWrongDateOrders")
    public void getPostFollowed_FollowedPostsOrderByDate_WrongOrder_ReturnsOrder(String order, Comparator<String> wrongComparator) {
        // Arrange
        User user = CustomFactory.getUserWithFollowedListAndPosts();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user));
        int expectedPost = 5;
        // Act
        UserDto response = postService.getPostFollowed(anyLong(), order);
        // Assert
        assertThrows(AssertionError.class, () -> {
            assertThat(response.getPosts())
                    .extracting(PostDto::getDate)
                    .isSortedAccordingTo(wrongComparator);
        });

        verify(userRepository).getById(anyLong());
    }

    @Test
    @DisplayName("T-0005 & T-0006: getPostFollowed lanza NotFoundException si el usuario no existe")
    public void getPostFollowed_FollowedPostsOrderByDate_NotFoundUser_ThrowException(){
        //Arrange
        String orderType = "date_asc";
        boolean isUserExist = false;
        when(userRepository.isAnyMatch(any())).thenReturn(isUserExist);
        //Act & Assert
        assertThrows(NotFoundException.class,
                () -> postService.getPostFollowed(anyLong(), orderType));
    }

    @Test
    @DisplayName("T-0005 & T-0006: getPostFollowed lanza NotFoundException si el tipo de orden no es válido")
    public void getPostFollowed_FollowedPostsOrderByDate_NotFoundOrderType_ThrowException(){
        //Arrange
        String orderType = "name_asc";
        User user = CustomFactory.getUserWithFollowedListAndPosts();
        when(userRepository.isAnyMatch(any())).thenReturn(true);
        when(userRepository.getById(anyLong())).thenReturn(Optional.of(user));
        //Act && Assert
        assertThrows(NotFoundException.class,
                () -> postService.getPostFollowed(anyLong(), orderType));
    }
}
