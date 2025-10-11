package com.example.confl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ConflRepository extends JpaRepository<Object, Long> {
    boolean existsByEmail(String email);
}
