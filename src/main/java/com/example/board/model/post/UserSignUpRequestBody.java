package com.example.board.model.post;

import jakarta.validation.constraints.NotEmpty;

public record UserSignUpRequestBody(
        @NotEmpty
        String username,
        @NotEmpty
        String password
) {
}
