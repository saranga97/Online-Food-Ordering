package com.advent.controller;

import com.advent.config.JwtProvider;
import com.advent.model.Cart;
import com.advent.model.User;
import com.advent.repository.CartRepository;
import com.advent.repository.UserRepository;
import com.advent.request.LoginRequest;
import com.advent.response.AuthResponse;
import com.advent.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null){
            throw new Exception("Email is already used with another account");
        }
        User createdUser = new User();

        createdUser.setEmail(user.getEmail());

        createdUser.setFullName(user.getFullName());

        createdUser.setRole(user.getRole());

        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = UserRepository.save(createdUser);

        Cart cart = new Cart();

        cart.setCustomer(savedUser);
        CartRepository.save(cart);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Register success");
        authResponse.setRole(savedUser.getRole());

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

    }

    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest req){
        return  null;
    }

}
