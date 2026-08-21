package com.api.agb.itera.dto;

import java.util.List;

public record PexelsSearchResponse(
        List<PexelsPhoto> photos
) {
}
