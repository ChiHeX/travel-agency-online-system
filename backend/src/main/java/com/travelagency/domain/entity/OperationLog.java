package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("operation_log")
public class OperationLog extends BaseEntity {
    public Long operatorId;
    public String module;
    public String operationType;
    public String objectType;
    public String objectId;
    public String result;
    public String detail;
    public String ipAddress;
}
