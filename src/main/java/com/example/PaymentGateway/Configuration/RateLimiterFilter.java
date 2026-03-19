package com.example.PaymentGateway.Configuration;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final ProxyManager<byte[]> proxyManager;
    private final BucketConfiguration bucketConfiguration;

    public RateLimiterFilter(ProxyManager<byte[]> proxyManager,
                             BucketConfiguration bucketConfiguration) {

        this.proxyManager = proxyManager;
        this.bucketConfiguration = bucketConfiguration;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        String key = "rate_limit:ip:" + ip;

        Bucket bucket = proxyManager.builder()
                .build(key.getBytes(StandardCharsets.UTF_8), bucketConfiguration);

        if (bucket.tryConsume(1)) {

            filterChain.doFilter(request, response);

        } else {

            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }
}