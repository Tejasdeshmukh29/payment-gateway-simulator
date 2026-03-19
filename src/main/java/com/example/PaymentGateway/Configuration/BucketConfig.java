package com.example.PaymentGateway.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;

import io.lettuce.core.RedisClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Bean
    public BucketConfiguration rateLimitBucketConfiguration() {

        Bandwidth limit =
                Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));

        return BucketConfiguration.builder()
                .addLimit(limit)
                .build();
    }

    @Bean
    public ProxyManager<byte[]> proxyManager() {

        RedisClient redisClient = RedisClient.create("redis://redis:6379");
        return LettuceBasedProxyManager
                .builderFor(redisClient)
                .build();
    }
}