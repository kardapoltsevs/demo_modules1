package com.example.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Data
@Component
public class UserBody {

  private String name;
  private String email;

}
