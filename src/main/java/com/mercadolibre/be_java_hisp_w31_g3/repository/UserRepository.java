package com.mercadolibre.be_java_hisp_w31_g3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.mercadolibre.be_java_hisp_w31_g3.model.User;

@Repository
public class UserRepository implements IUserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public Optional<User> getById(Long userId) {
        return users.stream().filter(u -> u.getUserId().equals(userId)).findFirst();
    }

    @Override
    public void addFollower(Long userId, Long followerId) {
        User userFollower = getById(userId).get();
        User userFollowed = getById(followerId).get();

        userFollowed.getFollowers().add(userFollower);
    }

    @Override
    public void addAll(List<User> users) {
        this.users.addAll(users);
    }

    @Override
    public void add(User user) {
        this.users.add(user);
    }

    @Override
    public List<User> getAll() {
        return users;
    }

//    public List<User> getAllByPredicate(Predicate<User> predicate) {
//        return users.stream().filter(predicate).toList();
//    }
//
//    public User getByPredicate(Predicate<User> predicate) {
//        return users.stream().filter(predicate).findFirst().orElse(null);
//    }
}
