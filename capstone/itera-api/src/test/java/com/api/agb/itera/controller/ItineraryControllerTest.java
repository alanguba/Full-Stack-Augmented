package com.api.agb.itera.controller;

import com.api.agb.itera.dto.ItineraryResponse;
import com.api.agb.itera.dto.TripPlanRequest;
import com.api.agb.itera.dto.TripPlanRequest.PlaceDto;
import com.api.agb.itera.service.OpenAiItineraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ItineraryControllerTest {

    @Mock
    private OpenAiItineraryService service;

    @InjectMocks
    private ItineraryController itineraryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(itineraryController)
                .setValidator(validator)
                .build();
    }

    @Test
    void generate_returnsOkWhenRequestIsValid() throws Exception {

        TripPlanRequest request = new TripPlanRequest(
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

        ItineraryResponse response = new ItineraryResponse(
                "Cancun",
                3,
                "Relaxed",
                List.of(
                        new ItineraryResponse.DayPlan(
                                1,
                                "Arrival Day",
                                "Enjoy the beach",
                                List.of(
                                        new ItineraryResponse.Stop(
                                                "09:00",
                                                "Playa Delfines",
                                                "Morning visit"
                                        )
                                )
                        )
                )
        );

        when(service.generateItinerary(any(TripPlanRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/itinerario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination", is("Cancun")))
                .andExpect(jsonPath("$.days", is(3)))
                .andExpect(jsonPath("$.pace", is("Relaxed")))
                .andExpect(jsonPath("$.itinerary.length()", is(1)))
                .andExpect(jsonPath("$.itinerary[0].day", is(1)))
                .andExpect(jsonPath("$.itinerary[0].title", is("Arrival Day")))
                .andExpect(jsonPath("$.itinerary[0].stops.length()", is(1)))
                .andExpect(jsonPath("$.itinerary[0].stops[0].name",
                        is("Playa Delfines")));

        verify(service).generateItinerary(any(TripPlanRequest.class));
    }

    @Test
    void generate_returnsBadRequestWhenDestinationIsMissing()
            throws Exception {

        String invalidJson = """
            {
              "destination":"",
              "days":3,
              "pace":"Relaxed",
              "places":[
                {
                  "id":"place-1",
                  "name":"Playa Delfines",
                  "address":"Cancun",
                  "category":"Beach"
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/itinerario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generate_returnsBadRequestWhenDaysIsLessThanOne()
            throws Exception {

        String invalidJson = """
            {
              "destination":"Cancun",
              "days":0,
              "pace":"Relaxed",
              "places":[
                {
                  "id":"place-1",
                  "name":"Playa Delfines",
                  "address":"Cancun",
                  "category":"Beach"
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/itinerario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generate_returnsBadRequestWhenPlacesIsEmpty()
            throws Exception {

        String invalidJson = """
            {
              "destination":"Cancun",
              "days":3,
              "pace":"Relaxed",
              "places":[]
            }
            """;

        mockMvc.perform(post("/api/itinerario/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItinerario_returnsOk() throws Exception {

        mockMvc.perform(get("/api/itinerario")
                        .param("itinerarioId", "123"))
                .andExpect(status().isOk());
    }

    @Test
    void saveItinerario_returnsOk() throws Exception {

        mockMvc.perform(post("/api/itinerario/save"))
                .andExpect(status().isOk());
    }
}