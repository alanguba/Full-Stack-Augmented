package com.api.agb.itera.controller;


import com.api.agb.itera.dto.DestinationImageResponse;
import com.api.agb.itera.service.DestinationImageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/destination-images")
@CrossOrigin(origins = "http://localhost:4200")
public class DestinationImageController {

    private final DestinationImageService destinationImageService;

    public DestinationImageController(DestinationImageService destinationImageService) {
        this.destinationImageService = destinationImageService;
    }

    @GetMapping
    public DestinationImageResponse getImage(@RequestParam String place) {
        return destinationImageService.getImageByPlace(place);
    }
}
