package com.travelagency.web.controller;

import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.api.PageResponse;
import com.travelagency.domain.dto.RouteDetailResponse;
import com.travelagency.domain.entity.TravelRoute;
import com.travelagency.domain.service.RouteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ApiResponse<PageResponse<TravelRoute>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "12") long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String departureCity,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer durationDays,
            @RequestParam(required = false) Integer departureMonth,
            @RequestParam(defaultValue = "false") boolean hasDeparture) {
        return ApiResponse.ok(PageResponse.from(routeService.pagePublic(page, size, keyword, departureCity,
                destination, minPrice, maxPrice, durationDays, departureMonth, hasDeparture)));
    }

    @GetMapping("/{id}")
    public ApiResponse<RouteDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(routeService.detail(id));
    }
}
