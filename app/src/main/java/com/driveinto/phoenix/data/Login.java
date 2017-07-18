package com.driveinto.phoenix.data;

public class Login {
    private String customerId;

    private String providerType;
    private String token;
    private String indexProviderTypeToken;

    public Login() {
        // Needed for Firebase
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIndexProviderTypeToken() {
        return indexProviderTypeToken;
    }

    public void setIndexProviderTypeToken(String indexProviderTypeToken) {
        this.indexProviderTypeToken = indexProviderTypeToken;
    }
}
