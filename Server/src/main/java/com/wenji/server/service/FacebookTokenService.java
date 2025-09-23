package com.wenji.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class FacebookTokenService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FacebookTokenValidationResult verifyToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return new FacebookTokenValidationResult(false, null, null, null, "Access token is required");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=id,name,email";
            HttpGet request = new HttpGet(url);

            ClassicHttpResponse response = httpClient.execute(request);
            String responseBody;
            try {
                responseBody = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                return new FacebookTokenValidationResult(false, null, null, null, "Failed to read response");
            }
            
            if (response.getCode() == 200) {
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                
                String userId = jsonNode.has("id") ? jsonNode.get("id").asText() : null;
                String userName = jsonNode.has("name") ? jsonNode.get("name").asText() : null;
                String userEmail = jsonNode.has("email") ? jsonNode.get("email").asText() : null;
                
                return new FacebookTokenValidationResult(true, userId, userName, userEmail, null);
            } else {
                // Parse error response
                try {
                    JsonNode errorNode = objectMapper.readTree(responseBody);
                    String errorMessage = errorNode.has("error") && errorNode.get("error").has("message") 
                        ? errorNode.get("error").get("message").asText() 
                        : "Invalid access token";
                    return new FacebookTokenValidationResult(false, null, null, null, errorMessage);
                } catch (Exception e) {
                    return new FacebookTokenValidationResult(false, null, null, null, "Invalid access token");
                }
            }
        } catch (IOException e) {
            return new FacebookTokenValidationResult(false, null, null, null, "Failed to verify token: " + e.getMessage());
        }
    }

    public static class FacebookTokenValidationResult {
        private final boolean valid;
        private final String userId;
        private final String userName;
        private final String userEmail;
        private final String errorMessage;

        public FacebookTokenValidationResult(boolean valid, String userId, String userName, String userEmail, String errorMessage) {
            this.valid = valid;
            this.userId = userId;
            this.userName = userName;
            this.userEmail = userEmail;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}