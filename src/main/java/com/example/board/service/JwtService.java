package com.example.board.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();


    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String getUsername(String accessToken) {
        return getSubject(accessToken);
    }

    private String generateToken(String subject) {
        var now = new Date();
        var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3));
        // 완료 시점은 3시간 이후

        return Jwts.builder().subject(subject).signWith(secretKey)
                .issuedAt(now)
                .expiration(exp)
                .compact();
    }

    private String getSubject(String token) {

        try {

            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
            //OK, we can trust this JWT

        } catch (JwtException e) {
            log.error("JwtException",e);
            throw e;
            //don't trust the JWT!
        }

    }
}
