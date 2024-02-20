package org.example.jwttokensecurity.service;




import jakarta.persistence.EntityNotFoundException;
import org.example.jwttokensecurity.dto.CreateUserRequest;
import org.example.jwttokensecurity.entity.User;
import org.example.jwttokensecurity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*
         * */
        Optional<User> user =  userRepository.findByUsername(username);

        return user.orElseThrow(EntityNotFoundException::new);
    }

    public Optional<User> getByUserName(String username) {
        return userRepository.findByUsername(username);
    }


    public User createUser(CreateUserRequest createUserRequest) {

        User newUser =  User.builder()
                .name(createUserRequest.name())
                .username(createUserRequest.username())
                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))//yapmazsak veritabanına direk password eklenir.
                .authorities(createUserRequest.authorities())
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .accountNonLocked(true)
                .build();

        return userRepository.save(newUser);
    }
}
