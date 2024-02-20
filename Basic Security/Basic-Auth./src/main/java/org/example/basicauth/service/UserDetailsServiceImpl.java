package org.example.basicauth.service;

import org.example.basicauth.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
/* Burada yapmak yerine UserService içerisinde yapılabilir ancak userDetailService security ait bir parça iken
*  userService bussiness ın bir parçası olabilir. Single responsibilty olarak düşünebiliriz.
**/
    /* Injection işlemleri Lombok a bırakılmamalı. ThirdLibrary ve spirng bir application context yönetiyor ve
    *  bu context dışında başka bir library e injection vermek sıkıntılı olabilir. Performansa kadar.
    */
    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /*
        * */
        Optional<User> user =  userService.getByUserName(username);

        return user.orElseThrow(EntityNotFoundException::new);
    }
}
