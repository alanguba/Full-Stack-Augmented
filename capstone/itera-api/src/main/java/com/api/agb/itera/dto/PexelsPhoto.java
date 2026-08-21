package com.api.agb.itera.dto;

public record PexelsPhoto(
        Long id,
        String url,
        String photographer,
        String photographer_url,
        PexelsPhotoSrc src,
        String alt

) {
}
