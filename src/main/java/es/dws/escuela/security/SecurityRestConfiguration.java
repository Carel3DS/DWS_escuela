package es.dws.escuela.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
public class SecurityRestConfiguration {
    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Every request that starts with "/api"
        http.securityMatcher("/api/**")
                //Authorize requests from these routes
                .authorizeHttpRequests((authz) ->{
                            authz.requestMatchers(HttpMethod.GET,"/test").authenticated();
                            //Allow public API requests
                            authz.requestMatchers(HttpMethod.GET,"/api/grade").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/grade/*").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/department").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/department/*").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/teacher").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/teacher/*").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/user").permitAll();
                            authz.requestMatchers(HttpMethod.GET,"/api/user/*").permitAll();
                            //Allow some POST API requests
                            authz.requestMatchers(HttpMethod.POST,"/api/teacher").permitAll();
                            authz.requestMatchers(HttpMethod.POST,"/api/user").permitAll();
                            //Private API requests
                            authz.requestMatchers(HttpMethod.POST,"/api/grade").hasAnyRole("TEACHER","ADMIN");
                            authz.requestMatchers(HttpMethod.PUT,"/api/grade/*").hasAnyRole("TEACHER","ADMIN");
                            authz.requestMatchers(HttpMethod.DELETE,"/api/grade/*").hasAnyRole("TEACHER","ADMIN");
                            //Admin API requests
                            authz.requestMatchers(HttpMethod.POST,"/api/department").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.PUT,"/api/department/*").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.DELETE,"/api/department/*").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.PUT,"/api/user/*").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.DELETE,"/api/user/*").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.PUT,"/api/teacher/*").hasRole("ADMIN");
                            authz.requestMatchers(HttpMethod.DELETE,"/api/teacher/*").hasRole("ADMIN");
                            //Non-standard API requests (some of them are public)
                            //TODO: implement non-standard API requests
                            //Rest of API requests are denied
                            authz.anyRequest().authenticated();
                        }
                );
        //Use HTTP Basic
        http.httpBasic(Customizer.withDefaults());
        //Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        //Make session Stateless
        http.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Disable login form
        http.formLogin(AbstractHttpConfigurer::disable);
        //Build the configuration
        return http.build();
    }

}
