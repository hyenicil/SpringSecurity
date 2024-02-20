package org.example.basicauth.dto;

import org.example.basicauth.entity.Role;
import lombok.Builder;

import java.util.Set;

//Record olduğu için getter ve setter herşey içerisinde var.
@Builder
public record CreateUserRequest (
        String name,
        String username,
        String password,
        Set<Role> authorities
){
}
