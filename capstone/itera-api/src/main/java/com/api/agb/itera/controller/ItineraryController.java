package com.api.agb.itera.controller;

import com.api.agb.itera.dto.TripPlanRequest;
import com.api.agb.itera.dto.ItineraryResponse;
import com.api.agb.itera.service.OpenAiItineraryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itinerario")
@CrossOrigin(origins={"http://localhost:4200"})
public class ItineraryController {

    private final OpenAiItineraryService service;

    public ItineraryController(OpenAiItineraryService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ItineraryResponse generate(@Valid @RequestBody TripPlanRequest plan) {
        return service.generateItinerary(plan);
    }

    @GetMapping()
    public void getItinerario(@RequestParam String itinerarioId){

    }

    @PostMapping("save")
    public void saveItinerario(){

    }

}
