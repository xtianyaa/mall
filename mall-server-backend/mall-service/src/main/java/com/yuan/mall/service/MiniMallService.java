package com.yuan.mall.service;

import com.yuan.mall.dto.MiniAddressCreateDTO;
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
import com.yuan.mall.vo.MiniAddressVO;
import com.yuan.mall.vo.MiniBannerVO;
import com.yuan.mall.vo.MiniCartItemVO;
import com.yuan.mall.vo.MiniCategoryVO;
import com.yuan.mall.vo.MiniCouponVO;
import com.yuan.mall.vo.MiniGoodsVO;
import com.yuan.mall.vo.LogisticsTraceVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.MiniUserProfileVO;
import com.yuan.mall.vo.MiniPrepayVO;
import com.yuan.mall.vo.MiniSystemConfigVO;
import com.yuan.mall.vo.HomeDecoVO;

import java.util.List;

/**
 * 小程序商城服务
 */
public interface MiniMallService {

    List<MiniBannerVO> listBanner();

    HomeDecoVO getHomeDeco();

    /**
     * 小程序端获取系统配置（如商城名称，用于导航栏、欢迎语等）
     */
    MiniSystemConfigVO getSystemConfig();

    List<MiniCategoryVO> listCategory();

    List<MiniGoodsVO> listGoods(Long categoryId, String keyword);

    MiniGoodsVO getGoodsDetail(Long goodsId);

    MiniUserProfileVO login(MiniLoginDTO dto);

    MiniUserProfileVO getUserProfile(Long userId);

    List<MiniAddressVO> listAddress(Long userId);

    MiniAddressVO createAddress(MiniAddressCreateDTO dto);

    MiniAddressVO updateAddress(MiniAddressUpdateDTO dto);

    Boolean deleteAddress(Long userId, Long addressId);

    List<MiniCouponVO> listCoupon(Long userId);

    List<MiniOrderVO> listOrder(Long userId);

    MiniOrderVO getOrderDetail(String orderId);

    /**
     * 查询订单物流轨迹（已发货订单可查，未发货返回空列表）
     */
    List<LogisticsTraceVO> getLogistics(String orderId);

    MiniOrderVO createOrder(MiniOrderCreateDTO dto);

    MiniOrderVO createOrderFromCart(MiniOrderCreateFromCartDTO dto);

    List<MiniCartItemVO> listCart(Long userId);

    List<MiniCartItemVO> addCart(MiniCartAddDTO dto);

    List<MiniCartItemVO> updateCart(MiniCartUpdateDTO dto);

    List<MiniCartItemVO> removeCart(MiniCartRemoveDTO dto);

    /**
     * 模拟支付成功：确认扣减库存、结算订单
     */
    MiniOrderVO payOrderSuccess(MiniOrderPayDTO dto);

    /**
     * 支付失败/用户取消：取消订单并释放锁定资源
     */
    Boolean cancelOrder(MiniOrderCancelDTO dto, String reason);

    /**
     * 关闭超时未支付订单（由定时任务触发）
     */
    Integer closeExpiredPendingOrders();

    /**
     * 支付预下单（占位：后续对接真实支付网关）
     */
    MiniPrepayVO prepay(MiniPayPrepayDTO dto);

    /**
     * 支付回调（占位：后续对接真实支付网关验签/解析）
     */
    Boolean payNotify(MiniPayNotifyDTO dto);
}
