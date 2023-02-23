package com.demo;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Named("oidcConfig")
public class OidcConfig {

    private static final Logger LOGGER = Logger.getLogger(OidcConfig.class.getName());

    private String domain;
    private String clientId;
    private String clientSecret;
    private String issuerUri;

    @PostConstruct
    void init() {
        try {
            var properties = new Properties();
            properties.load(getClass().getResourceAsStream("/oidc.properties"));
            domain = properties.getProperty("domain");
            clientId = properties.getProperty("clientId");
            clientSecret = properties.getProperty("clientSecret");
            issuerUri = "https://" + this.domain + "/";
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load oidc.properties", e);
        }
    }

    public String getDomain() {
        return domain;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getIssuerUri() {
        return issuerUri;
    }
}
