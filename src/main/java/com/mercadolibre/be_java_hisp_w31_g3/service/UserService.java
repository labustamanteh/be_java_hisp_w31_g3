package com.mercadolibre.be_java_hisp_w31_g3.service;

import com.mercadolibre.be_java_hisp_w31_g3.dto.UserDto;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import com.mercadolibre.be_java_hisp_w31_g3.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final UserDto.UserDtoBuilder userDtoBuilder = UserDto.getUserDtoBuilder();

    @Override
    public UserDto getFollowersById(Long id, String order) {
        if (!userRepository.existsById(id)) {
            // TODO: throw not found
        }

        User user = userRepository.getById(id);
        List<User> orderedFollowers = getUserListOrderedByName(order, user.getFollowers());
        orderedFollowers.forEach(u -> {
            u.setFollowed(null);
            u.setFollowers(null);
        });

        return userDtoBuilder.withUserId(user.getUserId())
                .withUserName(user.getUserName())
                .withFollowers(orderedFollowers)
                .build();
    }

    private static List<User> getUserListOrderedByName(String order, List<User> userList) {
        switch (order) {
            case "name_asc":
                userList = userList.stream().sorted(Comparator.comparing(User::getUserName)).toList();
                break;
            case "name_desc":
                userList = userList.stream().sorted(Comparator.comparing(User::getUserName).reversed()).toList();
                break;
        }
        return userList;
    }
}
