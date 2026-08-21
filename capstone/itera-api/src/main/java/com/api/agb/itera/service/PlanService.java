package com.api.agb.itera.service;

import com.api.agb.itera.dto.ItinerarySaveRequest;
import com.api.agb.itera.model.*;
import com.api.agb.itera.repository.PlanRepository;
import com.api.agb.itera.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {
    private final PlanRepository planRepository;
    private final UsuarioRepository usuarioRepository;

    public PlanService(PlanRepository planRepository, UsuarioRepository usuarioRepository) {
        this.planRepository = planRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Plan saveEntirePlan(ItinerarySaveRequest itinerarySaveRequest, String imageUrl) {
        //Fetch Usuario
        Usuario usuario = usuarioRepository.findById(itinerarySaveRequest.usuario_id())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + itinerarySaveRequest.usuario_id()));

        //Create Plan
        Plan plan = Plan.builder()
                .nombre(itinerarySaveRequest.name())
                .destino(itinerarySaveRequest.destination())
                .dias(itinerarySaveRequest.days())
                .ritmo(itinerarySaveRequest.pace())
                .estado((short) 1)
                .usuario(usuario)
                .url(imageUrl)
                .build();

        // Create Lugares
        if (itinerarySaveRequest.lugares() != null) {
            itinerarySaveRequest.lugares().forEach(place -> {
                Lugar lugar = Lugar.builder()
                        .nombre(place.name())
                        .direccion(place.direction())
                        .categoria(place.category())
                        .build();

                plan.addLugar(lugar);
            });
        }


        // Create Itinerarios
        if (itinerarySaveRequest.itinerary() != null) {
            itinerarySaveRequest.itinerary().forEach(itinerary -> {
                Itinerario itinerario = Itinerario.builder()
                        .dia(itinerary.day())
                        .titulo(itinerary.title())
                        .resumen(itinerary.summary())
                        .build();

                // Create Actividades
                if (itinerary.stops() != null) {
                    itinerary.stops().forEach(stop -> {
                        Actividad actividad = Actividad.builder()
                                .tiempo(stop.time())
                                .nombre(stop.name())
                                .nota(stop.note())
                                .build();

                        itinerario.addActividad(actividad);
                    });
                }

                plan.addItinerario(itinerario);
            });
        }


        return planRepository.save(plan);

    }

    public List<Plan> getAllPlansForUser(Long userId){
        return planRepository.findAllByUsuarioId(userId);
    }

    public Plan getPlanById(Long planId){
        return planRepository.findById(planId).orElseThrow(()-> new IllegalArgumentException("No se encontró el plan seleccionado") );
    }

}
