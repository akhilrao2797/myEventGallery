package com.example.eventphoto.repository;

import com.example.eventphoto.model.AppProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppPropertyRepository extends JpaRepository<AppProperty, String> {
    Optional<AppProperty> findByPropertyKey(String key);
}
