package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenManager {

    private String authToken = "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";

    public String getAuthToken() {
        return "Bearer " + authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
