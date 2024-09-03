package com.example.projectmanagementsystem.domain.Role;

import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;



    public List<RoleDto> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(mapper::map).collect(Collectors.toList());
    }

    public Optional<RoleDto> getRoleById(Long id){
        if(id == null){
            throw new IllegalArgumentException("Argument is not valid!");
        }
        return roleRepository.findById(id).map(mapper::map);
    }

}
