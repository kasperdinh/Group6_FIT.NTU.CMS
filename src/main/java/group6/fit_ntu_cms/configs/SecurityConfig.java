package group6.fit_ntu_cms.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/", "/signin", "/signup", "/css/**", "/js/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 ->
                        oauth2
                                .loginPage("/signin")
                                .defaultSuccessUrl("/index", true)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers( "/signin", "/signup", "/css/**", "/js/**")
                );
        return http.build();
    }
}
