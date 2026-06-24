package com.stud.files.internal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class InternalAuthInterceptor implements HandlerInterceptor {

    private final InternalAuthProperties properties;

    public InternalAuthInterceptor(InternalAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!StringUtils.hasText(properties.getToken())) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal auth token is not configured");
            return false;
        }

        String actualToken = request.getHeader(properties.getHeaderName());
        if (!constantTimeEquals(properties.getToken(), actualToken)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid internal service token");
            return false;
        }

        return true;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (actual == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }
}
