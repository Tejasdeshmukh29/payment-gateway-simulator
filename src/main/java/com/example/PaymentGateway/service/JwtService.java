package com.example.PaymentGateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {


    //constructor injection instead of filed injection( @Autowired) (spring recommended)
//     private CustomeUserDetailsService userDetailsService;
//     public JwtService( CustomeUserDetailsService userDetailsService)
//     {
//         this.userDetailsService = userDetailsService;
//     }

    private final String SECREATE_KEY = "p9F2k3nL8xQ1sV6wB4yT7mE0rZ5uC9dH2jK8aP3tR6Y4wX1s";



    public String genrateToken(String username)
    {
        return Jwts.builder()
                .signWith(getSignkey(), SignatureAlgorithm.HS256)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*60))
                .compact();
    }

    public Key getSignkey()
    {
      byte[] sec_byte = SECREATE_KEY.getBytes();
       return Keys.hmacShaKeyFor(sec_byte);
    }

    public boolean isTokenValid(String token, UserDetails userDetails)
    {
     String username = extractUsername(token);
     return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return claims(token).getExpiration().before(new Date());
    }

    public Claims claims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSignkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
