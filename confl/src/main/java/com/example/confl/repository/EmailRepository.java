package com.example.confl.repository;

import com.example.confl.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

  boolean existsByEmail(String email);
}
