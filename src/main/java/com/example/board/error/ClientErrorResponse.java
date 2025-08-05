package com.example.board.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClientErrorResponse(HttpStatus status, Object message) {
}
