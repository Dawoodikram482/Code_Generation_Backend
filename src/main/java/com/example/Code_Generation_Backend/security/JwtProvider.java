package com.example.Code_Generation_Backend.security;

import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {
    private final JwtKeyProvider keyProvider;
    private final MyUserDetailsService myUserDetailsService;

    public JwtProvider(JwtKeyProvider keyProvider, MyUserDetailsService myUserDetailsService) {
        this.keyProvider = keyProvider;
        this.myUserDetailsService = myUserDetailsService;
    }

    public String createToken(String email, List<Role> roles) throws JwtException {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000);
        Key privateKey = keyProvider.getPrivateKey();

        return Jwts.builder()
                .subject(email)
                .claim("auth", roles.stream().map(Role::name).toList())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        PublicKey publicKey = keyProvider.getPublicKey();
        try {
            Claims claims =
                    Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
            String email = claims.getSubject();
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(userDetails, "",
                    userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }
}
