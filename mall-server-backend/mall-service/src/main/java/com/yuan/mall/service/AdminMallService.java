package com.yuan.mall.service;

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
import com.yuan.mall.dto.BannerSortUpdateDTO;
import com.yuan.mall.dto.BannerCreateDTO;
import com.yuan.mall.dto.BannerUpdateDTO;
import com.yuan.mall.dto.BannerDeleteDTO;
import com.yuan.mall.dto.OrderStatusUpdateDTO;
import com.yuan.mall.dto.SystemSettingUpdateDTO;
import com.yuan.mall.vo.MallBannerVO;
import com.yuan.mall.vo.MallCategoryVO;
import com.yuan.mall.vo.MallCouponVO;
import com.yuan.mall.vo.MallDashboardVO;
import com.yuan.mall.vo.MallGoodsVO;
import com.yuan.mall.vo.MallOrderVO;
import com.yuan.mall.vo.MallSystemSettingVO;
import com.yuan.mall.vo.MallUserAddressVO;
import com.yuan.mall.vo.MallUserCouponVO;
import com.yuan.mall.vo.MallUserVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.PageResultVO;

import java.util.List;

/**
 * 管理后台商城服务
 */
public interface AdminMallService {

    /**
     * 查询工作台数据
     *
     * @return 工作台数据
     */
    MallDashboardVO dashboard();

    /**
     * 分页查询分类列表
     *
     * @param nameKeyword 分类名称关键词（可选）
     * @param pageNum     页码，从 1 开始
     * @param pageSize    每页条数
     * @return 分页结果
     */
    PageResultVO<MallCategoryVO> listCategory(String nameKeyword, int pageNum, int pageSize);

    /**
     * 新增分类
     *
     * @param dto 分类创建参数
     * @return 分类数据
     */
    MallCategoryVO createCategory(CategoryCreateDTO dto);

    /**
     * 更新分类状态
     *
     * @param dto 分类状态参数
     * @return 是否成功
     */
    Boolean updateCategoryStatus(CategoryStatusUpdateDTO dto);

    /**
     * 更新分类（含名称、图标、排序、状态）
     *
     * @param dto 分类更新参数
     * @return 更新后的分类
     */
    MallCategoryVO updateCategory(CategoryUpdateDTO dto);

    /**
     * 删除分类（分类下无商品时允许删除）
     */
    Boolean deleteCategory(CategoryDeleteDTO dto);

    /**
     * 批量删除分类
     */
    Boolean batchDeleteCategory(CategoryBatchDeleteDTO dto);

    /**
     * 分页查询商品列表（支持关键词、分类筛选）
     *
     * @param keyword    关键词，匹配商品名称、卖点（可选）
     * @param categoryId 分类ID（可选）
     * @param pageNum    页码，从 1 开始
     * @param pageSize   每页条数
     * @return 分页结果
     */
    PageResultVO<MallGoodsVO> listGoods(String keyword, Long categoryId, int pageNum, int pageSize);

    /**
     * 新增商品
     *
     * @param dto 商品创建参数
     * @return 商品数据
     */
    MallGoodsVO createGoods(GoodsCreateDTO dto);

    /**
     * 更新商品
     *
     * @param dto 商品更新参数
     * @return 商品数据
     */
    MallGoodsVO updateGoods(GoodsUpdateDTO dto);

    /**
     * 更新商品状态
     *
     * @param dto 商品状态参数
     * @return 是否成功
     */
    Boolean updateGoodsStatus(GoodsStatusUpdateDTO dto);

    /**
     * 删除商品（含 SKU、购物车中该商品）
     */
    Boolean deleteGoods(GoodsDeleteDTO dto);

    /**
     * 批量删除商品
     */
    Boolean batchDeleteGoods(GoodsBatchDeleteDTO dto);

    /**
     * 分页查询订单列表
     *
     * @param status  订单状态筛选（可选）
     * @param userId  用户ID筛选（可选，用于用户详情中的订单）
     * @param pageNum 页码，从 1 开始
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResultVO<MallOrderVO> listOrder(String status, Long userId, int pageNum, int pageSize);

    /**
     * 查询订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    MiniOrderVO getOrderDetail(String orderId);

    /**
     * 订单状态更新（关闭待支付/取消/完成）
     */
    Boolean updateOrderStatus(OrderStatusUpdateDTO dto);

    /**
     * 发货
     *
     * @param dto 发货参数
     * @return 处理结果
     */
    Boolean shipOrder(OrderShipDTO dto);

    /**
     * 分页查询用户列表
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResultVO<MallUserVO> listUser(int pageNum, int pageSize);

    /**
     * 查询用户地址列表
     *
     * @return 用户地址列表
     */
    PageResultVO<MallUserAddressVO> listUserAddress(Long userId, int pageNum, int pageSize);

    /**
     * 查询用户优惠券列表
     *
     * @return 用户优惠券列表
     */
    PageResultVO<MallUserCouponVO> listUserCoupon(Long userId, int pageNum, int pageSize);

    /**
     * 查询优惠券列表
     *
     * @return 优惠券列表
     */
    PageResultVO<MallCouponVO> listCoupon(int pageNum, int pageSize);

    /**
     * 新增优惠券
     *
     * @param dto 优惠券参数
     * @return 优惠券数据
     */
    MallCouponVO createCoupon(CouponCreateDTO dto);

    /**
     * 查询首页 Banner 列表
     *
     * @return Banner 列表
     */
    List<MallBannerVO> listBanner();

    MallBannerVO createBanner(BannerCreateDTO dto);

    MallBannerVO updateBanner(BannerUpdateDTO dto);

    Boolean deleteBanner(BannerDeleteDTO dto);

    /**
     * 更新 Banner 排序
     *
     * @param dto 排序参数
     * @return 是否成功
     */
    Boolean updateBannerSort(BannerSortUpdateDTO dto);

    /**
     * 查询首页首屏文案配置
     *
     * @return 首屏文案
     */
    com.yuan.mall.vo.HomeDecoVO getHomeDeco();

    /**
     * 更新首页首屏文案配置
     *
     * @param dto 更新参数
     * @return 更新后的配置
     */
    com.yuan.mall.vo.HomeDecoVO updateHomeDeco(com.yuan.mall.dto.HomeDecoUpdateDTO dto);

    /**
     * 查询系统设置
     *
     * @return 系统设置
     */
    MallSystemSettingVO getSystemSetting();

    /**
     * 更新系统设置
     *
     * @param dto 系统设置参数
     * @return 系统设置
     */
    MallSystemSettingVO updateSystemSetting(SystemSettingUpdateDTO dto);
}
