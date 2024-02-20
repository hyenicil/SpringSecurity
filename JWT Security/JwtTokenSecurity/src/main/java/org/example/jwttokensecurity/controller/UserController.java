package org.example.jwttokensecurity.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.jwttokensecurity.dto.AuthRequest;
import org.example.jwttokensecurity.dto.CreateUserRequest;
import org.example.jwttokensecurity.entity.User;
import org.example.jwttokensecurity.service.JwtService;
import org.example.jwttokensecurity.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class UserController {
    //Buradaki bazı işlemler service olmalı.
    private final UserService service;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public UserController(UserService service, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Hello World! Spring Security....";
    }

    @PostMapping("/addNewUser")
    public User addUser(@RequestBody CreateUserRequest userRequest) {
        return  service.createUser(userRequest);
    }

    @GetMapping("/user")
    public String getUserString() {
        return "This is User";
    }

    @GetMapping("/admin")
    public String getAdminString() {
        return "This is Admın";
    }

    @PostMapping("/generateToken")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication =
                authenticationManager
                        .authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        authRequest.username(),
                                        authRequest.password()));

        //Kullanıcın autheticate mi kontrolü
        if(authentication.isAuthenticated())
        {
            return jwtService.generateToken(authRequest.username());
        }
        log.info("invalid username " + authRequest.username());
        throw new UsernameNotFoundException("invalid Username");
    }

}
