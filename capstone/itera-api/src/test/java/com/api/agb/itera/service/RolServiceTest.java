package com.api.agb.itera.service;

import com.api.agb.itera.model.Rol;
import com.api.agb.itera.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRol_returnsRolWhenExists() {

        Rol rol = createRol(
                1L,
                "ADMINISTRADOR",
                "Rol administrador"
        );

        when(rolRepository.findById(1L))
                .thenReturn(Optional.of(rol));

        Rol result = rolService.getRol(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ADMINISTRADOR", result.getNombre());

        verify(rolRepository).findById(1L);
    }

    @Test
    void getRol_throwsExceptionWhenNotFound() {

        when(rolRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rolService.getRol(99L)
                );

        assertEquals(
                "No se encontró el rol",
                exception.getMessage()
        );
    }

    @Test
    void getRoles_returnsAllRoles() {

        Rol rol1 = createRol(
                1L,
                "ADMINISTRADOR",
                "Rol administrador"
        );

        Rol rol2 = createRol(
                2L,
                "USUARIO",
                "Rol usuario"
        );

        when(rolRepository.findAll())
                .thenReturn(List.of(rol1, rol2));

        List<Rol> result = rolService.getRoles();

        assertEquals(2, result.size());
        assertEquals("ADMINISTRADOR", result.get(0).getNombre());
        assertEquals("USUARIO", result.get(1).getNombre());

        verify(rolRepository).findAll();
    }

    @Test
    void crearRol_savesRoleWhenNameDoesNotExist() {

        Rol rol = createRol(
                null,
                "EDITOR",
                "Rol editor"
        );

        Rol savedRol = createRol(
                3L,
                "EDITOR",
                "Rol editor"
        );

        when(rolRepository.existsByNombreIgnoreCase("EDITOR"))
                .thenReturn(false);

        when(rolRepository.save(rol))
                .thenReturn(savedRol);

        Rol result = rolService.crearRol(rol);

        assertEquals(3L, result.getId());
        assertEquals("EDITOR", result.getNombre());

        verify(rolRepository)
                .existsByNombreIgnoreCase("EDITOR");

        verify(rolRepository)
                .save(rol);
    }

    @Test
    void crearRol_throwsExceptionWhenRoleAlreadyExists() {

        // Arrange
        Rol rol = new Rol();
        rol.setNombre("ADMINISTRADOR");
        rol.setDescripcion("Rol con permisos administrativos");

        when(rolRepository.existsByNombreIgnoreCase("ADMINISTRADOR"))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rolService.crearRol(rol)
        );

        // Assert
        assertEquals(
                "El rol ya está registrado",
                exception.getMessage()
        );

        verify(rolRepository).existsByNombreIgnoreCase("ADMINISTRADOR");

        verify(rolRepository, never())
                .save(any(Rol.class));
    }

    private Rol createRol(
            Long id,
            String nombre,
            String descripcion
    ) {

        Rol rol = new Rol();

        rol.setId(id);
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);

        return rol;
    }
}