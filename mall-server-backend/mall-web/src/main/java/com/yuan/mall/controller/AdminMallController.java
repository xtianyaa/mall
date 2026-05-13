package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import com.yuan.mall.dto.BannerSortUpdateDTO;
import com.yuan.mall.dto.BannerCreateDTO;
import com.yuan.mall.dto.BannerUpdateDTO;
import com.yuan.mall.dto.BannerDeleteDTO;
import com.yuan.mall.dto.CategoryBatchDeleteDTO;
import com.yuan.mall.dto.CategoryCreateDTO;
import com.yuan.mall.dto.CategoryDeleteDTO;
import com.yuan.mall.dto.CategoryStatusUpdateDTO;
import com.yuan.mall.dto.CategoryUpdateDTO;
import com.yuan.mall.dto.CouponCreateDTO;
import com.yuan.mall.dto.GoodsBatchDeleteDTO;
import com.yuan.mall.dto.GoodsCreateDTO;
import com.yuan.mall.dto.GoodsDeleteDTO;
import com.yuan.mall.dto.GoodsStatusUpdateDTO;
import com.yuan.mall.dto.GoodsUpdateDTO;
import com.yuan.mall.dto.OrderShipDTO;
import com.yuan.mall.dto.OrderStatusUpdateDTO;
import com.yuan.mall.dto.SystemSettingUpdateDTO;
import com.yuan.mall.dto.HomeDecoUpdateDTO;
import com.yuan.mall.service.AdminMallService;
import com.yuan.mall.service.MiniMallService;
import com.yuan.mall.vo.LogisticsTraceVO;
import com.yuan.mall.vo.MallBannerVO;
import com.yuan.mall.vo.HomeDecoVO;
import com.yuan.mall.vo.MallCategoryVO;
import com.yuan.mall.vo.MallCouponVO;
import com.yuan.mall.vo.MallDashboardVO;
import com.yuan.mall.vo.MallGoodsVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.MallOrderVO;
import com.yuan.mall.vo.MallSystemSettingVO;
import com.yuan.mall.vo.MallUserAddressVO;
import com.yuan.mall.vo.MallUserCouponVO;
import com.yuan.mall.vo.MallUserVO;
import com.yuan.mall.vo.PageResultVO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商城管理后台控制器
 */
@Validated
@RestController
@RequestMapping("/mall/admin")
public class AdminMallController {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AdminMallService adminMallService;
    private final MiniMallService miniMallService;

    public AdminMallController(AdminMallService adminMallService, MiniMallService miniMallService) {
        this.adminMallService = adminMallService;
        this.miniMallService = miniMallService;
    }

    @GetMapping("/dashboard")
    public Result<MallDashboardVO> dashboard() {
        return Result.success(adminMallService.dashboard());
    }

