package org.example.jwttokensecurity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.jwttokensecurity.service.JwtService;
import org.example.jwttokensecurity.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/*Service ile Component arasındaki fark nedir;
* Hiçbir fark yok sadece kullanım amacı farklıdır.
* Service data ile işlem yapar.
* Component utilize işlemler yapar.
* sadece semantic fark var.
*  */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //Gelen request içersindeki token ı validate yapılır


        String  autHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if(autHeader != null && autHeader.startsWith("Bearer ")) {
            // authorization var veya Bearer ile başlıyorsa
            token = autHeader.substring(7);
            userName = jwtService.extractUser(token);
        }

        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //username var ve autheticatiın validate durumda ise
            //username null olmayacak ve çalışıtğım thread de authentication olmamalı.
            // yoksada token üretilmeli
            UserDetails user = userService.loadUserByUsername(userName);

            if(jwtService.validateToken(token, user)) {
                //Gelen token ın doğruluna bakıyoruz

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null,user.getAuthorities());
                /*
                * authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));:
                * Bu kod satırı, kimlik doğrulama talebinin ayrıntılarını ayarlar
                * WebAuthenticationDetailsSource().buildDetails(request) çağrısı, HTTP isteğinden ayrıntıları alır
                * ve bunları bir WebAuthenticationDetails nesnesine dönüştürür.
                * Bu nesne, istekle ilgili ayrıntıları (örneğin, istemci IP adresi ve oturum ID’si gibi) içerir.
                * SecurityContextHolder.getContext().setAuthentication(authenticationToken);: Bu kod satırı, güvenlik
                * bağlamında kimlik doğrulama nesnesini ayarlar.
                * SecurityContextHolder bir saklama alanıdır ve genellikle kimlik doğrulama nesnesini saklar.
                *  Bu nesne, mevcut kullanıcının kimlik bilgilerini ve yetkilerini içerir.
                * Bu kod satırı, kimlik doğrulama nesnesini günceller veya ayarlar, böylece mevcut kullanıcının kimlik
                * bilgileri ve yetkileri güncellenir.
                * */
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request,response);



    }
}
