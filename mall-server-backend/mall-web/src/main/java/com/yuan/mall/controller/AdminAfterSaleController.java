package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import com.yuan.mall.dto.AfterSaleHandleDTO;
import com.yuan.mall.dto.AfterSaleQueryDTO;
import com.yuan.mall.service.AfterSaleService;
import com.yuan.mall.vo.AfterSaleVO;
import com.yuan.mall.vo.PageResultVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端售后控制器
 */
@Validated
@RestController
@RequestMapping("/mall/admin/afterSale")
public class AdminAfterSaleController {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AfterSaleService afterSaleService;

    public AdminAfterSaleController(AfterSaleService afterSaleService) {
        this.afterSaleService = afterSaleService;
    }

    @PostMapping("/page")
    public Result<PageResultVO<AfterSaleVO>> page(@Valid @RequestBody AfterSaleQueryDTO dto) {
        if (dto.getPageNum() == null || dto.getPageNum() < 1) {
            dto.setPageNum(DEFAULT_PAGE_NUM);
        }
        if (dto.getPageSize() == null || dto.getPageSize() < 1) {
            dto.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return Result.success(afterSaleService.pageAfterSale(dto));
    }

    @GetMapping("/detail")
    public Result<AfterSaleVO> detail(@RequestParam Long id) {
        return Result.success(afterSaleService.getAfterSaleDetail(id));
    }

    @PostMapping("/handle")
    public Result<Boolean> handle(@Valid @RequestBody AfterSaleHandleDTO dto) {
        return Result.success(afterSaleService.handleAfterSale(dto));
    }
}

