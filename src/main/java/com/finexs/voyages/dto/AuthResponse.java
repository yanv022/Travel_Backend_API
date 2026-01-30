package com.finexs.voyages.dto;

import com.finexs.voyages.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private String token;

}
