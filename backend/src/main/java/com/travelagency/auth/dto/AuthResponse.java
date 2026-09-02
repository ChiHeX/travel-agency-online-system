package com.travelagency.auth.dto;

public record AuthResponse(String token, String tokenType, UserView user) {
}
