package com.demo;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/protected")
public class ApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        DecodedJWT jwt = (DecodedJWT) request.getAttribute("accessToken");
        IdToken idToken = (IdToken) request.getAttribute("idToken");
        response.setContentType("text/plain");
        response.getWriter().println("Welcome, " + idToken.email);
        response.getWriter().println("accessToken claims:" + jwt.getClaims());
        response.getWriter().println("idToken claims:" + idToken.toString());
    }
}
