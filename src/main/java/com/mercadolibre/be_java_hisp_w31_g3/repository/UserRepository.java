package com.mercadolibre.be_java_hisp_w31_g3.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadolibre.be_java_hisp_w31_g3.model.User;

@Repository
public class UserRepository implements IUserRepository {

    List<User> listofUsers = new ArrayList<>();

    public UserRepository() throws IOException {
        loadDataBase();
    }

    private void loadDataBase() throws IOException {
        File file;
        ObjectMapper objectMapper = new ObjectMapper();
        List<User> users;

        file = ResourceUtils.getFile("classpath:db_users.json");
        users = objectMapper.readValue(file, new TypeReference<List<User>>() {
        });

        listofUsers = users;
    }

    @Override
    public User getUserById(Long userId) {
        return listofUsers.stream().filter(u -> u.getUserId().equals(userId)).findFirst().get();
    }

    @Override
    public Boolean existsById(Long userId) {
        return listofUsers.stream().anyMatch(u -> userId.equals(u.getUserId()));
    }
}
