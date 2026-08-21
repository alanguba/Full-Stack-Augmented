package com.api.agb.itera.repository;

import com.api.agb.itera.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE LOWER(u.correo) = LOWER(:correo)")
    Optional<Usuario> findByCorreoIgnoreCase(@Param("correo") String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    List<Usuario> findAllByEstado(Short estado);

    List<Usuario> findAllByRolId(Long rolId);

    boolean existsByCorreoAndIdNot(@NotBlank @Email @Size(max = 100) String correo, @NotNull Long id);
}