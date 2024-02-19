package org.example.springdeepsecurity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/*
* @EnableWebSecurity : Anatasyonu verilmez ise videoda gösterilen web security şablonunda bulunan filterChain
* uygulanamaz.
*
* @EnableMethodSecurity : Path değilde sadece controller sınıfı verilirse orada kontrolü sağlar.
*
* Encrypt : Öncelikle şifre encyprt yapalım. In memory securıty gerçek hayatta kullanılmıyor veya az kullanılıyor.
* Burada memory securıty üzerine ekleme yapılacak basic authentication sonrasında üzerine bir ekleme daha jwt token
* olarak ilerlenecek.
* */
public class SecurityConfig {


    /*
    *Burada PaswoordEncoder veya BCryptPasswordEncoder kullanılabilir. İkisi arasındaki fark passwordEncoder şifreleme
    *için SHA-1 kullanır. BCryptPasswordEncoder ise Base-64 kullanmaktadır.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /*
    * Bir method yardımıyla detailslar geri döner ve buradaki detailslar in memory aktarılır.
    * Burada spring in kendi  user ı vardır. O kullanılır arkada ancak biz eğer user ımızda örneğin email vs. gibi
    * özel attributelar istersek kendi userımızı yaratırız.
    *  */
    @Bean
    public UserDetailsService users () {
        UserDetails user1 = User.builder()
                .username("Hüseyin")
                .password(passwordEncoder().encode("pass"))
                .roles("USER")//"ROLE_USER" BURADA ROLE_ ile başlamıyorsa ROLE_ ekleniyor.
                .build();
        UserDetails admin = User.builder()
                .username("Mustafa")
                .password(passwordEncoder().encode("msss"))
                .roles("ADMIN")
                .build();

        /*Burada service instance dönülmesi gerekiyor çünkü filterchain de kullanılması gerekiyor ve kullanıcılar
        verilir.
        */
        return new InMemoryUserDetailsManager(user1, admin);
    }

    //Uygulamanın BodyGuard ı olarak geçerlidir.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security)  throws  Exception{

        security
                .headers(x -> x.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))//Burada FrameOptions sayesinde custom değerler verilebiliyor. Ignore ile iptal ettik.
                .csrf(AbstractHttpConfigurer::disable)//Eskisinde direk disable verilebilirken şimdi spring 3.0 ile csrf içerisinde fonksiyon alıyor.
                .formLogin(AbstractHttpConfigurer::disable)// Burada login form çıkmaz header ile gönderilir.
                //.formLogin(Customizer.withDefaults())//Burada Spring security bize login sayfası üretiyor.
                .authorizeHttpRequests(x -> x.requestMatchers("/public/**","/auth/**").permitAll())//api lerin erişimleri belirleniyor.
                .authorizeHttpRequests(x -> x.requestMatchers("private/user/**").hasRole("USER"))//SONRADAN EKLENDI
                .authorizeHttpRequests(x -> x.requestMatchers("private/admin/**").hasRole("ADMIN"))//SONRADAN EKLENDI
                //requestMatchers sonrası tekrar yazılması okunaklılıgı bozar
                //authorizeHttpRequest kısmının sonunda hasRole denirse sadece role bağlı izin verilir.
                //eğer orada authenticated denirse giriş yapan herkese izin verilir.
                //hasAnyRole herhangi bir Role olabilir.
                //permitAll herkese izin ver demek.
                .authorizeHttpRequests(x->x.anyRequest().authenticated())//Bunların dışında gelen herhangi bir isteği authenticate yap.
                // 82. satırı yani anyRequest i authenticate yaparsak fark ne olur ?
                // anyRequest().authenticated permitall dan önce yapılırsa public ve aut için kullanıcının login olması gereklidir.
                // Ancak permit all daha önce olursa bunların dışındakileri authenticated yap olur.
                .httpBasic(Customizer.withDefaults());

        return security.build();


    }
}
