package com.example.PaymentGateway.service;


import com.example.PaymentGateway.model.User;
import com.example.PaymentGateway.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomeUserDetailsService implements UserDetailsService {

    //constructor injection instead of filed injection( @Autowired) (spring recommended)
    private UserRepository userRepository;

    public CustomeUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("user not found at a time of auth "));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getName())
                .password(user.getPassword())
                .build();
    }
}
