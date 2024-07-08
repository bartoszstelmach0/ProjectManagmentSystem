package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.Comment.CommentService;
import com.example.projectmanagementsystem.domain.Comment.dto.CommentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    public CommentController(CommentService commentService, ObjectMapper objectMapper) {
        this.commentService = commentService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(){
        List<CommentDto> result = commentService.getAllComments();
        return  ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id){
        return commentService.findCommentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto commentDto){
        try{
            CommentDto comment = commentService.createComment(commentDto);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(comment.getId()).toUri();
            return ResponseEntity.created(uri).body(comment);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody @Valid CommentDto commentDto){
        Optional<CommentDto> updatedComment = commentService.updateComment(id, commentDto);
        if(updatedComment.isPresent()){
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
  
}
