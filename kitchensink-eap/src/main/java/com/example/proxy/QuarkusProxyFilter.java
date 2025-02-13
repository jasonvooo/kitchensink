package com.example.proxy;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

@WebFilter("/api/*")
public class QuarkusProxyFilter implements Filter {

    private static final String QUARKUS_URL = "http://localhost:9000";
    private final Set<String> migratedPaths = Set.of("/api/members", "/api/members/");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestPath = httpRequest.getRequestURI();

        // Remove context path but keep /api
        String contextPath = httpRequest.getContextPath();
        requestPath = requestPath.substring(contextPath.length());

        if (isMigrated(requestPath)) {
            // Forward to Quarkus keeping /api
            proxyRequest(httpRequest, response, requestPath);
        } else {
            // Continue with EAP processing
            chain.doFilter(request, response);
        }
    }

    private boolean isMigrated(String path) {
        String normalizedPath = path.endsWith("/") ?
                path.substring(0, path.length() - 1) : path;
        return migratedPaths.stream()
                .anyMatch(migrated -> normalizedPath.startsWith(migrated));
    }

    private void proxyRequest(HttpServletRequest request,
                              ServletResponse response,
                              String path) throws IOException {
        String queryString = request.getQueryString();
        String fullUrl = QUARKUS_URL + path +
                (queryString != null ? "?" + queryString : "");

        URL url = new URL(fullUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Copy method
        conn.setRequestMethod(request.getMethod());

        // Copy headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            conn.setRequestProperty(headerName, headerValue);
        }

        // Handle request body for POST/PUT
        if ("POST".equalsIgnoreCase(request.getMethod()) ||
                "PUT".equalsIgnoreCase(request.getMethod())) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                request.getInputStream().transferTo(os);
            }
        }

        // Copy response
        response.setContentType(conn.getContentType());
        conn.getInputStream().transferTo(response.getOutputStream());
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}