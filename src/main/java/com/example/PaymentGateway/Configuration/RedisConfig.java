package com.example.PaymentGateway.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;



// this class is used to create connection between redis and spring boot application
//RedisConfig
//   ↓
//creates Redis connection objects
//   ↓
//other parts of app reuse them       , simply class job is  create Redis related objects

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionactory()
    {
        return new LettuceConnectionFactory("localhost",6379); // connect to redis running on same machin on port 6379(it is defalut redis port)
    }

}


//What is LettuceConnectionFactory?
// it makes TCP connection with redis on port number 6379 , and factory manages connection crearion , reuse , polling and closing
//This class comes from Spring Data Redis.
//Internally it uses a Redis client called: Lettuce
//Lettuce is a Java Redis client library.

//So the layers look like this:
//Your Spring Boot app
//       ↓
//Spring Data Redis
//       ↓
//LettuceConnectionFactory
//       ↓
//Lettuce Redis client
//       ↓
//Redis server
//So this object is basically a bridge between Spring and Redis.
