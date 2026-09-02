package com.travelagency.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelagency.domain.entity.Traveler;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TravelerMapper extends BaseMapper<Traveler> {
}
