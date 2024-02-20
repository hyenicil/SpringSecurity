package org.example.jwttokensecurity.dto;

public record AuthRequest(
        String username,
        String password
) {
}
