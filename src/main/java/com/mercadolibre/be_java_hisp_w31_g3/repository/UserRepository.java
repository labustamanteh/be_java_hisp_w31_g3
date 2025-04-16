package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class UserRepository implements IUserRepository {
    private List<User> usersList = new ArrayList<>();

    public UserRepository() throws IOException {
        loadDataBase();
    }

    private void loadDataBase() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users;

        file = ResourceUtils.getFile("classpath:usersWithoutFollowers.json");
        users = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        usersList = users;
    }

    @Override
    public List<User> getUsers() {
        return usersList;
    }

    @Override
    public void addFollower(Long userId, Long userToFollow){
        User userFollower = usersList.stream().filter(user -> user.getUserId()
                        .equals(userId)).findFirst()
                        .orElse(null);
        User userFollowed = usersList.stream().filter(user -> user.getUserId()
                        .equals(userToFollow)).findFirst()
                        .orElse(null);
        userFollowed.getFollowers().add(userFollower);

    }

    @Override
    public boolean existsById(Long userId) {
        return usersList.stream()
                .anyMatch(user -> Objects.equals(user.getUserId(), userId));
    }
}
