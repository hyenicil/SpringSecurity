package org.example.jwttokensecurity.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

import static org.example.jwttokensecurity.constant.KeyConstant.JWT_KEY;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String SECRET_KEY;

    public String generateToken(String userName) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("can","wia");
        return createToken(claims,userName);
    };

    public Boolean validateToken(String token, UserDetails userDetails) {
        //Burada validate edilemilmesi için token extract edilmeli ve validate edilmeli içerikler.

        String username = extractUser(token);
        Date expirationDate = extractExpiration(token);
        //Burada kullanıcı adı kontrol edilir ve daha sonra zamana bakılır token sonlanma suresı suandan once mı dıye kontrol eder.

        return userDetails.getUsername().equals(username) && !expirationDate.before(new Date());
    }

    private Date extractExpiration(String token) {

        //token ın ne zaman expire olduğunu dönecek.
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)//neyi parse edeceğim
                .getBody();

        return claims.getExpiration();
    }

    public String extractUser(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder() //Jsonwebtoken üreten sınıf -Jwts
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))// token ne zaman üretildi.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))// token bitiş zamanı
                .signWith(getSignKey(), SignatureAlgorithm.HS256)//genelde 256 kullanılıyor.
                .compact();
    }
    private Key getSignKey() {

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);// secret_key decode yapılır ve key üretilir.
        //Keys.hmacShaKeyFor(SignatureAlgorithm.HS256)// Signature ile key oluşturabilirdik ancak zaten kendi keyimiz olduğu için onu kullandık.
        return Keys.hmacShaKeyFor(keyBytes);

    }


}
