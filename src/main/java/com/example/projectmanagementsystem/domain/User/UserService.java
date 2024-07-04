package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserDtoMapper mapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserDtoMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream().map(mapper::map)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> findUserById(Long id){
        return userRepository.findById(id).map(mapper::map);
    }

    public UserDto createUser (UserDto userDto){
        if (userDto == null){
            throw  new IllegalArgumentException("UserDto cannot be null!");
        }
        User userToSave = mapper.map(userDto);
        User savedUser = userRepository.save(userToSave);
        return mapper.map(savedUser);
    }

    public Optional<UserDto> updateUser (Long id, UserDto userDto){
        if (id == null || id < 0 || userDto == null){
            throw new IllegalArgumentException("Invalid parameters!");
        }
       return userRepository.findById(id).map(existingUser -> {
            existingUser.setUsername(userDto.getUsername());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setPassword(userDto.getPassword());
            existingUser.setRoles(userDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(Role.RoleName.valueOf(roleName))
                            .orElseThrow(() -> new RuntimeException("Role not found " + roleName)))
                    .collect(Collectors.toSet()));
           User savedUser = userRepository.save(existingUser);
           return mapper.map(savedUser);
       });
    }

    public void deleteUser(Long id){
        if(id == null){
            throw new IllegalArgumentException("Argument is not valid!");
        } else{
            userRepository.deleteById(id);
        }
    }
}
