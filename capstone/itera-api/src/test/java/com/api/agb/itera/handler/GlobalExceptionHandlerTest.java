package com.api.agb.itera.handler;

import com.api.agb.itera.dto.ErrorResponse;
import com.api.agb.itera.dto.ValidationErrorResponse;
import com.api.agb.itera.exception.CorreoDuplicadoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleCorreoDuplicado_returnsConflict() {

        CorreoDuplicadoException ex =
                new CorreoDuplicadoException(
                        "El correo ya está registrado"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleCorreoDuplicado(ex);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                409,
                response.getBody().status()
        );

        assertEquals(
                "CONFLICT",
                response.getBody().error()
        );

        assertEquals(
                "El correo ya está registrado",
                response.getBody().message()
        );
    }

    @Test
    void handleDataIntegrityViolation_returnsDuplicateEmailMessage() {

        RuntimeException rootCause =
                new RuntimeException(
                        "UK_USUARIO_CORREO"
                );

        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "error",
                        rootCause
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertEquals(
                "El correo ya está registrado",
                response.getBody().message()
        );
    }

    @Test
    void handleDataIntegrityViolation_returnsGenericMessage() {

        DataIntegrityViolationException ex =
                new DataIntegrityViolationException(
                        "error"
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleDataIntegrityViolation(ex);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertEquals(
                "Violación de integridad de datos",
                response.getBody().message()
        );
    }

    @Test
    void handleValidationErrors_returnsBadRequest() {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(
                        new Object(),
                        "usuario"
                );

        bindingResult.addError(
                new FieldError(
                        "usuario",
                        "correo",
                        "El correo es obligatorio"
                )
        );

        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult())
                .thenReturn(bindingResult);

        when(request.getRequestURI())
                .thenReturn("/api/usuario/add");

        ResponseEntity<ValidationErrorResponse> response =
                handler.handleValidationErrors(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "Validation failed",
                response.getBody().message()
        );

        assertEquals(
                "/api/usuario/add",
                response.getBody().path()
        );

        assertEquals(
                1,
                response.getBody().details().size()
        );
    }

    @Test
    void handleInvalidJson_returnsBadRequest() {

        HttpMessageNotReadableException exception =
                mock(HttpMessageNotReadableException.class);

        when(request.getRequestURI())
                .thenReturn("/api/usuario/add");

        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidJson(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                "BAD_REQUEST",
                response.getBody().get("error")
        );

        assertEquals(
                "/api/usuario/add",
                response.getBody().get("path")
        );

        assertEquals(
                "El cuerpo de la solicitud es inválido o está mal formado",
                response.getBody().get("message")
        );
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {

        ConstraintViolation<?> violation =
                mock(ConstraintViolation.class);

        Path path = mock(Path.class);

        when(path.toString())
                .thenReturn("correo");

        when(violation.getPropertyPath())
                .thenReturn(path);

        when(violation.getMessage())
                .thenReturn("Correo inválido");

        ConstraintViolationException exception =
                new ConstraintViolationException(
                        Set.of(violation)
                );

        when(request.getRequestURI())
                .thenReturn("/api/usuario");

        ResponseEntity<Map<String, Object>> response =
                handler.handleConstraintViolation(
                        exception,
                        request
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertEquals(
                "Validation failed",
                response.getBody().get("message")
        );

        assertEquals(
                "/api/usuario",
                response.getBody().get("path")
        );

        Map<?, ?> details =
                (Map<?, ?>) response.getBody().get("details");

        assertEquals(
                "Correo inválido",
                details.get("correo")
        );
    }
}