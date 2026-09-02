package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("consultation_reply")
public class ConsultationReply extends BaseEntity {
    public Long consultationId;
    public Long staffId;
    public String content;
}
