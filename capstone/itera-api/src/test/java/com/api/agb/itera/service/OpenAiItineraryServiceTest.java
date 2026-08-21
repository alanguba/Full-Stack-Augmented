package com.api.agb.itera.service;

import com.api.agb.itera.dto.ItineraryResponse;
import com.api.agb.itera.dto.PexelsPhoto;
import com.api.agb.itera.dto.TripPlanRequest;
import com.api.agb.itera.dto.TripPlanRequest.PlaceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class OpenAiItineraryServiceTest {

    @Mock
    private RestClient openAi;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private ObjectMapper mapper;

    private OpenAiItineraryService service;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        service = new OpenAiItineraryService(
                openAi,
                mapper,
                "gpt-4.1"
        );

        when(openAi.post())
                .thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/responses"))
                .thenReturn(requestBodySpec);

        doReturn(requestBodySpec)
                .when(requestBodySpec)
                .body(any(Object.class));

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);
    }

    @Test
    void generateItinerary_returnsItineraryUsingDirectOutputText() throws Exception {

        String json = """
                {
                  "destination":"Cancun",
                  "days":3,
                  "pace":"Relaxed",
                  "itinerary":[]
                }
                """;

        ItineraryResponse expectedResponse =
                new ItineraryResponse(
                        "Cancun",
                        3,
                        "Relaxed",
                        List.of()
                );

        when(responseSpec.body(Map.class))
                .thenReturn(Map.of("output_text", json));

        when(mapper.readValue(
                any(String.class),
                eq(ItineraryResponse.class)
        )).thenReturn(expectedResponse);

        ItineraryResponse response =
                service.generateItinerary(createRequest());

        assertEquals("Cancun", response.destination());
        assertEquals(3, response.days());
        assertEquals("Relaxed", response.pace());
    }

    @Test
    void generateItinerary_returnsItineraryUsingOutputStructure() throws Exception {

        String json = """
                {
                  "destination":"Paris",
                  "days":5,
                  "pace":"Balanced",
                  "itinerary":[]
                }
                """;

        Map<String, Object> raw = Map.of(
                "output",
                List.of(
                        Map.of(
                                "type", "message",
                                "content",
                                List.of(
                                        Map.of(
                                                "type", "output_text",
                                                "text", json
                                        )
                                )
                        )
                )
        );

        ItineraryResponse expectedResponse =
                new ItineraryResponse(
                        "Paris",
                        5,
                        "Balanced",
                        List.of()
                );

        when(responseSpec.body(Map.class))
                .thenReturn(raw);

        when(mapper.readValue(
                any(String.class),
                eq(ItineraryResponse.class)
        )).thenReturn(expectedResponse);

        ItineraryResponse response =
                service.generateItinerary(createRequest());

        assertEquals("Paris", response.destination());
        assertEquals(5, response.days());
        assertEquals("Balanced", response.pace());
    }

    @Test
    void generateItinerary_throwsExceptionWhenJsonIsInvalid() throws Exception {

        when(responseSpec.body(Map.class))
                .thenReturn(Map.of(
                        "output_text",
                        "invalid-json"
                ));

        when(mapper.readValue(
                any(String.class),
                eq(ItineraryResponse.class)
        )).thenThrow(new RuntimeException("boom"));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateItinerary(createRequest())
                );

        assertTrue(
                exception.getMessage()
                        .contains("OpenAI response was not valid JSON")
        );
    }

    @Test
    void generateItinerary_throwsExceptionWhenOutputTextCannotBeExtracted() {

        when(responseSpec.body(Map.class))
                .thenReturn(Map.of());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> service.generateItinerary(createRequest())
                );

        assertEquals(
                "Could not extract output_text from OpenAI response",
                exception.getMessage()
        );
    }

    private TripPlanRequest createRequest() {

        return new TripPlanRequest(
                "Cancun",
                3,
                "Relaxed",
                List.of(
                        new PlaceDto(
                                "place-1",
                                "Playa Delfines",
                                "Cancun",
                                "Beach"
                        )
                )
        );
    }
}