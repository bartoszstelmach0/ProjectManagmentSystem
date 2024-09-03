package com.example.projectmanagementsystem.domain.config;

import com.example.projectmanagementsystem.domain.config.exception.JwtAuthenticationException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class BearerTokenFilter extends HttpFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final JwtService jwtService;


    public BearerTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if(authorizationHeader == null || authorizationHeader.isEmpty()){
            log.debug("Missing Authorization header or empty bearer token!");
            filterChain.doFilter(request,response);
        }else{
            String compactJwt = authorizationHeader.substring(BEARER_PREFIX.length());
            SignedJWT signedJWT;
            try{
                signedJWT = SignedJWT.parse(compactJwt);
                verifyJwt(signedJWT);
                setSecurityContext(signedJWT);
                filterChain.doFilter(request,response);
            } catch (ParseException | JwtAuthenticationException e) {
                log.debug(e.getMessage());
                handleAuthenticationFailure(response);
            }
        }
    }
    private void handleAuthenticationFailure(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
    private void setSecurityContext(SignedJWT signedJwt) throws JwtAuthenticationException {
        Authentication authentication = jwtService.createAuthentication(signedJwt);
        SecurityContext securityContext = securityContextHolderStrategy.getContext();
        securityContext.setAuthentication(authentication);
    }

    private void verifyJwt(SignedJWT signedJWT) throws JwtAuthenticationException {
        jwtService.verifySignature(signedJWT);
        jwtService.verifyExpirationTime(signedJWT);
    }
}
