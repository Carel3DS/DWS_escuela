package es.dws.escuela.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;


import java.security.SecureRandom;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    // TODO: make proper Security Configuration
    @Autowired
    RepositoryUserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) ->{
                //Allow public web views
                authz.requestMatchers("/").permitAll();
                authz.requestMatchers("/assets/**").permitAll();
                authz.requestMatchers("/signup").permitAll();
                authz.requestMatchers("/login").permitAll();
                authz.requestMatchers("/logout").permitAll();
                authz.requestMatchers("/error").permitAll();

                //Make public some entity views
                authz.requestMatchers("/grade").permitAll();
                authz.requestMatchers(regexMatcher("/grade\\/\\d+")).permitAll();
                authz.requestMatchers("/teacher").permitAll();
                authz.requestMatchers(regexMatcher("/teacher\\/[A-Za-z]+.[A-Za-z]+")).permitAll();
                authz.requestMatchers(regexMatcher("/user\\/[A-Za-z]+.[A-Za-z]+")).permitAll();
                authz.requestMatchers("/teacher/add").permitAll();
                authz.requestMatchers("/teacherByAge").permitAll();
                authz.requestMatchers("/department").permitAll();
                authz.requestMatchers(regexMatcher("/department\\/\\d+")).permitAll();

                //Private views
                authz.requestMatchers("/grade/**").hasAnyRole("TEACHER", "ADMIN");
                authz.requestMatchers("/department/**").hasRole("ADMIN");
                authz.requestMatchers("/admin").hasRole("ADMIN");
                authz.requestMatchers("/profile").hasRole("USER");
                authz.requestMatchers("/profile/*").hasRole("USER");
                authz.requestMatchers("/user/edit").hasRole("USER");
                authz.requestMatchers("/teacher/edit").hasRole("TEACHER");

                //Rest of requests are authenticated
                authz.anyRequest().authenticated();
            }
        );
        //Use HTTP Basic
        http.httpBasic(Customizer.withDefaults());

        //Set Login form
        http.formLogin(login ->{
            login.loginPage("/login");
            login.usernameParameter("user");
            login.passwordParameter("pass");
            login.failureUrl("/login");
        });
        //Set Logout
        http.logout(Customizer.withDefaults());

        //Disable CSRF for now
        http.csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

}
