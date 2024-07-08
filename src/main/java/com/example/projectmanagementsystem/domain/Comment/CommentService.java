package com.example.projectmanagementsystem.domain.Comment;

import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentDtoMapper mapper;

    public CommentService(CommentRepository commentRepository, CommentDtoMapper mapper) {
        this.commentRepository = commentRepository;
        this.mapper = mapper;
    }

    public List<CommentDto> getAllComments(){
        return commentRepository.findAll().stream().map(mapper::map).collect(Collectors.toList());
    }

    public Optional<CommentDto> findCommentById(Long id){
        return commentRepository.findById(id).map(mapper::map);
    }

    public CommentDto createComment(CommentDto commentDto){
        if (commentDto == null){
            throw  new IllegalArgumentException("CommentDto cannot be null!");
        }
        Comment commentToSave = mapper.map(commentDto);
        Comment savedComment = commentRepository.save(commentToSave);
        return mapper.map(savedComment);
    }

    public Optional<CommentDto> updateComment (Long id, CommentDto commentDto) {
        if (!commentRepository.existsById(id)) {
            return Optional.empty();
        }
        commentDto.setId(id);
        Comment commentToUpdate = mapper.map(commentDto);
        Comment updatedComment = commentRepository.save(commentToUpdate);
        return Optional.of(mapper.map(updatedComment));
    }

        public void deleteComment(Long id){
        if(id == null){
            throw  new IllegalArgumentException("Id cannot be null!");
        }
        commentRepository.deleteById(id);
    }
}
