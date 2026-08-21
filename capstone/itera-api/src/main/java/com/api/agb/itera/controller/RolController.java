package com.api.agb.itera.controller;

import com.api.agb.itera.dto.RolCreateRequest;
import com.api.agb.itera.dto.RolUpdateRequest;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.service.RolService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rol")
@CrossOrigin(origins={"http://localhost:4200"})
public class RolController {
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping()
    public List<Rol> getAllRoles(){
        return rolService.getRoles();
    }

    @PostMapping()
    public Rol crearRol(@RequestBody @Valid RolCreateRequest rolRequest){
        Rol rol = new Rol();
        rol.setNombre(rolRequest.nombre());
        rol.setDescripcion(rolRequest.description());
        return rolService.crearRol(rol);
    }

    @PatchMapping()
    public Rol actualizarRol(@RequestBody @Valid RolUpdateRequest rolRequest){
        Rol rol = rolService.getRol(rolRequest.id());
        rol.setNombre(rolRequest.nombre());
        rol.setDescripcion(rolRequest.description());
        return rolService.updateRol(rol);
    }
}
