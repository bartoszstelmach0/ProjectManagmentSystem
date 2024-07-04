package com.example.projectmanagementsystem.domain.web;

import com.example.projectmanagementsystem.domain.User.UserDtoMapper;
import com.example.projectmanagementsystem.domain.User.UserService;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final ObjectMapper objectMapper;

    public UserController(UserService userService,ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(){
        List<UserDto> result = userService.getAllUsers();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        return userService.findUserById(id)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser (@RequestBody @Valid UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(uri).body(savedUser);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<?> updateUser (@PathVariable Long id, @RequestBody JsonMergePatch patch){
        try{
            UserDto userDto = userService.findUserById(id).orElseThrow(() -> new EntityNotFoundException("User not found!"));
            UserDto userPatched = applyPatch(userDto,patch);
            userService.updateUser(id,userPatched);
        } catch (JsonPatchException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid patch");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing Error");
        }
        return ResponseEntity.noContent().build();
    }

    private UserDto applyPatch(UserDto userDto, JsonMergePatch patch) throws JsonProcessingException, JsonPatchException {
        JsonNode jsonNode = objectMapper.valueToTree(userDto);
        JsonNode userPatchedNode = patch.apply(jsonNode);
        return objectMapper.treeToValue(userPatchedNode,UserDto.class);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
