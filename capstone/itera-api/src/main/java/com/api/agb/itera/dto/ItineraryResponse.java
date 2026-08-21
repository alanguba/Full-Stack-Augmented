package com.api.agb.itera.dto;

import java.util.List;

public record ItineraryResponse(
        String destination,
        int days,
        String pace,
        List<DayPlan> itinerary
) {
    public record DayPlan(
            int day,
            String title,
            String summary,
            List<Stop> stops
    ) {}
    public record Stop(
            String time,
            String name,
            String note
    ) {}
}
