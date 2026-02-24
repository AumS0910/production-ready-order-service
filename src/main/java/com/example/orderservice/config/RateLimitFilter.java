package com.example.orderservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, RequestCounter> requestsCount = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        RequestCounter counter = requestsCount.computeIfAbsent(ip, k -> new RequestCounter());

        synchronized (counter) {
            long now = Instant.now().getEpochSecond();

            if (now - counter.startTime > WINDOW_SECONDS) {
                counter.startTime = now;
                counter.count = 0;
            }

            counter.count++;

            if(counter.count > MAX_REQUESTS) {
                response.setStatus(429);
                response.getWriter().write("Too many requests");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    static class RequestCounter {
        long startTime = Instant.now().getEpochSecond();
        int count = 0;
    }
}
