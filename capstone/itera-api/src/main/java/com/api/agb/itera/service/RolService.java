package com.api.agb.itera.service;

import com.api.agb.itera.model.Rol;
import com.api.agb.itera.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {
    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository){
        this.rolRepository = rolRepository;
    }

    public Rol getRol(Long rolId){
        return rolRepository.findById(rolId).orElseThrow(()-> new IllegalArgumentException("No se encontró el rol"));
    }

    public List<Rol> getRoles(){
        return rolRepository.findAll();
    }

    public Rol crearRol(Rol rol){
        if(rolRepository.existsByNombreIgnoreCase(rol.getNombre())) {
            throw new IllegalArgumentException("El rol ya está registrado");
        }
        return rolRepository.save(rol);
    }

    public Rol updateRol(Rol updatedRol){
        if(rolRepository.existsByNombreIgnoreCase(updatedRol.getNombre())) {
            throw new IllegalArgumentException("El rol ya está registrado");
        }
        return rolRepository.save(updatedRol);
    }
}
