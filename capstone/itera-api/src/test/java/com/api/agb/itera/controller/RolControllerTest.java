package com.api.agb.itera.controller;

import com.api.agb.itera.dto.RolCreateRequest;
import com.api.agb.itera.dto.RolUpdateRequest;
import com.api.agb.itera.model.Rol;
import com.api.agb.itera.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(rolController)
                .setValidator(validator)
                .build();
    }

    @Test
    void getAllRoles_returnsOkWithRoleList() throws Exception {

        Rol administrador = createRol(
                1L,
                "ADMINISTRADOR",
                "Rol con permisos administrativos"
        );

        Rol usuario = createRol(
                2L,
                "USUARIO",
                "Rol estándar"
        );

        when(rolService.getRoles())
                .thenReturn(List.of(administrador, usuario));

        mockMvc.perform(get("/api/rol"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre",
                        is("ADMINISTRADOR")))
                .andExpect(jsonPath("$[0].descripcion",
                        is("Rol con permisos administrativos")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre",
                        is("USUARIO")));

        verify(rolService).getRoles();
    }

    @Test
    void getAllRoles_returnsEmptyListWhenNoRolesExist() throws Exception {

        when(rolService.getRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/rol"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rolService).getRoles();
    }

    @Test
    void crearRol_returnsOkWhenRequestIsValid() throws Exception {

        RolCreateRequest request = new RolCreateRequest(
                "EDITOR",
                "Rol encargado de editar contenido"
        );

        Rol savedRol = createRol(
                3L,
                "EDITOR",
                "Rol encargado de editar contenido"
        );

        when(rolService.crearRol(any(Rol.class)))
                .thenReturn(savedRol);

        mockMvc.perform(post("/api/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre",
                        is("EDITOR")))
                .andExpect(jsonPath("$.descripcion",
                        is("Rol encargado de editar contenido")));

        verify(rolService).crearRol(any(Rol.class));
    }

    @Test
    void actualizarRol_returnsOkWhenRequestIsValid() throws Exception {

        RolUpdateRequest request = new RolUpdateRequest(
                2L,
                "USUARIO PREMIUM",
                "Rol actualizado con permisos adicionales"
        );

        Rol existingRol = createRol(
                2L,
                "USUARIO",
                "Rol estándar"
        );

        Rol updatedRol = createRol(
                2L,
                "USUARIO PREMIUM",
                "Rol actualizado con permisos adicionales"
        );

        when(rolService.getRol(2L))
                .thenReturn(existingRol);

        when(rolService.updateRol(any(Rol.class)))
                .thenReturn(updatedRol);

        mockMvc.perform(patch("/api/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.nombre",
                        is("USUARIO PREMIUM")))
                .andExpect(jsonPath("$.descripcion",
                        is("Rol actualizado con permisos adicionales")));

        verify(rolService).getRol(2L);
        verify(rolService).updateRol(any(Rol.class));
    }

    @Test
    void crearRol_returnsBadRequestWhenRequestIsInvalid()
            throws Exception {

        String invalidJson = """
                {
                  "nombre": "",
                  "description": ""
                }
                """;

        mockMvc.perform(post("/api/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(rolService, never())
                .crearRol(any(Rol.class));
    }

    @Test
    void actualizarRol_returnsBadRequestWhenRequestIsInvalid()
            throws Exception {

        String invalidJson = """
                {
                  "id": null,
                  "nombre": "",
                  "description": ""
                }
                """;

        mockMvc.perform(patch("/api/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(rolService, never())
                .updateRol(any(Rol.class));
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