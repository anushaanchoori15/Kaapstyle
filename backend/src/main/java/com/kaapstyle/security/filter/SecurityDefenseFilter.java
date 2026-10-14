package com.kaapstyle.security.filter;

import com.kaapstyle.security.service.SecurityDefenseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Servlet filter that applies per-IP rate limiting before a request reaches the rest of the chain.
 *
 * <ul>
 *   <li>/api/auth/** : max 30 requests per minute</li>
 *   <li>all other paths : max 120 requests per minute</li>
 * </ul>
 *
 * Assumed collaborator contract (KAAPSTYLE security service layer):
 * {@code boolean SecurityDefenseService#isRateLimited(String clientIp, String path, int maxRequestsPerMinute)}
 * returns {@code true} when the limit has been exceeded for the given client/path bucket.
 *
 * Traceability: CR15 -> UC01 -> T03 (B06-JAVA-ST04)
 */
@Component
public class SecurityDefenseFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH_PATTERN = "/api/auth/**";
    private static final int AUTH_MAX_REQUESTS_PER_MINUTE = 30;
    private static final int DEFAULT_MAX_REQUESTS_PER_MINUTE = 120;
    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String RATE_LIMIT_BODY =
            "{\"error\":\"Rate limit exceeded. Try again in 60 seconds.\"}";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecurityDefenseService securityDefenseService;

    public SecurityDefenseFilter(SecurityDefenseService securityDefenseService) {
        this.securityDefenseService = securityDefenseService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = extractClientIp(request);
        String path = resolvePath(request);
        int limit = pathMatcher.match(AUTH_PATH_PATTERN, path)
                ? AUTH_MAX_REQUESTS_PER_MINUTE
                : DEFAULT_MAX_REQUESTS_PER_MINUTE;

        if (securityDefenseService.isRateLimited(clientIp, path, limit)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Retry-After", "60");
            response.getWriter().write(RATE_LIMIT_BODY);
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // The first entry is the originating client; subsequent ones are proxies.
            String first = forwardedFor.split(",")[0].trim();
            if (!first.isEmpty()) {
                return first;
            }
        }
        return request.getRemoteAddr();
    }

    private String resolvePath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }
        return uri;
    }
}