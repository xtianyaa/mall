package com.yuan.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mall.entity.MallCartEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车 Mapper
 */
@Mapper
public interface MallCartMapper extends BaseMapper<MallCartEntity> {
}

