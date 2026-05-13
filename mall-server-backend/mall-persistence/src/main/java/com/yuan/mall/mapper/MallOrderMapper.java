package com.yuan.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mall.entity.MallOrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 */
@Mapper
public interface MallOrderMapper extends BaseMapper<MallOrderEntity> {
}
