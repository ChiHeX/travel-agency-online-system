package com.travelagency.domain.dto;

import com.travelagency.domain.entity.Departure;
import com.travelagency.domain.entity.RouteItineraryDay;
import com.travelagency.domain.entity.RouteItineraryItem;
import com.travelagency.domain.entity.TravelRoute;

import java.util.List;
import java.util.Map;

public record RouteDetailResponse(
        TravelRoute route,
        List<Departure> departures,
        List<ItineraryDayView> itinerary,
        List<Map<String, Object>> reviews) {

    public record ItineraryDayView(RouteItineraryDay day, List<RouteItineraryItem> items) {
    }
}
