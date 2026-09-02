package com.travelagency.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelagency.domain.entity.TravelOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TravelOrderMapper extends BaseMapper<TravelOrder> {
}
