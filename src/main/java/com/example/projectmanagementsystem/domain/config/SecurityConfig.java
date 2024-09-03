package com.example.projectmanagementsystem.domain.config;

import com.example.projectmanagementsystem.domain.config.BearerTokenFilter;
import com.example.projectmanagementsystem.domain.config.JwtAuthenticationFilter;
import com.example.projectmanagementsystem.domain.config.JwtService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            MvcRequestMatcher.Builder mvc,
                                            AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService);
        BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService);
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers(mvc.pattern("/auth/**")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.POST,"/project")).hasRole("ADMIN")
                        .requestMatchers(mvc.pattern("/task/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(mvc.pattern("/users/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(mvc.pattern("/comments/**")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated())
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class)
                .addFilterBefore(bearerTokenFilter, AuthorizationFilter.class)
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
