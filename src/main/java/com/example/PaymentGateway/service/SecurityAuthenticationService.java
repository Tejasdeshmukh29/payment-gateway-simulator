package com.example.PaymentGateway.service;

import com.example.PaymentGateway.dto.UserLoginDTO;
import com.example.PaymentGateway.dto.UserRegisterDTO;
import com.example.PaymentGateway.exception.IncorrectPassword;
import com.example.PaymentGateway.model.User;
import com.example.PaymentGateway.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuthenticationService {

    private UserRepository userRepository;
    private JwtService jwtService;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public SecurityAuthenticationService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String authenticate(UserLoginDTO userLoginDTO) {

        // getting user from repo
        User user = userRepository.findByName(userLoginDTO.getUsername()).orElseThrow(()-> new UsernameNotFoundException("user not found in database ( at a time of login )"));

        //comparing both passwords if incorrect throw error
        if(! encoder.matches(userLoginDTO.getPassword(), user.getPassword())) throw new IncorrectPassword("password is incorrect please try again !");

        //else genrate token
        return jwtService.genrateToken(user.getName());
    }

    public void Register(UserRegisterDTO userRegisterDTO)
    {
        //creating new user (entity)
        User user = new User();
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(encoder.encode(userRegisterDTO.getPassword()));
        user.setName(userRegisterDTO.getName());
        user.setPhoneNo(userRegisterDTO.getPhoneNo());

        userRepository.save(user);
    }


}
