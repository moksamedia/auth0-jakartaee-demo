package com.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Logger;

import static jakarta.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;

@ApplicationScoped
public class AuthorizationIdentityStore implements IdentityStore {

    private static final Logger LOGGER = Logger.getLogger(AuthorizationIdentityStore.class.getName());

    @Override
    public Set<ValidationType> validationTypes() {
        return EnumSet.of(PROVIDE_GROUPS);
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        var principal = validationResult.getCallerPrincipal().getName();
        LOGGER.info("Get principal name in validation result: " + principal);
        return Set.of("foo", "bar");
    }

}