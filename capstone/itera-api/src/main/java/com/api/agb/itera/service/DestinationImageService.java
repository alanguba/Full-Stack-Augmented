package com.api.agb.itera.service;

import com.api.agb.itera.dto.DestinationImageResponse;
import com.api.agb.itera.dto.PexelsPhoto;
import com.api.agb.itera.dto.PexelsSearchResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DestinationImageService {

    private final RestClient pexelsRestClient;

    public DestinationImageService(RestClient pexelsRestClient) {
        this.pexelsRestClient = pexelsRestClient;
    }

    public DestinationImageResponse getImageByPlace(String place) {
        String query = buildQuery(place);

        PexelsSearchResponse response = pexelsRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("query", query)
                        .queryParam("per_page", 1)
                        .queryParam("orientation", "landscape")
                        .build())
                .retrieve()
                .body(PexelsSearchResponse.class);

        if (response == null || response.photos() == null || response.photos().isEmpty()) {
            return fallback(place);
        }

        PexelsPhoto photo = response.photos().get(0);

        String imageUrl = null;
        if (photo.src() != null) {
            imageUrl = photo.src().landscape() != null
                    ? photo.src().landscape()
                    : photo.src().large();
        }

        if (imageUrl == null || imageUrl.isBlank()) {
            return fallback(place);
        }

        return new DestinationImageResponse(
                imageUrl,
                photo.photographer(),
                photo.photographer_url(),
                photo.url(),
                photo.alt() != null && !photo.alt().isBlank()
                        ? photo.alt()
                        : "Imagen de " + place
        );
    }

    private String buildQuery(String place) {
        return place + " city travel landmark";
    }

    private DestinationImageResponse fallback(String place) {
        return new DestinationImageResponse(
                "/assets/images/destination-placeholder.jpg",
                null,
                null,
                null,
                "Imagen de referencia de " + place
        );
    }

}
