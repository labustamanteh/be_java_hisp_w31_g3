package com.mercadolibre.be_java_hisp_w31_g3.repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    void addAll(List<User> users);
    void add(User user);
    List<User> getAll();
    Optional<User> getById(Long userId);
    void addFollower(Long userId, Long followerId);
}
