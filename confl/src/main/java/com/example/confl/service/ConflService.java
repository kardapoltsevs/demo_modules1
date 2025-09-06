package com.example.confl.service;

import com.example.confl.model.Email;
import com.example.confl.repository.EmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ConflService {

    private final EmailRepository emailRepository;

    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    public boolean isEmailAvailable(String email) {
        boolean isAvailable = !emailRepository.existsByEmail(email);

        if (isAvailable) {
            Email newEmail = Email.builder()
                    .email(email)
                    .build();

            emailRepository.save(newEmail);
        }

        return isAvailable;
    }
}