    @GetMapping("/category/list")
    public Result<PageResultVO<MallCategoryVO>> listCategory(
            @RequestParam(required = false) String nameKeyword,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listCategory(nameKeyword,
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @PostMapping("/category/create")
    public Result<MallCategoryVO> createCategory(@Valid @RequestBody CategoryCreateDTO dto) {
        return Result.success(adminMallService.createCategory(dto));
    }

    @PostMapping("/category/status")
    public Result<Boolean> updateCategoryStatus(@Valid @RequestBody CategoryStatusUpdateDTO dto) {
        return Result.success(adminMallService.updateCategoryStatus(dto));
    }

    @PostMapping("/category/update")
    public Result<MallCategoryVO> updateCategory(@Valid @RequestBody CategoryUpdateDTO dto) {
        return Result.success(adminMallService.updateCategory(dto));
    }

    @PostMapping("/category/delete")
    public Result<Boolean> deleteCategory(@Valid @RequestBody CategoryDeleteDTO dto) {
        return Result.success(adminMallService.deleteCategory(dto));
    }

    @PostMapping("/category/batch-delete")
    public Result<Boolean> batchDeleteCategory(@Valid @RequestBody CategoryBatchDeleteDTO dto) {
        return Result.success(adminMallService.batchDeleteCategory(dto));
    }

    @GetMapping("/goods/list")
    public Result<PageResultVO<MallGoodsVO>> listGoods(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listGoods(keyword, categoryId,
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @PostMapping("/goods/create")
    public Result<MallGoodsVO> createGoods(@Valid @RequestBody GoodsCreateDTO dto) {
        return Result.success(adminMallService.createGoods(dto));
    }

    @PostMapping("/goods/update")
    public Result<MallGoodsVO> updateGoods(@Valid @RequestBody GoodsUpdateDTO dto) {
        return Result.success(adminMallService.updateGoods(dto));
    }

    @PostMapping("/goods/status")
    public Result<Boolean> updateGoodsStatus(@Valid @RequestBody GoodsStatusUpdateDTO dto) {
        return Result.success(adminMallService.updateGoodsStatus(dto));
    }

    @PostMapping("/goods/delete")
    public Result<Boolean> deleteGoods(@Valid @RequestBody GoodsDeleteDTO dto) {
        return Result.success(adminMallService.deleteGoods(dto));
    }

    @PostMapping("/goods/batch-delete")
    public Result<Boolean> batchDeleteGoods(@Valid @RequestBody GoodsBatchDeleteDTO dto) {
        return Result.success(adminMallService.batchDeleteGoods(dto));
    }

    @GetMapping("/order/list")
    public Result<PageResultVO<MallOrderVO>> listOrder(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listOrder(status, userId,
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @GetMapping("/order/detail")
    public Result<MiniOrderVO> getOrderDetail(@RequestParam String orderId) {
        return Result.success(adminMallService.getOrderDetail(orderId));
    }

    @PostMapping("/order/ship")
    public Result<Boolean> shipOrder(@Valid @RequestBody OrderShipDTO dto) {
        return Result.success(adminMallService.shipOrder(dto));
    }

    @PostMapping("/order/status")
    public Result<Boolean> updateOrderStatus(@Valid @RequestBody OrderStatusUpdateDTO dto) {
        return Result.success(adminMallService.updateOrderStatus(dto));
    }

    @GetMapping("/order/logistics")
    public Result<List<LogisticsTraceVO>> getOrderLogistics(@RequestParam String orderId) {
        return Result.success(miniMallService.getLogistics(orderId));
    }

    @GetMapping("/user/list")
    public Result<PageResultVO<MallUserVO>> listUser(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listUser(
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @GetMapping("/user/address/list")
    public Result<PageResultVO<MallUserAddressVO>> listUserAddress(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listUserAddress(
                userId,
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @GetMapping("/user/coupon/list")
    public Result<PageResultVO<MallUserCouponVO>> listUserCoupon(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listUserCoupon(
                userId,
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @GetMapping("/coupon/list")
    public Result<PageResultVO<MallCouponVO>> listCoupon(
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(adminMallService.listCoupon(
                pageNum == null ? DEFAULT_PAGE_NUM : pageNum,
                pageSize == null ? DEFAULT_PAGE_SIZE : pageSize));
    }

    @PostMapping("/coupon/create")
    public Result<MallCouponVO> createCoupon(@Valid @RequestBody CouponCreateDTO dto) {
        return Result.success(adminMallService.createCoupon(dto));
    }

    @GetMapping("/banner/list")
    public Result<List<MallBannerVO>> listBanner() {
        return Result.success(adminMallService.listBanner());
    }

    @PostMapping("/banner/create")
    public Result<MallBannerVO> createBanner(@Valid @RequestBody BannerCreateDTO dto) {
        return Result.success(adminMallService.createBanner(dto));
    }

    @PostMapping("/banner/update")
    public Result<MallBannerVO> updateBanner(@Valid @RequestBody BannerUpdateDTO dto) {
        return Result.success(adminMallService.updateBanner(dto));
    }

    @PostMapping("/banner/delete")
    public Result<Boolean> deleteBanner(@Valid @RequestBody BannerDeleteDTO dto) {
        return Result.success(adminMallService.deleteBanner(dto));
    }

    @PostMapping("/banner/sort")
    public Result<Boolean> updateBannerSort(@Valid @RequestBody BannerSortUpdateDTO dto) {
        return Result.success(adminMallService.updateBannerSort(dto));
    }

    @GetMapping("/home/deco")
    public Result<HomeDecoVO> getHomeDeco() {
        return Result.success(adminMallService.getHomeDeco());
    }

    @PostMapping("/home/deco")
    public Result<HomeDecoVO> updateHomeDeco(@Valid @RequestBody HomeDecoUpdateDTO dto) {
        return Result.success(adminMallService.updateHomeDeco(dto));
    }

    @GetMapping("/system/setting")
    public Result<MallSystemSettingVO> getSystemSetting() {
        return Result.success(adminMallService.getSystemSetting());
    }

    @PostMapping("/system/setting")
    public Result<MallSystemSettingVO> updateSystemSetting(@Valid @RequestBody SystemSettingUpdateDTO dto) {
        return Result.success(adminMallService.updateSystemSetting(dto));
    }
}
