package com.travelagency.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelagency.domain.entity.Departure;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartureMapper extends BaseMapper<Departure> {
}
