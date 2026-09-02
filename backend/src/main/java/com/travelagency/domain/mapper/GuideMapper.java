package com.travelagency.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelagency.domain.entity.Guide;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuideMapper extends BaseMapper<Guide> {
}
