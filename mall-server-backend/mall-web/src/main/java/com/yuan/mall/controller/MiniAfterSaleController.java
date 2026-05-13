package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import com.yuan.mall.dto.AfterSaleApplyDTO;
import com.yuan.mall.service.AfterSaleService;
import com.yuan.mall.vo.AfterSaleVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序售后控制器
 */
@Validated
@RestController
@RequestMapping("/mall/mini/afterSale")
public class MiniAfterSaleController {

    private final AfterSaleService afterSaleService;

    public MiniAfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    /**
     * 提交售后申请
     */
    @PostMapping("/apply")
    public Result<AfterSaleVO> apply(@Valid @RequestBody AfterSaleApplyDTO dto) {
        return Result.success(afterSaleService.createAfterSale(dto));
    }

    /**
     * 查询售后详情
     */
    @GetMapping("/detail")
    public Result<AfterSaleVO> detail(@RequestParam Long id) {
        return Result.success(afterSaleService.getAfterSaleDetail(id));
    }
}

