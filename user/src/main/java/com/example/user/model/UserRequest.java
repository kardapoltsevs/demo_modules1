package com.example.user.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class UserRequest {
    //@NotBlank(message = "Email обязателен")
    //@Email(message = "Некорректный формат email")
    private String email;

   // @NotBlank(message = "Имя пользователя обязательно")
   // @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String name;
}
