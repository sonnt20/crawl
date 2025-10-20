package com.crawl.chungkhoan.repository;

import com.crawl.chungkhoan.model.Alert;
import com.crawl.chungkhoan.storage.InMemoryStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlertRepository {

    private final InMemoryStorage storage;

    public Alert save(Alert alert) {
        return storage.saveAlert(alert);
    }

    public Optional<Alert> findById(Long id) {
        return storage.findAlertById(id);
    }

    public List<Alert> findByUserIdAndEnabled(Long userId, Boolean enabled) {
        return storage.findAlertsByUserId(userId).stream()
                .filter(a -> enabled.equals(a.isEnabled()))
                .collect(Collectors.toList());
    }

    public List<Alert> findByEnabled(Boolean enabled) {
        return storage.findActiveAlerts().stream()
                .filter(a -> enabled.equals(a.isEnabled()))
                .collect(Collectors.toList());
    }

    public long countByUserId(Long userId) {
        return storage.findAlertsByUserId(userId).size();
    }

    public List<Alert> findAll() {
        return storage.findActiveAlerts();
    }

    public void deleteById(Long id) {
        storage.deleteAlert(id);
    }
}

