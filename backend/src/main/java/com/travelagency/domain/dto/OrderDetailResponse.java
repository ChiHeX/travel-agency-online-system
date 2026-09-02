package com.travelagency.domain.dto;

import com.travelagency.domain.entity.Departure;
import com.travelagency.domain.entity.Payment;
import com.travelagency.domain.entity.Refund;
import com.travelagency.domain.entity.TravelOrder;
import com.travelagency.domain.entity.TravelRoute;

import java.util.List;

public record OrderDetailResponse(
        TravelOrder order,
        TravelRoute route,
        Departure departure,
        List<TravelerView> travelers,
        Payment payment,
        Refund refund) {
}
