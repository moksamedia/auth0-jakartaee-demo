package com.demo;

import com.okta.jwt.Jwt;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/api/protected")
public class ApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Jwt jwt = (Jwt)request.getAttribute("jwt");

        response.setContentType("text");
        response.getWriter().println("Welcome, " + jwt.getClaims().get("sub"));
        response.getWriter().println(jwt.getClaims());

    }
}
