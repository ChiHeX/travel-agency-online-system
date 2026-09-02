package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("travel_guide_article")
public class TravelGuideArticle extends BaseEntity {
    public String title;
    public String summary;
    public String content;
    public String city;
    public String destination;
    public Long attractionId;
    public String coverUrl;
    public String status;
    public Long authorId;
    public java.time.LocalDateTime publishedAt;
}
