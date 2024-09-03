package com.example.projectmanagementsystem.domain.User;

import com.example.projectmanagementsystem.domain.Role.Role;
import com.example.projectmanagementsystem.domain.Role.RoleRepository;
import com.example.projectmanagementsystem.domain.User.auth.UserCredentialsDto;
import com.example.projectmanagementsystem.domain.User.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private  final RoleRepository roleRepository;
    private final UserDtoMapper mapper;
    private final PasswordEncoder passwordEncoder;



    public List<UserDto> getAllUsers(){
        return userRepository.findAll()
                .stream().map(mapper::map)
                .collect(Collectors.toList());
    }

    public Optional<UserDto> findUserById(Long id){
        return userRepository.findById(id).map(mapper::map);
    }


    public Optional<UserCredentialsDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserCredentialsDto(
                        user.getEmail(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet())
                ));
    }


    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null!");
        }

        Role role = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User userToSave = mapper.map(userDto);
        userToSave.setRoles(roles);

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
            if(!passwordEncoder.matches(userDto.getPassword(),existingUser.getPassword())){
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
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
