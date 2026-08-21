package com.api.agb.itera.service;

import com.api.agb.itera.dto.ItineraryResponse;
import com.api.agb.itera.dto.TripPlanRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class OpenAiItineraryService {
    private final RestClient openAi;
    private final ObjectMapper mapper;
    private final String model;

    public OpenAiItineraryService(RestClient openAiRestClient,
                                  ObjectMapper mapper,
                                  @Value("${openai.api.model}") String model) {
        this.openAi = openAiRestClient;
        this.mapper = mapper;
        this.model = model;
    }


    public ItineraryResponse generateItinerary(TripPlanRequest plan) {
        String prompt = buildPrompt(plan);

        // Request al endpoint POST /responses (Responses API) [5](https://stackoverflow.com/questions/65478922/namespace-google-maps-has-no-exported-member-mouseevent)

        Map<String, Object> body = Map.of(
                "model", model,
                "input", prompt,
                "temperature", 0.6,
                "text", Map.of(
                        "format", Map.of("type", "json_object")
                )
        );

        // RestClient: post().uri(...).retrieve().body(...) es el patrón estándar de uso [3](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html)
        Map<?, ?> raw = openAi.post()
                .uri("/responses")
                .body(body)
                .retrieve()
                .body(Map.class);

        String outputText = extractOutputText(raw);

        try {
            return mapper.readValue(outputText, ItineraryResponse.class);
        } catch (Exception e) {
            // Si por alguna razón no vino JSON perfecto, puedes manejarlo aquí
            throw new RuntimeException("OpenAI response was not valid JSON for itinerary", e);
        }
    }


    private String buildPrompt(TripPlanRequest plan) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                Eres un asistente de viajes. Genera un itinerario optimizado y realista.
                
                REQUISITO DE SALIDA:
                Devuelve SOLO JSON válido (sin markdown), con esta forma:
                {
                  "destination": string,
                  "days": number,
                  "pace": "Relajado"|"Balanceado"|"Intenso",
                  "itinerary": [
                    {
                      "day": number,
                      "title": string,
                      "summary": string,
                      "stops": [
                        {"time": "HH:MM", "name": string, "note": string}
                      ]
                    }
                  ]
                }
                
                DATOS:
                Destino: %s
                Días: %d
                Ritmo: %s
                
                Lugares seleccionados:
                """.formatted(plan.destination(), plan.days(), plan.pace()));

        for (int i = 0; i < plan.places().size(); i++) {
            var p = plan.places().get(i);
            sb.append("%d. %s (%s) - %s%n".formatted(i + 1, p.name(), nullSafe(p.category()), nullSafe(p.address())));
        }

        sb.append("""
                REGLAS:
                - Distribuye lugares entre días según el ritmo y su cercanía.
                - Incluye pausas y comida.
                - Horarios coherentes y realistas.
                - Evita repetir el mismo lugar.
                """);

        return sb.toString();
    }


    private String nullSafe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Extrae el texto final de la respuesta.
     * (La estructura exacta puede variar según SDK/formato, así que lo hacemos defensivo.)
     */
    @SuppressWarnings("unchecked")
    private String extractOutputText(Map<?, ?> raw) {
        // Forma común: raw["output_text"]
        Object direct = raw.get("output_text");
        if (direct instanceof String s && !s.isBlank()) return s;

        // Fallback: recorrer "output" -> message -> content -> output_text
        Object output = raw.get("output");
        if (output instanceof Iterable<?> iterable) {
            for (Object item : iterable) {
                if (item instanceof Map<?, ?> m && "message".equals(m.get("type"))) {
                    Object content = m.get("content");
                    if (content instanceof Iterable<?> contentList) {
                        for (Object c : contentList) {
                            if (c instanceof Map<?, ?> cm && "output_text".equals(cm.get("type"))) {
                                Object text = cm.get("text");
                                if (text instanceof String s && !s.isBlank()) return s;
                            }
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Could not extract output_text from OpenAI response");
    }


}
