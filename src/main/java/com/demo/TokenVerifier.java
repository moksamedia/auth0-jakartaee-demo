package com.demo;


import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.JwtVerifiers;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class TokenVerifier {

    private static final Logger LOGGER = Logger.getLogger(TokenVerifier.class.getName());

    protected AccessTokenVerifier instance;

    @Inject
    OpenIdConfig openIdConfig;

    @PostConstruct
    void init() {
        LOGGER.info("jwtVerifier initialized for issuer:" + openIdConfig.getIssuerUri());
        instance = JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer(openIdConfig.getIssuerUri())
                .build();
    }

    protected Jwt decode(String accessToken) throws JwtVerificationException {
        LOGGER.info("Decoding accessToken: " + accessToken);
        return instance.decode(accessToken);
    }

}
