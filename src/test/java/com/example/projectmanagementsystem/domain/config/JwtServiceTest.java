package com.example.projectmanagementsystem.domain.config;

import com.example.projectmanagementsystem.domain.config.exception.JwtAuthenticationException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class JwtServiceTest {

    @Mock
    private JWSAlgorithm jwsAlgorithm;
    @Mock
    private JWSSigner signer;
    @Mock
    private JWSVerifier verifier;

    private final String sharedKey = "test_shared_key_32_characters_long!!";
    private JwtService jwtService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService(sharedKey);
    }

    @Test
    void shouldCreateSignedJwt() throws ParseException, JOSEException {
        //given
        String username = "testUser";
        List<String> authorities = List.of("ROLE_USER");

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        LocalDateTime nowPlusSeconds = LocalDateTime.now().plusSeconds(30 * 24 * 60 * 60);
        Date expirationDate = Date.from(nowPlusSeconds.atZone(ZoneId.systemDefault()).toInstant());

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(expirationDate)
                .claim("authorities", authorities)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);


        //when
        String token = jwtService.createSignedJWT(username, authorities);
        //then
        assertNotNull(token);
        signedJWT = SignedJWT.parse(token);
        assertEquals(username, signedJWT.getJWTClaimsSet().getSubject());
        assertEquals(authorities, signedJWT.getJWTClaimsSet().getStringListClaim("authorities"));
    }

}