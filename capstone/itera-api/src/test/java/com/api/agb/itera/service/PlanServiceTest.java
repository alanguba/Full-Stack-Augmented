package com.api.agb.itera.service;

import com.api.agb.itera.dto.ItinerarySaveRequest;
import com.api.agb.itera.model.Plan;
import com.api.agb.itera.model.Usuario;
import com.api.agb.itera.repository.PlanRepository;
import com.api.agb.itera.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PlanService planService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEntirePlan_savesCompletePlan() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        ItinerarySaveRequest request = createRequest();

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(planRepository.save(any(Plan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Plan result = planService.saveEntirePlan(
                request,
                "https://image.com/cancun.jpg"
        );

        assertNotNull(result);
        assertEquals("Viaje Cancun 2026", result.getNombre());
        assertEquals("Cancun Beach Resort", result.getDestino());
        assertEquals(5, result.getDias());
        assertEquals("Very Relaxed Pace", result.getRitmo());
        assertEquals("https://image.com/cancun.jpg", result.getUrl());

        assertEquals(1, result.getLugares().size());
        assertEquals(1, result.getItinerarios().size());

        verify(planRepository).save(any(Plan.class));
    }

    @Test
    void saveEntirePlan_withoutPlaces() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        ItinerarySaveRequest request =
                new ItinerarySaveRequest(
                        "Viaje Cancun 2026",
                        1L,
                        "Cancun Beach Resort",
                        5,
                        "Very Relaxed Pace",
                        null,
                        null
                );

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(planRepository.save(any(Plan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Plan result = planService.saveEntirePlan(
                request,
                "img.jpg"
        );

        assertNotNull(result);
        assertTrue(result.getLugares().isEmpty());
        assertTrue(result.getItinerarios().isEmpty());
    }

    @Test
    void saveEntirePlan_throwsExceptionWhenUserDoesNotExist() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        ItinerarySaveRequest request =
                new ItinerarySaveRequest(
                        "Viaje Cancun 2026",
                        99L,
                        "Cancun Beach Resort",
                        5,
                        "Very Relaxed Pace",
                        List.of(),
                        List.of()
                );

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> planService.saveEntirePlan(
                                request,
                                "img.jpg"
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Usuario no encontrado")
        );

        verify(planRepository, never())
                .save(any());
    }

    @Test
    void getAllPlansForUser_returnsPlans() {

        Plan plan = new Plan();
        plan.setId(1L);

        when(planRepository.findAllByUsuarioId(10L))
                .thenReturn(List.of(plan));

        List<Plan> result =
                planService.getAllPlansForUser(10L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(planRepository)
                .findAllByUsuarioId(10L);
    }

    @Test
    void getPlanById_returnsPlan() {

        Plan plan = new Plan();
        plan.setId(1L);

        when(planRepository.findById(1L))
                .thenReturn(Optional.of(plan));

        Plan result =
                planService.getPlanById(1L);

        assertEquals(1L, result.getId());

        verify(planRepository)
                .findById(1L);
    }

    @Test
    void getPlanById_throwsExceptionWhenPlanDoesNotExist() {

        when(planRepository.findById(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> planService.getPlanById(999L)
                );

        assertEquals(
                "No se encontró el plan seleccionado",
                exception.getMessage()
        );
    }

    @Test
    void saveEntirePlan_createsActivitiesAndPlacesCorrectly() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(planRepository.save(any(Plan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        planService.saveEntirePlan(
                createRequest(),
                "image.jpg"
        );

        ArgumentCaptor<Plan> captor =
                ArgumentCaptor.forClass(Plan.class);

        verify(planRepository).save(captor.capture());

        Plan savedPlan = captor.getValue();

        assertEquals(
                1,
                savedPlan.getLugares().size()
        );

        assertEquals(
                1,
                savedPlan.getItinerarios().size()
        );

        assertEquals(
                1,
                savedPlan.getItinerarios()
                        .get(0)
                        .getActividades()
                        .size()
        );
    }

    private ItinerarySaveRequest createRequest() {

        return new ItinerarySaveRequest(
                "Viaje Cancun 2026",
                1L,
                "Cancun Beach Resort",
                5,
                "Very Relaxed Pace",
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