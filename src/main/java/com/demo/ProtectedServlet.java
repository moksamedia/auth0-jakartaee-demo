package com.demo;

import java.io.IOException;

import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/protected")
@DeclareRoles({"foo", "bar", "kaz"})
@ServletSecurity(
        @HttpConstraint(rolesAllowed = "foo")
)
public class ProtectedServlet extends HttpServlet {

        @Inject
        private OpenIdContext context;

        @Inject
        SecurityContext securityContext;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

                var principal = securityContext.getCallerPrincipal();
                var name = principal.getName();

                response.setContentType("text/html");
                response.getWriter().println("<h1>Protected Servlet</h1>");
                response.getWriter().println("<p>Principal name:" + name + "</p>");
                response.getWriter().println("<p>access token:" + context.getAccessToken() + "</p>");
                response.getWriter().println("<p>token type:" + context.getTokenType() + "</p>");
                response.getWriter().println("<p>subject:" + context.getSubject() + "</p>");
                response.getWriter().println("<p>expires in:" + context.getExpiresIn() + "</p>");
                response.getWriter().println("<p>refresh token:" + context.getRefreshToken() + "</p>");
                response.getWriter().println("<p>claims json:" + context.getClaimsJson() + "</p>");
        }
}
