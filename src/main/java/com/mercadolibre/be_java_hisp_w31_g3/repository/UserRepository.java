package com.mercadolibre.be_java_hisp_w31_g3.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mercadolibre.be_java_hisp_w31_g3.model.Post;
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
        userFollower.getFollowed().add(userFollowed);
    }

    @Override
    public void addPost(Long userId, Post post) {
        User user = getById(userId).get();
        user.getPosts().add(post);
    }

    @Override
    public void unfollowUser(Long userId, Long userIdToUnfollow) {
        User userFollower = getById(userId).get();
        User userFollowed = getById(userIdToUnfollow).get();

        userFollowed.getFollowers().remove(userFollower);
        userFollower.getFollowed().remove(userFollowed);
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

    @Override
    public Boolean isAnyMatch(Predicate<User> predicate) {
        return users.stream().anyMatch(predicate);
    }
}
