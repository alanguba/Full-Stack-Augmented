package com.api.agb.itera.controller;

import com.api.agb.itera.dto.DestinationImageResponse;
import com.api.agb.itera.dto.ItinerarySaveRequest;
import com.api.agb.itera.model.Itinerario;
import com.api.agb.itera.model.Lugar;
import com.api.agb.itera.model.Plan;
import com.api.agb.itera.service.DestinationImageService;
import com.api.agb.itera.service.PlanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlanControllerTest {

    @Mock
    private PlanService planService;

    @Mock
    private DestinationImageService destinationImageService;

    @InjectMocks
    private PlanController planController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(planController)
                .build();
    }

    @Test
    void savePlan_returnsSavedPlan() throws Exception {

        ItinerarySaveRequest request = createRequest();

        DestinationImageResponse imageResponse =
                new DestinationImageResponse(
                        "https://image.com/cancun.jpg",
                        "John Smith",
                        "https://pexels.com/john",
                        "https://pexels.com/photo/123",
                        "Cancun Beach"
                );

        Plan savedPlan = new Plan();
        savedPlan.setId(1L);
        savedPlan.setNombre("Viaje Cancun");

        when(destinationImageService.getImageByPlace("Cancun Beach Resort"))
                .thenReturn(imageResponse);

        when(planService.saveEntirePlan(
                any(ItinerarySaveRequest.class),
                eq("https://image.com/cancun.jpg")))
                .thenReturn(savedPlan);

        mockMvc.perform(post("/api/plan/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Viaje Cancun")));

        verify(destinationImageService)
                .getImageByPlace("Cancun Beach Resort");

        verify(planService)
                .saveEntirePlan(
                        any(ItinerarySaveRequest.class),
                        eq("https://image.com/cancun.jpg")
                );
    }

    @Test
    void getPlan_returnsPlanDetails() throws Exception {

        Plan plan = createPlan();

        when(planService.getPlanById(1L))
                .thenReturn(plan);

        mockMvc.perform(get("/api/plan/get")
                        .param("planId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Viaje Cancun")))
                .andExpect(jsonPath("$.destination", is("Cancun Beach Resort")))
                .andExpect(jsonPath("$.days", is(5)))
                .andExpect(jsonPath("$.pace", is("Very Relax")))
                .andExpect(jsonPath("$.url", is("https://image.com/cancun.jpg")))

                .andExpect(jsonPath("$.lugares.length()", is(1)))
                .andExpect(jsonPath("$.lugares[0].id", is(100)))
                .andExpect(jsonPath("$.lugares[0].name", is("Playa Delfines")))
                .andExpect(jsonPath("$.lugares[0].direction", is("Blvd Kukulkan")))
                .andExpect(jsonPath("$.lugares[0].category", is("Beach")))

                .andExpect(jsonPath("$.itinerary.length()", is(1)))
                .andExpect(jsonPath("$.itinerary[0].id", is(200)))
                .andExpect(jsonPath("$.itinerary[0].day", is(1)))
                .andExpect(jsonPath("$.itinerary[0].title", is("Arrival Day")))
                .andExpect(jsonPath("$.itinerary[0].summary", is("Enjoy Cancun")));
    }

    @Test
    void getAllPlansByUser_returnsPlans() throws Exception {

        Plan plan = createPlan();

        when(planService.getAllPlansForUser(10L))
                .thenReturn(List.of(plan));

        mockMvc.perform(get("/api/plan")
                        .param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Viaje Cancun")))
                .andExpect(jsonPath("$[0].destino", is("Cancun Beach Resort")))
                .andExpect(jsonPath("$[0].dias", is(5)))
                .andExpect(jsonPath("$[0].ritmo", is("Very Relax")))
                .andExpect(jsonPath("$[0].estado", is(1)))
                .andExpect(jsonPath("$[0].url",
                        is("https://image.com/cancun.jpg")))
                .andExpect(jsonPath("$[0].lugaresCount", is(1)));
    }

    private Plan createPlan() {

        Plan plan = new Plan();

        Lugar lugar = new Lugar();
        lugar.setId(100L);
        lugar.setNombre("Playa Delfines");
        lugar.setDireccion("Blvd Kukulkan");
        lugar.setCategoria("Beach");

        Itinerario itinerario = new Itinerario();
        itinerario.setId(200L);
        itinerario.setDia(1);
        itinerario.setTitulo("Arrival Day");
        itinerario.setResumen("Enjoy Cancun");
        itinerario.setActividades(new ArrayList<>());

        plan.setLugares(List.of(lugar));
        plan.setItinerarios(List.of(itinerario));

        plan.setId(1L);
        plan.setNombre("Viaje Cancun");
        plan.setDestino("Cancun Beach Resort");
        plan.setDias(5);
        plan.setRitmo("Very Relax");
        plan.setEstado((short) 1);
        plan.setUrl("https://image.com/cancun.jpg");
        return plan;
    }

    private ItinerarySaveRequest createRequest() {

        return new ItinerarySaveRequest(
                "Viaje Cancun 2026",      // name
                1L,                       // usuario_id
                "Cancun Beach Resort",    // destination
                5,                        // days
                "Very Relaxed Pace",      // pace

                List.of(
                        new ItinerarySaveRequest.Lugares(
                                "Playa Delfines",
                                "Blvd Kukulkan",
                                "Beach Area"
                        )
                ),

                List.of(
                        new ItinerarySaveRequest.DayPlan(
                                1,
                                "Arrival Day Plan",
                                "Enjoy Cancun and the beach",
                                List.of(
                                        new ItinerarySaveRequest.Stop(
                                                "09:00 AM",
                                                "Beach Stop Location",
                                                "Morning visit at the beach"
                                        )
                                )
                        )
                )
        );
    }
}