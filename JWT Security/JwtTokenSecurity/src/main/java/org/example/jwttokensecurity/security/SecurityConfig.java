package org.example.jwttokensecurity.security;

import org.example.jwttokensecurity.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception{

        return security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(x->
                        x
                                .requestMatchers("/auth/welcome/**","/auth/addNewUser/**",
                                        "/auth/generateToken/**").permitAll()
                                .requestMatchers("/auth/user").hasRole("USER")
                                .requestMatchers("/auth/admin").hasRole("ADMIN")
                )

                /*
                * always – A session will always be created if one doesn’t already exist.
                * ifRequired – A session will be created only if required (default).
                * never – The framework will never create a session itself, but it will use one if it already exists.
                * stateless – No session will be created or used by Spring Security.
                * */

                .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // authentication sonrasında gelen token lı istekleri bu filtre sayesinde valide edilir.
                // Bu filtre olumsuz dönerse authenticate işlemi yapar. İş akışına göre farklı filtreler eklenebilir. Redis...
                .build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        //Authentication işlemi yaparken hangi servisi ve hangi encoder kullanacağımızı belirtir
        /*Kimlik Doğrulama İşlemlerini Gerçekleştirir: AuthenticationProvider bir kimlik doğrulama talebini
        doğrulamaktan sorumludur123. Bir Authentication talebi alır ve tamamen kimlik doğrulanmış,
        tam yetkilere sahip bir nesne döndürür. Esneklik Sağlar: AuthenticationProvider kullanımı, standart bir
        UserDetailsService kullanımına kıyasla daha fazla esneklik sağlar1. Örneğin, bazı durumlarda, kimlik doğrulama
        işlemi için tam Authentication talebine erişim gerekebilir1. Bu, örneğin, bir üçüncü taraf servise (Crowd gibi)
         karşı kimlik doğrulama yaparken gerekebilir1.*/
        return authenticationProvider;
    }

    //Spring application context ayaga kalkerken saplanıyor.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        //Authentication işelmi yapar
        return configuration.getAuthenticationManager();
    }
}
