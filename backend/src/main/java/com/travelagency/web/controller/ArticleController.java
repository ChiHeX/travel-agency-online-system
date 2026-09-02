package com.travelagency.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.common.security.CurrentUser;
import com.travelagency.domain.dto.ArticleRequest;
import com.travelagency.domain.entity.TravelGuideArticle;
import com.travelagency.domain.mapper.TravelGuideArticleMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final TravelGuideArticleMapper articleMapper;

    public ArticleController(TravelGuideArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public ApiResponse<List<TravelGuideArticle>> publicList() {
        return ApiResponse.ok(articleMapper.selectList(new QueryWrapper<TravelGuideArticle>()
                .eq("status", "PUBLISHED").orderByDesc("published_at")));
    }

    @GetMapping("/{id}")
    public ApiResponse<TravelGuideArticle> publicDetail(@PathVariable Long id) {
        TravelGuideArticle article = articleMapper.selectById(id);
        if (article == null || !"PUBLISHED".equals(article.status)) {
            throw new BusinessException(404, "攻略不存在");
        }
        return ApiResponse.ok(article);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TravelGuideArticle> create(@Valid @RequestBody ArticleRequest request) {
        TravelGuideArticle article = fromRequest(request);
        article.authorId = CurrentUser.required().userId();
        if ("PUBLISHED".equals(article.status)) {
            article.publishedAt = LocalDateTime.now();
        }
        articleMapper.insert(article);
        return ApiResponse.ok(article);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<TravelGuideArticle> update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        TravelGuideArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(404, "攻略不存在");
        }
        TravelGuideArticle updated = fromRequest(request);
        updated.id = id;
        updated.authorId = article.authorId;
        if ("PUBLISHED".equals(updated.status) && article.publishedAt == null) {
            updated.publishedAt = LocalDateTime.now();
        } else {
            updated.publishedAt = article.publishedAt;
        }
        articleMapper.updateById(updated);
        return ApiResponse.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        articleMapper.deleteById(id);
        return ApiResponse.ok();
    }

    private TravelGuideArticle fromRequest(ArticleRequest request) {
        TravelGuideArticle article = new TravelGuideArticle();
        article.title = request.title();
        article.summary = request.summary();
        article.content = request.content();
        article.city = request.city();
        article.destination = request.destination();
        article.attractionId = request.attractionId();
        article.coverUrl = request.coverUrl();
        article.status = request.status() == null || request.status().isBlank() ? "DRAFT" : request.status();
        return article;
    }
}
