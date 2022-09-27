package com.demo;

import com.okta.jwt.AccessTokenVerifier;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import com.okta.jwt.JwtVerifiers;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebFilter(filterName = "jwtFilter", urlPatterns = "/api/*")
public class JwtFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JwtFilter.class.getName());

    @Inject
    OpenIdConfig openIdConfig;

    private AccessTokenVerifier jwtVerifier;

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("jwtVerifier initialized for issuer:" + openIdConfig.getIssuerUri());
        jwtVerifier = JwtVerifiers.accessTokenVerifierBuilder()
                .setIssuer(openIdConfig.getIssuerUri())
                .build();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        LOGGER.info("In JwtFilter, path: " + request.getRequestURI());

        // Get access token from authorization header
        String authHeader = request.getHeader("authorization");
        if (authHeader == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getOutputStream().print("Unauthorized");
            return;
        } else {
            String accessToken = authHeader.substring(authHeader.indexOf("Bearer ") + 7);
            try {
                Jwt jwt = jwtVerifier.decode(accessToken);
                LOGGER.info("JWT decoded. sub=" + jwt.getClaims().get("sub"));
                request.setAttribute("jwt", jwt);
            } catch (JwtVerificationException e) {
                LOGGER.severe("Failed to verify JWT with error message: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().print("Unauthorized");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}