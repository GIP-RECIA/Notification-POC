package fr.recia.producer_api_poc.security;

import fr.recia.producer_api_poc.configuration.ApiKeyProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String HEADER_IP_ADDRESS = "X-Forwarded-For";

    private final ApiKeyProperties props;
    private final List<IpAddressMatcher> ipMatchers;

    public ApiKeyFilter(ApiKeyProperties props) {
        this.props = props;
        this.ipMatchers = props.getAllowedIps()
                .stream()
                .map(IpAddressMatcher::new)
                .toList();

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        if (!isIpAllowed(clientIp)) {
            log.warn("Unauthorized IP {}", clientIp);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String apiKey = request.getHeader(HEADER_API_KEY);
        if (apiKey == null || !props.getApiKeys().containsValue(apiKey)) {
            log.warn("Service isn't authorized to produce an event with apiKey {}", apiKey);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isIpAllowed(String ip) {
        return this.ipMatchers.stream().anyMatch(matcher -> matcher.matches(ip));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(HEADER_IP_ADDRESS);
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}

