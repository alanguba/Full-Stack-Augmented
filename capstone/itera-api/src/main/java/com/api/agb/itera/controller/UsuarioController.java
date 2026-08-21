package com.api.agb.itera.controller;

import com.api.agb.itera.dto.UsuarioAddRequest;
import com.api.agb.itera.dto.UsuarioAddResponse;
import com.api.agb.itera.dto.UsuarioDto;
import com.api.agb.itera.dto.UsuarioEditRequest;
import com.api.agb.itera.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins={"http://localhost:4200"})
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UsuarioDto>> getUsuarios(){
        return new ResponseEntity<>(usuarioService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<UsuarioAddResponse> addUsuario(@RequestBody @Valid UsuarioAddRequest usuario){
        return new ResponseEntity<>(usuarioService.addUsuario(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<UsuarioAddResponse> editUsuario(@RequestBody @Valid UsuarioEditRequest usuarioEditRequest){
        return new ResponseEntity<>(usuarioService.updateUsuario(usuarioEditRequest), HttpStatus.OK);

    }

    @PatchMapping("/status")
    public ResponseEntity<UsuarioAddResponse> updateStatus(@RequestBody @Valid UsuarioEditRequest usuarioEditRequest){
        return new ResponseEntity<>(usuarioService.updateUsuarioStatus(usuarioEditRequest), HttpStatus.OK);

    }
}
