package com.yuan.mall.service;

import com.yuan.mall.dto.AfterSaleApplyDTO;
import com.yuan.mall.dto.AfterSaleHandleDTO;
import com.yuan.mall.dto.AfterSaleQueryDTO;
import com.yuan.mall.vo.AfterSaleVO;
import com.yuan.mall.vo.PageResultVO;

/**
 * 售后服务
 */
public interface AfterSaleService {

    /**
     * 提交售后申请（小程序端）
     */
    AfterSaleVO createAfterSale(AfterSaleApplyDTO dto);

    /**
     * 查询售后详情
     */
    AfterSaleVO getAfterSaleDetail(Long id);

    /**
     * 管理端分页查询售后列表
     */
    PageResultVO<AfterSaleVO> pageAfterSale(AfterSaleQueryDTO dto);

    /**
     * 管理端处理售后单
     */
    Boolean handleAfterSale(AfterSaleHandleDTO dto);
}

