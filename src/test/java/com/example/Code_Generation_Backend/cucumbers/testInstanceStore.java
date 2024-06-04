package com.example.Code_Generation_Backend.cucumbers;

import com.example.Code_Generation_Backend.DTOs.requestDTOs.TokenDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

public class testInstanceStore {
    private static testInstanceStore instance;

    public static testInstanceStore getInstance() {
        if (instance == null) {
            instance = new testInstanceStore();
        }
        return instance;
    }
    @Getter
    @Setter
    private TokenDTO token;
    @Setter
    @Getter
    protected ResponseEntity response;
//    public TokenDTO getToken() {
//        return token;
//    }
//  public void setToken(TokenDTO token) {
//        this.token = token;
//  }
}
