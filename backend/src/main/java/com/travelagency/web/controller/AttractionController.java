package com.travelagency.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travelagency.common.api.ApiResponse;
import com.travelagency.domain.entity.Attraction;
import com.travelagency.domain.mapper.AttractionMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionMapper attractionMapper;

    public AttractionController(AttractionMapper attractionMapper) {
        this.attractionMapper = attractionMapper;
    }

    @GetMapping
    public ApiResponse<List<Attraction>> list(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String city) {
        QueryWrapper<Attraction> query = new QueryWrapper<Attraction>().eq("status", 1);
        if (keyword != null && !keyword.isBlank()) {
            query.and(w -> w.like("name", keyword.trim()).or().like("intro", keyword.trim()));
        }
        if (city != null && !city.isBlank()) {
            query.eq("city", city.trim());
        }
        return ApiResponse.ok(attractionMapper.selectList(query.orderByAsc("name")));
    }
}
