package com.api.agb.itera.repository;

import com.api.agb.itera.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findAllByUsuarioId(Long usuarioId);

    List<Plan> findAllByUsuarioIdAndEstado(Long usuarioId, Short estado);

    List<Plan> findAllByDestinoContainingIgnoreCase(String destino);

    boolean existsByUsuarioIdAndNombreIgnoreCase(Long usuarioId, String nombre);
}