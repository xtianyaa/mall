package com.yuan.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mall.entity.MallPayLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付回调日志 Mapper
 */
@Mapper
public interface MallPayLogMapper extends BaseMapper<MallPayLogEntity> {
}

