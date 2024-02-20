package org.example.basicauth;


import org.example.basicauth.dto.CreateUserRequest;
import org.example.basicauth.entity.Role;
import org.example.basicauth.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final UserService userService;

    public Application(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        createDummyDate();
    }

    private void createDummyDate() {
        CreateUserRequest request1 = CreateUserRequest.builder()
                .name("Hüseyin")
                .username("yenicilh")
                .password("1234")
                .authorities(Set.of(Role.ROLE_ADMIN))
                .build();
        userService.createUser(request1);

        CreateUserRequest request2 = CreateUserRequest.builder()
                .name("Mustafa")
                .username("mito")
                .password("12345")
                .authorities(Set.of(Role.ROLE_USER))
                .build();
        userService.createUser(request2);

        CreateUserRequest request3 = CreateUserRequest.builder()
                .name("Adnan")
                .username("adana")
                .password("0101")
                .authorities(Set.of(Role.ROLE_FSK))
                .build();
        userService.createUser(request3);
    }
}
