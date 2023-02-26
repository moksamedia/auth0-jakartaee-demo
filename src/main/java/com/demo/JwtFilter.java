package com.demo;

import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Logger;

@WebFilter(filterName = "jwtFilter", urlPatterns = "/api/*")
public class JwtFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(JwtFilter.class.getName());

    @Inject
    OidcConfig oidcConfig;

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("Auth0 jwtVerifier initialized for issuer:" + oidcConfig.getIssuerUri());
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
            // Get the access token from the header
            String accessToken = authHeader.substring(authHeader.indexOf("Bearer ") + 7);
            LOGGER.info("accesstoken: " + accessToken);
            JwkProvider provider = new UrlJwkProvider(oidcConfig.getIssuerUri());
            try {
                // Decode the access token
                DecodedJWT jwt = JWT.decode(accessToken);
                // Get the kid from received JWT token
                Jwk jwk = provider.get(jwt.getKeyId());

                Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

                // Verify the access token
                JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(oidcConfig.getIssuerUri())
                    .build();

                jwt = verifier.verify(accessToken);
                LOGGER.info("JWT decoded. sub=" + jwt.getClaims().get("sub"));

                // Save the access token in a request attribute
                request.setAttribute("accessToken", jwt);

                // Get the ID Token
                String issuerUri = oidcConfig.getIssuerUri();
                String userinfoUri = issuerUri + "userinfo";
                LOGGER.info("userinfoUri: " + userinfoUri);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest requestIdToken = HttpRequest.newBuilder(
                                URI.create(userinfoUri))
                        .header("Authorization", "Bearer " + accessToken)
                        .build();
                HttpResponse<String> responseIdToken = client.send(requestIdToken, HttpResponse.BodyHandlers.ofString());
                String idTokenString = responseIdToken.body();
                LOGGER.info("idTokenString: " + idTokenString);

                // Deserialize the ID token
                IdToken idToken = new Gson().fromJson(idTokenString, IdToken.class);

                LOGGER.info("idToken: " + idToken.toString());

                // Save the id token in a request attribute
                request.setAttribute("idToken", idToken);

            } catch (JWTVerificationException | JwkException e) {
                e.printStackTrace();
                LOGGER.severe("Failed to verify JWT with error message: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getOutputStream().print("Unauthorized");
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.severe("Failed to retrieve ID Token with error message: " + e.getMessage());
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
