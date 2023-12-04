package com.amf.connectsong.utils;

import java.io.Serializable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler implements Serializable {
    public ResponseEntity<?> returnException(Exception e) {
        switch (e.getMessage()) {
            case "EMAIL_ALREADY_TAKEN":
                return new ResponseEntity<>("Error: Email is already taken!", HttpStatus.CONFLICT);

            case "USERNAME_ALREADY_TAKEN":
                return new ResponseEntity<>("Error: Username is already taken!", HttpStatus.CONFLICT);

            case "ROLE_NOT_FOUND":
                return new ResponseEntity<>("Error: Role not found!", HttpStatus.NOT_FOUND);

            case "PASSWORD_NOT_STRONG":
                return new ResponseEntity<>("Error: Weak password!", HttpStatus.NOT_ACCEPTABLE);

            case "INVALID_EMAIL":
                return new ResponseEntity<>("Error: Invalid email!", HttpStatus.NOT_ACCEPTABLE);

            case "USER_NOT_FOUND":
                return new ResponseEntity<>("Error: User not found!", HttpStatus.NOT_FOUND);

            case "NO_ALBUMS_FOUND":
                return new ResponseEntity<>("Error: Album not found!", HttpStatus.NOT_FOUND);

            case "ALBUM_NOT_FOUND":
                return new ResponseEntity<>("Error: Album not found!", HttpStatus.NOT_FOUND);

            case "TOKEN_NOT_FOUND":
                return new ResponseEntity<>("Token not found!", HttpStatus.NOT_FOUND);

            case "ROULETTE_NOT_FOUND":
                return new ResponseEntity<>("Roulette not found!", HttpStatus.NOT_FOUND);

            case "NOT_FOUND_ALBUM":
                return new ResponseEntity<>("Album not found!", HttpStatus.NOT_FOUND);

            case "USER_ADDRESS_NOT_FOUND":
                return new ResponseEntity<>("User don't have address!", HttpStatus.NOT_FOUND);

            case "FILENAME_IS_INVALID":
                return new ResponseEntity<>("Insert a valid filename!", HttpStatus.BAD_REQUEST);

            case "CANNOT_SAVE_FILE":
                return new ResponseEntity<>("Error saving this file!", HttpStatus.INTERNAL_SERVER_ERROR);

            case "CANNOT_READ_FILE":
                return new ResponseEntity<>("Error reading this file!", HttpStatus.INTERNAL_SERVER_ERROR);

            case "MESSAGE_NOT_FOUND":
                return new ResponseEntity<>("Message not found!", HttpStatus.NOT_FOUND);

            default:
                break;
        }

        return new ResponseEntity<>(e.getStackTrace(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
