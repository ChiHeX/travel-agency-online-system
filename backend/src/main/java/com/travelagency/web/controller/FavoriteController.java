package com.travelagency.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.common.security.CurrentUser;
import com.travelagency.domain.dto.FavoriteRequest;
import com.travelagency.domain.entity.Favorite;
import com.travelagency.domain.entity.TravelRoute;
import com.travelagency.domain.mapper.FavoriteMapper;
import com.travelagency.domain.mapper.TravelRouteMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteMapper favoriteMapper;
    private final TravelRouteMapper routeMapper;

    public FavoriteController(FavoriteMapper favoriteMapper, TravelRouteMapper routeMapper) {
        this.favoriteMapper = favoriteMapper;
        this.routeMapper = routeMapper;
    }

    @GetMapping
    public ApiResponse<List<TravelRoute>> list() {
        Long userId = CurrentUser.required().userId();
        return ApiResponse.ok(favoriteMapper.selectList(new QueryWrapper<Favorite>().eq("user_id", userId))
                .stream().map(favorite -> routeMapper.selectById(favorite.routeId))
                .filter(route -> route != null && Integer.valueOf(0).equals(route.deleted))
                .toList());
    }

    @PostMapping
    public ApiResponse<Void> add(@Valid @RequestBody FavoriteRequest request) {
        Long userId = CurrentUser.required().userId();
        if (routeMapper.selectById(request.routeId()) == null) {
            throw new BusinessException(404, "线路不存在");
        }
        Favorite existing = favoriteMapper.selectOne(new QueryWrapper<Favorite>()
                .eq("user_id", userId).eq("route_id", request.routeId()));
        if (existing == null) {
            Favorite favorite = new Favorite();
            favorite.userId = userId;
            favorite.routeId = request.routeId();
            favoriteMapper.insert(favorite);
        }
        return ApiResponse.ok();
    }

    @DeleteMapping("/{routeId}")
    public ApiResponse<Void> remove(@PathVariable Long routeId) {
        favoriteMapper.delete(new QueryWrapper<Favorite>().eq("user_id", CurrentUser.required().userId())
                .eq("route_id", routeId));
        return ApiResponse.ok();
    }
}
