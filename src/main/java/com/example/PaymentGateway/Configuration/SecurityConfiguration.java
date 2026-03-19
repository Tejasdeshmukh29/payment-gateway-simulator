package com.example.PaymentGateway.Configuration;


import com.example.PaymentGateway.service.CustomeUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {


    //constructor injection instead of filed injection( @Autowired) (spring recommended)
    private final CustomeUserDetailsService customeUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimiterFilter rateLimiterFilter;

    public SecurityConfiguration(CustomeUserDetailsService customeUserDetailsService, JwtAuthFilter jwtAuthFilter, RateLimiterFilter rateLimiterFilter)
    {
        this.customeUserDetailsService = customeUserDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimiterFilter = rateLimiterFilter;
    }


    // creating and adjusting JWT filter in the flow ( JWT filter comes before UsernamePasswordAuthentication Filter) and permiting Auth requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/hello/**").permitAll()
                        .requestMatchers("/merchant/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Rate limiter runs first
                .addFilterBefore(rateLimiterFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
               return  http.build();
    }


    //creating Authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(customeUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    //creating password encoder
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }


    // creating authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider DaoAuthenticationProvider,HttpSecurity http)
    {
        return http.
                authenticationProvider(DaoAuthenticationProvider)
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

}
