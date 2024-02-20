package org.example.basicauth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

//UserDetails  implemente edilmiştir. Çünkü burada spring security ait bir entity olduğu anlaşılır.

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String password;

    //Zorunlu Alanlar//Ayrı bir enitty de taşınabilir.
    private boolean accountNonExpired;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    //ROlE u ayrı bir tablo olarak tutarız.
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))//Program ayağa kalkarken tablo yaratılır.
    @Column(name = "role", nullable = false)//User tablosuna role column eklenir.
    @Enumerated(EnumType.STRING)//Otomatik convert işlemi yapılır.
    private Set<Role> authorities;
    // Bir user in aldığı tüm rolleri ayrı bir tabloda bir liste şeklinde tutmuş oluruz.
}
