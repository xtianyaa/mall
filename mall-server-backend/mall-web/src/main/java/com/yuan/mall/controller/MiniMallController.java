package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import com.yuan.mall.dto.MiniAddressCreateDTO;
import com.yuan.mall.dto.MiniAddressDeleteDTO;
import com.yuan.mall.dto.MiniAddressUpdateDTO;
import com.yuan.mall.dto.MiniLoginDTO;
import com.yuan.mall.dto.MiniCartAddDTO;
import com.yuan.mall.dto.MiniCartRemoveDTO;
import com.yuan.mall.dto.MiniCartUpdateDTO;
import com.yuan.mall.dto.MiniOrderCancelDTO;
import com.yuan.mall.dto.MiniOrderCreateDTO;
import com.yuan.mall.dto.MiniOrderCreateFromCartDTO;
import com.yuan.mall.dto.MiniOrderPayDTO;
import com.yuan.mall.dto.MiniPayNotifyDTO;
import com.yuan.mall.dto.MiniPayPrepayDTO;
import com.yuan.mall.service.MiniMallService;
import com.yuan.mall.vo.MiniAddressVO;
import com.yuan.mall.vo.MiniBannerVO;
import com.yuan.mall.vo.HomeDecoVO;
import com.yuan.mall.vo.MiniCartItemVO;
import com.yuan.mall.vo.MiniCategoryVO;
import com.yuan.mall.vo.MiniCouponVO;
import com.yuan.mall.vo.MiniGoodsVO;
import com.yuan.mall.vo.LogisticsTraceVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.MiniPrepayVO;
import com.yuan.mall.vo.MiniSystemConfigVO;
import com.yuan.mall.vo.MiniUserProfileVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 小程序商城控制器
 */
@Validated
@RestController
@RequestMapping("/mall/mini")
public class MiniMallController {

    private final MiniMallService miniMallService;

    public MiniMallController(MiniMallService miniMallService) {
        this.miniMallService = miniMallService;
    }

    @GetMapping("/banner/list")
    public Result<List<MiniBannerVO>> listBanner() {
        return Result.success(miniMallService.listBanner());
    }

    @GetMapping("/home/deco")
    public Result<HomeDecoVO> getHomeDeco() {
        return Result.success(miniMallService.getHomeDeco());
    }

    @GetMapping("/system/config")
    public Result<MiniSystemConfigVO> getSystemConfig() {
        return Result.success(miniMallService.getSystemConfig());
    }

    @GetMapping("/category/list")
    public Result<List<MiniCategoryVO>> listCategory() {
        return Result.success(miniMallService.listCategory());
    }

    @GetMapping("/goods/list")
    public Result<List<MiniGoodsVO>> listGoods(@RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) String keyword) {
        return Result.success(miniMallService.listGoods(categoryId, keyword));
    }

    @GetMapping("/goods/detail")
    public Result<MiniGoodsVO> getGoodsDetail(@RequestParam Long id) {
        return Result.success(miniMallService.getGoodsDetail(id));
    }

    @PostMapping("/login")
    public Result<MiniUserProfileVO> login(@Valid @RequestBody MiniLoginDTO dto) {
        return Result.success(miniMallService.login(dto));
    }

    @GetMapping("/user/profile")
    public Result<MiniUserProfileVO> getUserProfile(@RequestParam Long userId) {
        return Result.success(miniMallService.getUserProfile(userId));
    }

    @GetMapping("/address/list")
    public Result<List<MiniAddressVO>> listAddress(@RequestParam Long userId) {
        return Result.success(miniMallService.listAddress(userId));
    }

    @PostMapping("/address/create")
    public Result<MiniAddressVO> createAddress(@Valid @RequestBody MiniAddressCreateDTO dto) {
        return Result.success(miniMallService.createAddress(dto));
    }

    @PostMapping("/address/update")
    public Result<MiniAddressVO> updateAddress(@Valid @RequestBody MiniAddressUpdateDTO dto) {
        return Result.success(miniMallService.updateAddress(dto));
    }

    @PostMapping("/address/delete")
    public Result<Boolean> deleteAddress(@Valid @RequestBody MiniAddressDeleteDTO dto) {
        return Result.success(miniMallService.deleteAddress(dto.getUserId(), dto.getAddressId()));
    }

    @GetMapping("/coupon/list")
    public Result<List<MiniCouponVO>> listCoupon(@RequestParam Long userId) {
        return Result.success(miniMallService.listCoupon(userId));
    }

    @GetMapping("/cart/list")
    public Result<List<MiniCartItemVO>> listCart(@RequestParam Long userId) {
        return Result.success(miniMallService.listCart(userId));
    }

    @PostMapping("/cart/add")
    public Result<List<MiniCartItemVO>> addCart(@Valid @RequestBody MiniCartAddDTO dto) {
        return Result.success(miniMallService.addCart(dto));
    }

    @PostMapping("/cart/update")
    public Result<List<MiniCartItemVO>> updateCart(@Valid @RequestBody MiniCartUpdateDTO dto) {
        return Result.success(miniMallService.updateCart(dto));
    }

    @PostMapping("/cart/remove")
    public Result<List<MiniCartItemVO>> removeCart(@Valid @RequestBody MiniCartRemoveDTO dto) {
        return Result.success(miniMallService.removeCart(dto));
    }

    @GetMapping("/order/list")
    public Result<List<MiniOrderVO>> listOrder(@RequestParam Long userId) {
        return Result.success(miniMallService.listOrder(userId));
    }

    @GetMapping("/order/detail")
    public Result<MiniOrderVO> getOrderDetail(@RequestParam String orderId) {
        return Result.success(miniMallService.getOrderDetail(orderId));
    }

    @GetMapping("/order/logistics")
    public Result<List<LogisticsTraceVO>> getLogistics(@RequestParam String orderId) {
        return Result.success(miniMallService.getLogistics(orderId));
    }

    @PostMapping("/order/create")
    public Result<MiniOrderVO> createOrder(@Valid @RequestBody MiniOrderCreateDTO dto) {
        return Result.success(miniMallService.createOrder(dto));
    }

    @PostMapping("/order/createFromCart")
    public Result<MiniOrderVO> createOrderFromCart(@Valid @RequestBody MiniOrderCreateFromCartDTO dto) {
        return Result.success(miniMallService.createOrderFromCart(dto));
    }

    @PostMapping("/order/pay/success")
    public Result<MiniOrderVO> paySuccess(@Valid @RequestBody MiniOrderPayDTO dto) {
        return Result.success(miniMallService.payOrderSuccess(dto));
    }

    @PostMapping("/order/pay/fail")
    public Result<Boolean> payFail(@Valid @RequestBody MiniOrderCancelDTO dto) {
        return Result.success(miniMallService.cancelOrder(dto, "支付失败"));
    }

    @PostMapping("/order/cancel")
    public Result<Boolean> cancelOrder(@Valid @RequestBody MiniOrderCancelDTO dto) {
        return Result.success(miniMallService.cancelOrder(dto, "用户取消"));
    }

    @PostMapping("/pay/prepay")
    public Result<MiniPrepayVO> prepay(@Valid @RequestBody MiniPayPrepayDTO dto) {
        return Result.success(miniMallService.prepay(dto));
    }

    @PostMapping("/pay/notify")
    public Result<Boolean> payNotify(@Valid @RequestBody MiniPayNotifyDTO dto) {
        return Result.success(miniMallService.payNotify(dto));
    }
}
