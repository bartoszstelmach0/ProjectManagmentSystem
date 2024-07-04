package com.example.projectmanagementsystem.domain.Role;

import com.example.projectmanagementsystem.domain.Role.dto.RoleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private RoleRepository roleRepository;
    private RoleMapper mapper;

    public RoleService(RoleRepository roleRepository, RoleMapper mapper) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

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
