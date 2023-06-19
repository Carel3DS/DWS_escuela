package es.dws.escuela.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    // TODO: make proper Security Configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) ->{
                    authz.requestMatchers("/").permitAll();
                    authz.requestMatchers("/assets/**").permitAll();
                    authz.requestMatchers("/login").permitAll();
                    authz.requestMatchers("/logout").permitAll();
                    authz.requestMatchers("/loginerror").permitAll();
                    authz.anyRequest().authenticated();
                }
        );
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
