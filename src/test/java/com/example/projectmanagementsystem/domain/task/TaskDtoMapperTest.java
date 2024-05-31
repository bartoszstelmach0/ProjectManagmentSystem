package com.example.projectmanagementsystem.domain.task;

import com.example.projectmanagementsystem.domain.task.dto.TaskDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskDtoMapperTest {

    private TaskDtoMapper taskDtoMapper;

    @BeforeEach
    public void init(){
        taskDtoMapper = new TaskDtoMapper();
    }

    @Test
    public void shouldCorrectlyMapToDto(){
        // given
        Task task = Task.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status(Task.TaskStatus.TO_DO)
                .build();
        //  when
        TaskDto taskDto = taskDtoMapper.map(task);
        //then
        assertNotNull(taskDto);
        assertEquals(task.getId(),taskDto.getId());
        assertEquals(task.getName(),taskDto.getName());
        assertEquals(task.getDescription(),taskDto.getDescription());
        assertEquals(task.getDeadline(),taskDto.getDeadline());
        assertEquals(task.getStatus().toString(),taskDto.getStatus());
    }
    @Test
    public void shouldCorrectlyMapToEntity(){
        //given
        TaskDto taskDto = TaskDto.builder()
                .id(1L)
                .name("Test task")
                .description("Test description")
                .deadline(LocalDateTime.now())
                .status("TO_DO")
                .build();
        //when
        Task task = taskDtoMapper.map(taskDto);
        //then
        assertNotNull(task);
        assertEquals(taskDto.getId(),task.getId());
        assertEquals(taskDto.getName(),task.getName());
        assertEquals(taskDto.getDescription(),task.getDescription());
        assertEquals(Task.TaskStatus.TO_DO,task.getStatus());
    }
}