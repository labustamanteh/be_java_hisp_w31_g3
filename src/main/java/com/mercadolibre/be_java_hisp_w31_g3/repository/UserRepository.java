package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository implements IUserRepository {
    List<User> users = new ArrayList<>();

    @Override
    public User getById(Long userId) {
        return users.stream().filter(u -> u.getUserId().equals(userId)).findFirst().get();
    }

    @Override
    public Boolean existsById(Long userId) {
        return users.stream().anyMatch(u -> userId.equals(u.getUserId()));
    }
}
