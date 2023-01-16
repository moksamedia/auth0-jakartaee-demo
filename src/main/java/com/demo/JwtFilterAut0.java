package com.demo;

import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

@WebFilter(filterName = "jwtFilter", urlPatterns = "/api/*")
public class JwtFilterAut0 implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JwtFilterAut0.class.getName());

    @Inject
    OpenIdConfig openIdConfig;

    private JWTVerifier jwtVerifier;

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("Auth0 jwtVerifier initialized for issuer:" + openIdConfig.getIssuerUri());
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
            LOGGER.info("accesstoken: " + request.getRequestURI());
            JwkProvider provider = new UrlJwkProvider(openIdConfig.getIssuerUri());
            try {
                DecodedJWT jwt = JWT.decode(accessToken);
                // Get the kid from received JWT token
                Jwk jwk = provider.get(jwt.getKeyId());

                Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

                JWTVerifier verifier = JWT.require(algorithm)
                        .withIssuer(openIdConfig.getIssuerUri())
                        .build();

                jwt = verifier.verify(accessToken);
                LOGGER.info("JWT decoded. sub=" + jwt.getClaims().get("sub"));
                request.setAttribute("jwt", jwt);

            } catch (JWTVerificationException e){
                e.printStackTrace();
                LOGGER.severe("Failed to verify JWT with error message: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().print("Unauthorized");
                return;
            } catch (JwkException e) {
                e.printStackTrace();
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