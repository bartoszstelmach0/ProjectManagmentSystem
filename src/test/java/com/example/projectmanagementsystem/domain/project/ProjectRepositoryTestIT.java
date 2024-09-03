package com.example.projectmanagementsystem.domain.project;

import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProjectRepositoryTestIT {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    private Project defaultProject;
    private User defaultUser;

    @BeforeEach
    public void init(){
        defaultUser = new User();
        defaultUser.setUsername("User");
        defaultUser.setEmail("example@example.com");
        defaultUser.setPassword("1222");
        userRepository.save(defaultUser);

        defaultProject = new Project();
        defaultProject.setName("Test Project");
        defaultProject.setDescription("Test Project Description");
        defaultProject.setStartDate(LocalDateTime.now().plusHours(1));
        defaultProject.setEndDate(LocalDateTime.now().plusDays(1));
        defaultProject.setUser(defaultUser);
        projectRepository.save(defaultProject);
    }

    @Test
    void shouldFindByNameContainingIgnoreCase(){
        //given
        String name = "test";
        //when
        List<Project> projects =
                projectRepository.findByNameContainingIgnoreCase(name);
        //then
        assertFalse(projects.isEmpty());
        assertEquals(defaultProject.getName(),projects.get(0).getName());
    }

    @Test
    void shouldFindByStartDateBetween(){
        //given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        //when
        List<Project> result = projectRepository.findByStartDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(defaultProject.getStartDate(),result.get(0).getStartDate());
        assertEquals(defaultProject.getEndDate(),result.get(0).getEndDate());
    }

    @Test
    void shouldFindByEndDateBetween(){
        //given
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        //when
        List<Project> result = projectRepository.findByEndDateBetween(startDate, endDate);
        //then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(defaultProject.getStartDate(), result.get(0).getStartDate());
        assertEquals(defaultProject.getEndDate(), result.get(0).getEndDate());
    }
}