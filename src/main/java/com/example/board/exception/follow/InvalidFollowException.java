package com.example.board.exception.follow;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class InvalidFollowException extends ClientErrorException {
    public InvalidFollowException() {
        super(HttpStatus.BAD_REQUEST,"Follow 할 수 없습니다.");
    }

    public InvalidFollowException(String message) {
        super(HttpStatus.BAD_REQUEST,message);
    }
}
