package com.example.projectmanagementsystem.domain.User;

import org.checkerframework.checker.units.qual.N;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTestIT {

    @Autowired
    UserRepository userRepository;
    private User defaultUser;
    @BeforeEach
    public void init(){
        defaultUser = new User();
        defaultUser.setUsername("User");
        defaultUser.setEmail("example@example.com");
        defaultUser.setPassword("1222");
        userRepository.save(defaultUser);
    }

    @Test
    void shouldFindByEmailCorrectly(){
        //when
        Optional<User> result = userRepository.findByEmail(defaultUser.getEmail());
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(result.get().getEmail(),defaultUser.getEmail());
    }
}