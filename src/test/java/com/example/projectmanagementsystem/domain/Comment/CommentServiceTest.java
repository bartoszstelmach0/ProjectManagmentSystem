package com.example.projectmanagementsystem.domain.Comment;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.example.projectmanagementsystem.domain.User.User;
import com.example.projectmanagementsystem.domain.project.Project;
import com.example.projectmanagementsystem.domain.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentDtoMapper commentDtoMapper;

    private CommentService commentService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
        commentService = new CommentService(commentRepository,commentDtoMapper);
    }

    @Test
    void shouldReturnAllComments(){
        //given
        Comment comment = new Comment();
        CommentDto commentDto = new CommentDto();

        Mockito.when(commentRepository.findAll()).thenReturn(Collections.singletonList(comment));
        Mockito.when(commentDtoMapper.map(comment)).thenReturn(commentDto);
        //when
        List<CommentDto> result = commentService.getAllComments();
        //then
        assertNotNull(result);
        assertEquals(1,result.size());
        assertEquals(commentDto,result.get(0));

        Mockito.verify(commentRepository).findAll();
        Mockito.verify(commentDtoMapper).map(comment);

    }

    @Test
    void shouldReturnEmptyListWithNoComments(){
        //given
        Mockito.when(commentRepository.findAll()).thenReturn(Collections.emptyList());
        //when
        List<CommentDto> result = commentService.getAllComments();
        //then
        assertNotNull(result);
        assertEquals(0,result.size());

        Mockito.verify(commentRepository).findAll();
        Mockito.verify(commentDtoMapper,Mockito.never()).map(Mockito.any(Comment.class));
        Mockito.verify(commentDtoMapper,Mockito.never()).map(Mockito.any(CommentDto.class));
    }

    @Test
    void shouldFindCommentByIdCorrectly(){
        //given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);

        CommentDto commentDto = new CommentDto();

        Comment comment = Comment.builder()
                .id(1L)
                .comment("example")
                .user(user)
                .project(project)
                .task(task)
                .build();
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        Mockito.when(commentDtoMapper.map(comment)).thenReturn(commentDto);
        //when
        Optional<CommentDto> result = commentService.findCommentById(commentId);
        //then
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(commentDto,result.get());

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(commentDtoMapper).map(comment);
    }

    @Test
    void shouldCreateCommentsCorrectly(){
        //given
        Long commentId = 1L;
        User user = new User();
        user.setId(1L);
        Project project = new Project();
        project.setId(1L);
        Task task = new Task();
        task.setId(1L);

        CommentDto commentDto = new CommentDto();

        Comment commentToSave = Comment.builder()
                .id(1L)
                .comment("example")
                .user(user)
                .project(project)
                .task(task)
                .build();
        Comment savedComment = new Comment();
        CommentDto savedCommentDto = new CommentDto();

        Mockito.when(commentDtoMapper.map(commentDto)).thenReturn(commentToSave);
        Mockito.when(commentRepository.save(commentToSave)).thenReturn(savedComment);
        Mockito.when(commentDtoMapper.map(savedComment)).thenReturn(savedCommentDto);
        //when
        CommentDto result = commentService.createComment(commentDto);
        //then
        assertNotNull(result);
        assertEquals(result,savedCommentDto);

        Mockito.verify(commentDtoMapper).map(commentDto);
        Mockito.verify(commentRepository).save(commentToSave);
        Mockito.verify(commentDtoMapper).map(savedComment);
    }

   @Test
   void shouldThrowExceptionWhenCommentDtoIsNull(){
        //given
       CommentDto commentDto = null;

       //when + then
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commentService.createComment(commentDto));
       assertEquals("CommentDto cannot be null!",exception.getMessage());
   }

   @Test
    void shouldDeleteCommentBeCorrect(){
       //given
       Long commentId = 1L;
       //when
       commentService.deleteComment(commentId);
       //then
       Mockito.verify(commentRepository).deleteById(commentId);
   }

   @Test
    void shouldThrowExceptionDurningDeletingComment(){
        //given
        Long commentId = null;
       //when + then
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(commentId));
       assertEquals("Id cannot be null!",exception.getMessage());
   }

   @Test
    void shouldReturnOptionalEmptyWhenCommentIsNotExists(){
        //given
       Long commentId = 1L;
       CommentDto commentDto = new CommentDto();
       Mockito.when(commentRepository.existsById(commentId)).thenReturn(false);
       //when
       Optional<CommentDto> result = commentService.updateComment(commentId, commentDto);
       //then
       assertTrue(result.isEmpty());
   }

   @Test
    void shouldUpdateCommentCorrectly(){
       //given
       Long commentId = 1L;
       CommentDto commentToUpdate = new CommentDto();
       Comment entityCommentToUpdate = new Comment();
       Comment updatedComment = new Comment();
       CommentDto updatedCommentDto = new CommentDto();

       Mockito.when(commentRepository.existsById(commentId)).thenReturn(true);
       Mockito.when(commentDtoMapper.map(commentToUpdate)).thenReturn(entityCommentToUpdate);
       Mockito.when(commentRepository.save(entityCommentToUpdate)).thenReturn(updatedComment);
       Mockito.when(commentDtoMapper.map(updatedComment)).thenReturn(updatedCommentDto);

       //when
       Optional<CommentDto> result = commentService.updateComment(commentId, commentToUpdate);
       //then
       assertNotNull(result);
       assertTrue(result.isPresent());
       assertEquals(updatedCommentDto, result.get());
   }
}