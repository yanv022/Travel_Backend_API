package com.finexs.voyages.config;

import com.finexs.voyages.security.CustomAccessDeniedHandler;
import com.finexs.voyages.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/routes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/routes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/routes").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/routes/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/routes/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/routes/*/schedules").permitAll()
                        .requestMatchers(HttpMethod.POST, "/routes/*/schedules").hasRole("MANAGER")
                        .requestMatchers("/bookings/**").hasRole("TRAVELER")
                        .requestMatchers("/auth/me").authenticated()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception ->
                        exception.accessDeniedHandler(accessDeniedHandler)
                );


        return http.build();
    }

}
