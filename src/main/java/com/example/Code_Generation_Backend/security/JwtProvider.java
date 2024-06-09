package com.example.Code_Generation_Backend.security;

import com.example.Code_Generation_Backend.models.Role;
import com.example.Code_Generation_Backend.services.MyUserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    private final JwtKeyProvider keyProvider;
    private final MyUserDetailsService myUserDetailsService;

    public JwtProvider(JwtKeyProvider keyProvider, MyUserDetailsService myUserDetailsService) {
        this.keyProvider = keyProvider;
        this.myUserDetailsService = myUserDetailsService;
    }

    public String createToken(String email, List<Role> roles, Boolean isApproved) throws JwtException {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("auth", roles.stream().map(Role::getAuthority).collect(Collectors.joining(", ")));

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(keyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token);
        String userID = claims.getBody().getSubject();
        String authority = claims.getBody().get("auth", String.class);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authority));

        System.out.println("Authorities: " + authorities);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(userID, "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }
}
