package com.example.eventphoto.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtPrincipal {
    private String email;
    private Long customerId;
    private Long guestId;
    private Long adminId;
    private String type;
}
