package com.travelagency.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.travelagency.common.model.BaseEntity;

@TableName("data_source")
public class DataSource extends BaseEntity {
    public String dataName;
    public String source;
    public String sourceType;
    public java.time.LocalDate usedDate;
    public String license;
    public String remark;
}
