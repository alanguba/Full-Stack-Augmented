package com.api.agb.itera.controller;

import com.api.agb.itera.dto.RolDto;
import com.api.agb.itera.dto.UsuarioAddRequest;
import com.api.agb.itera.dto.UsuarioAddResponse;
import com.api.agb.itera.dto.UsuarioDto;
import com.api.agb.itera.dto.UsuarioEditRequest;
import com.api.agb.itera.service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setValidator(validator)
                .build();
    }

    @Test
    void getUsuarios_returnsOkWithUsuarioList() throws Exception {
        UsuarioDto usuario = new UsuarioDto(
                1L,
                "test@example.com",
                "Alan",
                "Gutiérrez",
                "Banuelos",
                (short) 1,
                new RolDto(1L, "ADMINISTRADOR", "Admin role")
        );

        when(usuarioService.getAllUsers()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuario/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].correo", is("test@example.com")))
                .andExpect(jsonPath("$[0].rol.nombre", is("ADMINISTRADOR")));
    }

    @Test
    void addUsuario_returnsCreatedWhenRequestIsValid() throws Exception {
        UsuarioAddRequest request = new UsuarioAddRequest(
                "test@example.com",
                "Alan",
                "Gutiérrez",
                "Banuelos",
                new RolDto(2L, "USUARIO", "Standard user")
        );

        UsuarioAddResponse response = new UsuarioAddResponse(
                10L,
                request.correo(),
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                (short) 1,
                request.rol()
        );

        when(usuarioService.addUsuario(any(UsuarioAddRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/usuario/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.correo", is("test@example.com")))
                .andExpect(jsonPath("$.rol.nombre", is("USUARIO")));
    }

    @Test
    void addUsuario_returnsBadRequestWhenEmailIsMissing() throws Exception {
        String invalidJson = "{\"nombre\":\"Alan\",\"apellidoPaterno\":\"Gutiérrez\",\"rol\":{\"id\":2,\"nombre\":\"USUARIO\",\"description\":\"Standard user\"}}";

        mockMvc.perform(post("/api/usuario/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUsuario_returnsOkWhenUpdateSucceeds() throws Exception {
        UsuarioEditRequest request = new UsuarioEditRequest(
                5L,
                "updated@example.com",
                "Alan",
                "Gutiérrez",
                "Banuelos",
                (short) 1,
                new com.api.agb.itera.model.Rol(2L, "USUARIO", "Standard user")
        );

        UsuarioAddResponse response = new UsuarioAddResponse(
                request.id(),
                request.correo(),
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.estado(),
                new RolDto(request.rol().getId(), request.rol().getNombre(), request.rol().getDescripcion())
        );

        when(usuarioService.updateUsuario(any(UsuarioEditRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/usuario/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.correo", is("updated@example.com")));
    }

    @Test
    void updateStatus_returnsOkWhenStatusChanges() throws Exception {
        UsuarioEditRequest request = new UsuarioEditRequest(
                5L,
                "test@example.com",
                "Alan",
                "Gutiérrez",
                "Banuelos",
                (short) 0,
                new com.api.agb.itera.model.Rol(2L, "USUARIO", "Standard user")
        );

        UsuarioAddResponse response = new UsuarioAddResponse(
                request.id(),
                request.correo(),
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.estado(),
                new RolDto(request.rol().getId(), request.rol().getNombre(), request.rol().getDescripcion())
        );

        when(usuarioService.updateUsuarioStatus(any(UsuarioEditRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/usuario/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is(0)));
    }

    @Test
    void getUsuarios_returnsEmptyListWhenNoUsersExist() throws Exception {
        when(usuarioService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuario/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(0)));
    }
}
