package com.crawl.chungkhoan.repository;

import com.crawl.chungkhoan.model.User;
import com.crawl.chungkhoan.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final InMemoryStorage storage;

    public User save(User user) {
        return storage.saveUser(user);
    }

    public Optional<User> findById(Long id) {
        return storage.findUserById(id);
    }

    public Optional<User> findByEmail(String email) {
        return storage.findUserByEmail(email);
    }

    public Optional<User> findByApiKey(String apiKey) {
        return storage.findAllUsers().stream()
                .filter(u -> apiKey.equals(u.getApiKey()))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return storage.findUserByEmail(email).isPresent();
    }

    public List<User> findAll() {
        return storage.findAllUsers();
    }

    public void deleteById(Long id) {
        storage.deleteUser(id);
    }
}

