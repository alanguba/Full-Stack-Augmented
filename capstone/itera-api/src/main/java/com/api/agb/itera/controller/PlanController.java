package com.api.agb.itera.controller;

import com.api.agb.itera.dto.*;
import com.api.agb.itera.model.Plan;
import com.api.agb.itera.service.DestinationImageService;
import com.api.agb.itera.service.PlanService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/plan")
@CrossOrigin(origins={"http://localhost:4200"})
public class PlanController {
    private final PlanService planService;
    private final DestinationImageService destinationImageService;

    public PlanController(PlanService planService, DestinationImageService destinationImageService){
        this.planService = planService;
        this.destinationImageService = destinationImageService;
    }

    @PostMapping("/save")
    public ItinerarySaveResponse savePlan(@RequestBody ItinerarySaveRequest itinerarySaveRequest){
        // Retrieve image URL
        DestinationImageResponse destinationImageResponse = destinationImageService.getImageByPlace(itinerarySaveRequest.destination());

        //Save plan
        Plan savedPlan = planService.saveEntirePlan(itinerarySaveRequest, destinationImageResponse.imageUrl());
        return new ItinerarySaveResponse(savedPlan.getId(), savedPlan.getNombre());
    }

    @GetMapping("/get")
    public PlanByIdResponse getPlan(@RequestParam Long planId){
        Plan plan = planService.getPlanById(planId);
        return toDetailedDto(plan);
    }

    @GetMapping()
    public List<AllPlansByUserResponse> getAllPlansByUser(@RequestParam Long userId){
        List<Plan> planes = planService.getAllPlansForUser(userId);

        return planes.stream().map(this::toDto).toList();
    }

    private AllPlansByUserResponse toDto(Plan plan){
        return new AllPlansByUserResponse(
                plan.getId(),
                plan.getNombre(),
                plan.getDestino(),
                plan.getDias(),
                plan.getRitmo(),
                plan.getEstado(),
                plan.getUrl(),
                plan.getCreatedAt(),
                plan.getLugares().size()
        );
    }

    private PlanByIdResponse toDetailedDto(Plan plan){
        List<LugarDto> lugares = new ArrayList<>();
        plan.getLugares().forEach(lugar -> lugares.add(LugarDto.toDto(lugar)));

        List<ItinerarioDto> itinerarios = new ArrayList<>();
        plan.getItinerarios().forEach(itinerario -> itinerarios.add(ItinerarioDto.toDto(itinerario)));

        return new PlanByIdResponse(
                plan.getId(),
                plan.getNombre(),
                plan.getDestino(),
                plan.getDias(),
                plan.getRitmo(),
                plan.getUrl(),
                lugares,
                itinerarios
        );
    }
}
