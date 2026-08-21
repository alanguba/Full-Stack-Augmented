package com.api.agb.itera.controller;

import com.api.agb.itera.dto.DestinationImageResponse;
import com.api.agb.itera.service.DestinationImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DestinationImageControllerTest {

    @Mock
    private DestinationImageService destinationImageService;

    @InjectMocks
    private DestinationImageController destinationImageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(destinationImageController)
                .setValidator(validator)
                .build();
    }

    @Test
    void getImage_returnsImageResponseWhenPlaceIsValid() throws Exception {
        DestinationImageResponse response = new DestinationImageResponse(
                "https://images.pexels.com/photos/12345/pexels-photo-12345.jpeg",
                "Sample City",
                "Sample Photographer",
                "https://www.pexels.com/@sample",
                "A beautiful view of Sample City"
        );

        when(destinationImageService.getImageByPlace("Sample City")).thenReturn(response);

        mockMvc.perform(get("/api/destination-images")
                        .param("place", "Sample City")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getImage_returnsBadRequestWhenPlaceIsMissing() throws Exception {
        mockMvc.perform(get("/api/destination-images")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}