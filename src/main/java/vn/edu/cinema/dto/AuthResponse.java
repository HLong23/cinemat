package vn.edu.cinema.dto;

public record AuthResponse(String token, String tokenType, String username, String role, long expiresInMs) {
}
