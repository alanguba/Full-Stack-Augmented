package com.api.agb.itera.service;

import com.api.agb.itera.dto.DestinationImageResponse;
import com.api.agb.itera.dto.PexelsPhoto;
import com.api.agb.itera.dto.PexelsPhotoSrc;
import com.api.agb.itera.dto.PexelsSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DestinationImageServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private DestinationImageService service;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        service = new DestinationImageService(restClient);

        when(restClient.get())
                .thenReturn(requestHeadersUriSpec);

        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve())
                .thenReturn(responseSpec);
    }

    @Test
    void getImageByPlace_returnsLandscapeImage() {

        PexelsPhotoSrc src = new PexelsPhotoSrc(
                "https://image.com/original.jpg",
                "https://image.com/large.jpg",
                "https://image.com/medium.jpg",
                "https://image.com/small.jpg",
                "https://image.com/portrait.jpg",
                "https://image.com/landscape.jpg",
                "https://image.com/tiny.jpg"
        );

        PexelsPhoto photo = new PexelsPhoto(
                1L,
                "https://pexels.com/photo/123",
                "John Doe",
                "https://pexels.com/john",
                src,
                "Beautiful Cancun"
        );

        PexelsSearchResponse response =
                new PexelsSearchResponse(List.of(photo));

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(response);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "https://image.com/landscape.jpg",
                result.imageUrl()
        );

        assertEquals(
                "John Doe",
                result.photographer()
        );

        assertEquals(
                "Beautiful Cancun",
                result.alt()
        );
    }

    @Test
    void getImageByPlace_returnsLargeWhenLandscapeIsNull() {

        PexelsPhotoSrc src = new PexelsPhotoSrc(
                "https://image.com/original.jpg",
                "https://image.com/large.jpg",
                "https://image.com/medium.jpg",
                "https://image.com/small.jpg",
                "https://image.com/portrait.jpg",
                null,
                "https://image.com/tiny.jpg"
        );

        PexelsPhoto photo = new PexelsPhoto(
                1L,
                "https://pexels.com/photo/123",
                "John Doe",
                "https://pexels.com/john",
                src,
                "Cancun"
        );

        PexelsSearchResponse response =
                new PexelsSearchResponse(List.of(photo));

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(response);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "https://image.com/large.jpg",
                result.imageUrl()
        );
    }

    @Test
    void getImageByPlace_returnsFallbackWhenResponseIsNull() {

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(null);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "/assets/images/destination-placeholder.jpg",
                result.imageUrl()
        );

        assertEquals(
                "Imagen de referencia de Cancun",
                result.alt()
        );
    }

    @Test
    void getImageByPlace_returnsFallbackWhenPhotosListIsEmpty() {

        PexelsSearchResponse response =
                new PexelsSearchResponse(List.of());

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(response);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "/assets/images/destination-placeholder.jpg",
                result.imageUrl()
        );
    }

    @Test
    void getImageByPlace_returnsFallbackWhenSrcIsNull() {

        PexelsPhoto photo = new PexelsPhoto(
                1L,
                "https://pexels.com/photo/123",
                "John Doe",
                "https://pexels.com/john",
                null,
                "Cancun"
        );

        PexelsSearchResponse response =
                new PexelsSearchResponse(List.of(photo));

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(response);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "/assets/images/destination-placeholder.jpg",
                result.imageUrl()
        );
    }

    @Test
    void getImageByPlace_usesDefaultAltWhenAltIsBlank() {

        PexelsPhotoSrc src = new PexelsPhotoSrc(
                "https://image.com/original.jpg",
                "https://image.com/large.jpg",
                "https://image.com/medium.jpg",
                "https://image.com/small.jpg",
                "https://image.com/portrait.jpg",
                "https://image.com/landscape.jpg",
                "https://image.com/tiny.jpg"
        );

        PexelsPhoto photo = new PexelsPhoto(
                1L,
                "https://pexels.com/photo/123",
                "John Doe",
                "https://pexels.com/john",
                src,
                ""
        );

        PexelsSearchResponse response =
                new PexelsSearchResponse(List.of(photo));

        when(responseSpec.body(PexelsSearchResponse.class))
                .thenReturn(response);

        DestinationImageResponse result =
                service.getImageByPlace("Cancun");

        assertEquals(
                "Imagen de Cancun",
                result.alt()
        );
    }
}