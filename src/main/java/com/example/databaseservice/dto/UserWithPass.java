package com.example.databaseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Jacksonized
public class UserWithPass {

    private String email;
    private String password;
    private String username;


}
