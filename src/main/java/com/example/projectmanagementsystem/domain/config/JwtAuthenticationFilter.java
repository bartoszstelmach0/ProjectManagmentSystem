package com.example.projectmanagementsystem.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends HttpFilter {

    private static final RequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/auth/login","POST",false);
    private final AuthenticationManager authenticationManager;
    private final AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private final AuthenticationSuccessHandler successHandler;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        successHandler = new JwtAuthenticationSuccessHandler(jwtService);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if(!DEFAULT_ANT_PATH_REQUEST_MATCHER.matches(request)){
            filterChain.doFilter(request,response);
        }else {
            try{
                Authentication authenticationResult = attemptAuthentication(request);
                this.successHandler.onAuthenticationSuccess(request,response,authenticationResult);
            }catch (AuthenticationException e){
                this.failureHandler.onAuthenticationFailure(request,response,e);
            }
        }
    }

    private Authentication attemptAuthentication(HttpServletRequest request) throws IOException {
        JwtAuthenticationToken jwtAuthentication = new ObjectMapper().readValue(request.getInputStream(),JwtAuthenticationToken.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(jwtAuthentication.email,jwtAuthentication.password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    private record JwtAuthenticationToken(String email, String password){}
}
