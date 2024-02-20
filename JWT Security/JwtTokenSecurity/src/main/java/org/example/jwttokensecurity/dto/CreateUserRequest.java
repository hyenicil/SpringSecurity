package org.example.jwttokensecurity.dto;

import lombok.Builder;
import org.example.jwttokensecurity.entity.Role;

import java.util.Set;

//Record olduğu için getter ve setter herşey içerisinde var.
@Builder
public record CreateUserRequest(
        String name,
        String username,
        String password,
        Set<Role> authorities
){
}
