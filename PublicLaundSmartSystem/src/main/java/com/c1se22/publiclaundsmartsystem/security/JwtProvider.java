package com.c1se22.publiclaundsmartsystem.security;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import com.c1se22.publiclaundsmartsystem.exception.APIException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${app.jwt-secret}")
    private String secretKey;
    @Value("${app.jwt-expiration-milliseconds}")
    private long expiration;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime()+expiration);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key())
                .compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String getUsername(String token){
        // Create a parser builder of jwts -> get claims -> get subject
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(token);
            return true;
        } catch(MalformedJwtException e){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException e){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.EXPIRED_JWT);
        } catch (UnsupportedJwtException e){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e){
            throw new APIException(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_JWT_CLAIMS);
        }
    }
}
