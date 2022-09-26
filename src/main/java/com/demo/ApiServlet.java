package com.demo;

import com.okta.jwt.Jwt;
import com.okta.jwt.JwtVerificationException;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/protected")
public class ApiServlet extends HttpServlet {

    @Inject
    TokenVerifier verifier;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String authHeader = request.getHeader("authorization");
        String accessToken = authHeader.substring(authHeader.indexOf("Bearer ") + 7);
        try {
            Jwt jwt = verifier.decode(accessToken);
            response.setContentType("text");
            response.getWriter().println("Welcome, " + jwt.getClaims().get("sub"));
            response.getWriter().println(jwt.getClaims());
        }
        catch (JwtVerificationException e) {
            response.setContentType("text");
            response.getWriter().println("Failed to decode JWT");
        }

    }
}
