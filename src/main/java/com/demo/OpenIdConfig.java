package com.demo;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.ClaimsDefinition;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@OpenIdAuthenticationMechanismDefinition(
        providerURI = "${openIdConfig.issuerUri}",
        clientId = "${openIdConfig.clientId}",
        clientSecret = "${openIdConfig.clientSecret}",
        redirectURI = "${baseURL}/callback",
        // default 500ms caused timeouts for me
        jwksConnectTimeout = 5000,
        jwksReadTimeout = 5000,
        extraParameters = {"audience=http://my-api"},
        claimsDefinition = @ClaimsDefinition( callerGroupsClaim = "http://my-api/groups" )
)
@ApplicationScoped
@Named("openIdConfig")
public class OpenIdConfig {
    private static final Logger LOGGER = Logger.getLogger(OpenIdConfig.class.getName());

    private String domain;
    private String clientId;
    private String clientSecret;
    private String issuerUri;

    @PostConstruct
    void init() {
        try {
            var properties = new Properties();
            properties.load(getClass().getResourceAsStream("/openid.properties"));
            domain = properties.getProperty("domain");
            clientId = properties.getProperty("clientId");
            clientSecret = properties.getProperty("clientSecret");
            issuerUri = properties.getProperty("issuerUri");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load openid.properties", e);
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
