package com.example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/cashcards/**")
                .hasRole("CARD-OWNER")
                .and()
                .csrf().disable()
                .httpBasic();
        return http.build();
    }

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails kumar = users
                .username("kumar")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build();
        UserDetails sankarNonOwner = users
                .username("sankar")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER")
                .build();
        UserDetails kumar2 = users
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build();

        return new InMemoryUserDetailsManager(kumar, sankarNonOwner, kumar2);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
