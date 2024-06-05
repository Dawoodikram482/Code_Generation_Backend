package com.example.Code_Generation_Backend.security;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;

@Component
@Getter
public class JwtKeyProvider {
    @Value("${jwt.key-store}")
    private String keystore;
    @Value("${jwt.key-store-password}")
    private String password;
    @Value("${jwt.key-alias}")
    private String alias;
    private Key privateKey;
    private PublicKey publicKey;

    @PostConstruct
    protected void init() {
        try {
            Resource resource = new ClassPathResource(keystore);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(resource.getInputStream(), password.toCharArray());
            privateKey = keyStore.getKey(alias, password.toCharArray());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Key getPrivateKey() {
        return privateKey;
    }
}
