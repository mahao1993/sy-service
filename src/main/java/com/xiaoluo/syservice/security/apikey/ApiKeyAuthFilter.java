package com.xiaoluo.syservice.security.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final ApiKeyProperties apiKeyProperties;
    private final ObjectMapper objectMapper;

    public ApiKeyAuthFilter(ApiKeyProperties apiKeyProperties, ObjectMapper objectMapper) {
        this.apiKeyProperties = apiKeyProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PATH_MATCHER.match("/actuator/**", request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String configuredApiKey = apiKeyProperties.getValue();
        String providedApiKey = request.getHeader(apiKeyProperties.getHeaderName());

        if (configuredApiKey == null || configuredApiKey.isBlank()) {
            writeUnauthorized(response, "API key is not configured on the server.");
            return;
        }

        if (!configuredApiKey.equals(providedApiKey)) {
            writeUnauthorized(response, "Invalid API key.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "message", message
        ));
    }
}
