package com.yuan.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuan.mall.dto.MiniAddressCreateDTO;
import com.yuan.mall.dto.MiniAddressUpdateDTO;
import com.yuan.mall.dto.MiniLoginDTO;
import com.yuan.mall.dto.MiniOrderCancelDTO;
import com.yuan.mall.dto.MiniOrderCreateDTO;
import com.yuan.mall.dto.MiniOrderCreateFromCartDTO;
import com.yuan.mall.dto.MiniOrderItemDTO;
import com.yuan.mall.dto.MiniOrderPayDTO;
import com.yuan.mall.dto.MiniPayNotifyDTO;
import com.yuan.mall.dto.MiniPayPrepayDTO;
import com.yuan.mall.entity.MallBannerEntity;
import com.yuan.mall.entity.MallHomeDecoEntity;
import com.yuan.mall.entity.MallCartEntity;
import com.yuan.mall.entity.MallCategoryEntity;
import com.yuan.mall.entity.MallCouponEntity;
import com.yuan.mall.entity.MallGoodsEntity;
import com.yuan.mall.entity.MallGoodsVariantEntity;
import com.yuan.mall.entity.MallAfterSaleItemEntity;
import com.yuan.mall.entity.MallAfterSaleOrderEntity;
import com.yuan.mall.entity.MallOrderEntity;
import com.yuan.mall.entity.MallOrderItemEntity;
import com.yuan.mall.entity.MallPayLogEntity;
import com.yuan.mall.entity.MallSystemSettingEntity;
import com.yuan.mall.entity.MallUserAddressEntity;
import com.yuan.mall.entity.MallUserCouponEntity;
import com.yuan.mall.entity.MallUserEntity;
import com.yuan.mall.mapper.MallBannerMapper;
import com.yuan.mall.mapper.MallHomeDecoMapper;
import com.yuan.mall.mapper.MallCartMapper;
import com.yuan.mall.mapper.MallCategoryMapper;
import com.yuan.mall.mapper.MallCouponMapper;
import com.yuan.mall.mapper.MallGoodsMapper;
import com.yuan.mall.mapper.MallGoodsVariantMapper;
import com.yuan.mall.mapper.MallAfterSaleItemMapper;
import com.yuan.mall.mapper.MallAfterSaleOrderMapper;
import com.yuan.mall.mapper.MallOrderItemMapper;
import com.yuan.mall.mapper.MallOrderMapper;
import com.yuan.mall.mapper.MallPayLogMapper;
import com.yuan.mall.mapper.MallSystemSettingMapper;
import com.yuan.mall.mapper.MallUserAddressMapper;
import com.yuan.mall.mapper.MallUserCouponMapper;
import com.yuan.mall.mapper.MallUserMapper;
import com.yuan.mall.service.MiniMallService;
import com.yuan.mall.util.OrderNoGenerator;
import com.yuan.mall.wechat.WeChatMiniAuthClient;
import com.yuan.mall.vo.MiniAddressVO;
import com.yuan.mall.vo.MiniBannerVO;
import com.yuan.mall.vo.HomeDecoVO;
import com.yuan.mall.vo.MiniCartItemVO;
import com.yuan.mall.vo.MiniCategoryVO;
import com.yuan.mall.vo.MiniCouponVO;
import com.yuan.mall.vo.MiniGoodsVO;
import com.yuan.mall.vo.MiniGoodsDetailBlockVO;
import com.yuan.mall.vo.MiniGoodsVariantVO;
import com.yuan.mall.vo.LogisticsTraceVO;
import com.yuan.mall.vo.MiniOrderItemVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.MiniUserProfileVO;
import com.yuan.mall.vo.MiniPrepayVO;
import com.yuan.mall.vo.MiniSystemConfigVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 小程序商城服务实现
 */
@Service
public class MiniMallServiceImpl implements MiniMallService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Map<String, String> CATEGORY_ICON_MAP = Map.of(
            "应季鲜果", "🍊",
            "轻食乳品", "🥛",
            "休闲零食", "🍪",
            "精选礼盒", "🎁"
    );
    private static final Pattern IMAGE_EXT_RE = Pattern.compile("\\.(png|jpe?g|gif|webp|bmp|svg)(\\?.*)?$", Pattern.CASE_INSENSITIVE);

    private final MallCategoryMapper mallCategoryMapper;
    private final MallBannerMapper mallBannerMapper;
    private final MallHomeDecoMapper mallHomeDecoMapper;
    private final MallGoodsMapper mallGoodsMapper;
    private final MallGoodsVariantMapper mallGoodsVariantMapper;
    private final MallCartMapper mallCartMapper;
    private final MallUserMapper mallUserMapper;
    private final MallUserAddressMapper mallUserAddressMapper;
    private final MallCouponMapper mallCouponMapper;
    private final MallUserCouponMapper mallUserCouponMapper;
    private final MallOrderMapper mallOrderMapper;
    private final MallOrderItemMapper mallOrderItemMapper;
    private final MallAfterSaleItemMapper mallAfterSaleItemMapper;
    private final MallAfterSaleOrderMapper mallAfterSaleOrderMapper;
    private final MallSystemSettingMapper mallSystemSettingMapper;
    private final MallPayLogMapper mallPayLogMapper;
    private final WeChatMiniAuthClient weChatMiniAuthClient;

    public MiniMallServiceImpl(MallCategoryMapper mallCategoryMapper,
                               MallBannerMapper mallBannerMapper,
                               MallHomeDecoMapper mallHomeDecoMapper,
                               MallGoodsMapper mallGoodsMapper,
                               MallGoodsVariantMapper mallGoodsVariantMapper,
                               MallCartMapper mallCartMapper,
                               MallUserMapper mallUserMapper,
                               MallUserAddressMapper mallUserAddressMapper,
                               MallCouponMapper mallCouponMapper,
                               MallUserCouponMapper mallUserCouponMapper,
                               MallOrderMapper mallOrderMapper,
                               MallOrderItemMapper mallOrderItemMapper,
                               MallAfterSaleItemMapper mallAfterSaleItemMapper,
                               MallAfterSaleOrderMapper mallAfterSaleOrderMapper,
                               MallSystemSettingMapper mallSystemSettingMapper,
                               MallPayLogMapper mallPayLogMapper,
                               WeChatMiniAuthClient weChatMiniAuthClient) {
        this.mallCategoryMapper = mallCategoryMapper;
        this.mallBannerMapper = mallBannerMapper;
        this.mallHomeDecoMapper = mallHomeDecoMapper;
        this.mallGoodsMapper = mallGoodsMapper;
        this.mallGoodsVariantMapper = mallGoodsVariantMapper;
        this.mallCartMapper = mallCartMapper;
        this.mallUserMapper = mallUserMapper;
        this.mallUserAddressMapper = mallUserAddressMapper;
        this.mallCouponMapper = mallCouponMapper;
        this.mallUserCouponMapper = mallUserCouponMapper;
        this.mallOrderMapper = mallOrderMapper;
        this.mallOrderItemMapper = mallOrderItemMapper;
        this.mallAfterSaleItemMapper = mallAfterSaleItemMapper;
        this.mallAfterSaleOrderMapper = mallAfterSaleOrderMapper;
        this.mallSystemSettingMapper = mallSystemSettingMapper;
        this.mallPayLogMapper = mallPayLogMapper;
        this.weChatMiniAuthClient = weChatMiniAuthClient;
    }

    @Override
    public List<MiniBannerVO> listBanner() {
        return mallBannerMapper.selectList(new LambdaQueryWrapper<MallBannerEntity>()
                        .eq(MallBannerEntity::getStatus, true)
                        .orderByAsc(MallBannerEntity::getSort, MallBannerEntity::getId))
                .stream()
                .map(item -> MiniBannerVO.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .linkType(item.getLinkType())
                        .imageUrl(item.getImageUrl())
                        .linkUrl(item.getLinkUrl())
                        .sort(item.getSort())
                        .build())
                .toList();
    }

    @Override
    public HomeDecoVO getHomeDeco() {
        MallHomeDecoEntity entity = mallHomeDecoMapper.selectById(1L);
        if (entity == null) {
            return HomeDecoVO.builder()
                    .searchPlaceholder("搜索蓝莓、牛奶、礼盒")
                    .stats(List.of(
                            new HomeDecoVO.StatItem("30 分钟", "同城闪送"),
                            new HomeDecoVO.StatItem("99 元", "满额包邮"),
                            new HomeDecoVO.StatItem("48 小时", "售后保障")))
                    .build();
        }
        return HomeDecoVO.builder()
                .searchPlaceholder(entity.getSearchPlaceholder() != null ? entity.getSearchPlaceholder() : "搜索蓝莓、牛奶、礼盒")
                .stats(List.of(
                        new HomeDecoVO.StatItem(
                                entity.getStat1Value() != null ? entity.getStat1Value() : "30 分钟",
                                entity.getStat1Label() != null ? entity.getStat1Label() : "同城闪送"),
                        new HomeDecoVO.StatItem(
                                entity.getStat2Value() != null ? entity.getStat2Value() : "99 元",
                                entity.getStat2Label() != null ? entity.getStat2Label() : "满额包邮"),
                        new HomeDecoVO.StatItem(
                                entity.getStat3Value() != null ? entity.getStat3Value() : "48 小时",
                                entity.getStat3Label() != null ? entity.getStat3Label() : "售后保障")))
                .build();
    }

    @Override
    public MiniSystemConfigVO getSystemConfig() {
        MallSystemSettingEntity entity = mallSystemSettingMapper.selectOne(
                new LambdaQueryWrapper<MallSystemSettingEntity>()
                        .orderByAsc(MallSystemSettingEntity::getId)
                        .last("limit 1"));
        String mallName = (entity != null && entity.getMallName() != null && !entity.getMallName().isBlank())
                ? entity.getMallName().trim()
                : "元选商城";
        return MiniSystemConfigVO.builder().mallName(mallName).build();
    }

    @Override
    public List<MiniCategoryVO> listCategory() {
        return mallCategoryMapper.selectList(new LambdaQueryWrapper<MallCategoryEntity>()
                        .eq(MallCategoryEntity::getStatus, true)
                        .orderByAsc(MallCategoryEntity::getSort, MallCategoryEntity::getId))
                .stream()
                .map(item -> MiniCategoryVO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .icon(item.getIcon() != null && !item.getIcon().isBlank()
                                ? item.getIcon()
                                : CATEGORY_ICON_MAP.getOrDefault(item.getName(), "🛍️"))
                        .build())
                .toList();
    }

    @Override
    public List<MiniGoodsVO> listGoods(Long categoryId, String keyword) {
        LambdaQueryWrapper<MallGoodsEntity> queryWrapper = new LambdaQueryWrapper<MallGoodsEntity>()
                .eq(MallGoodsEntity::getStatus, true)
                .orderByDesc(MallGoodsEntity::getMonthlySales, MallGoodsEntity::getId);

        if (categoryId != null) {
            queryWrapper.eq(MallGoodsEntity::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            queryWrapper.and(wrapper -> wrapper.like(MallGoodsEntity::getName, keyword)
                    .or()
                    .like(MallGoodsEntity::getCharacteristic, keyword));
        }

        List<MallGoodsEntity> goodsEntities = mallGoodsMapper.selectList(queryWrapper);
        if (goodsEntities.isEmpty()) {
            return List.of();
        }

        List<Long> goodsIds = goodsEntities.stream().map(MallGoodsEntity::getId).toList();
        List<MallGoodsVariantEntity> variantEntities = mallGoodsVariantMapper.selectList(
                new LambdaQueryWrapper<MallGoodsVariantEntity>()
                        .in(MallGoodsVariantEntity::getGoodsId, goodsIds)
                        .eq(MallGoodsVariantEntity::getStatus, true));

        Map<Long, List<MallGoodsVariantEntity>> goodsIdToVariants = variantEntities.stream()
                .collect(Collectors.groupingBy(MallGoodsVariantEntity::getGoodsId));

        return goodsEntities.stream()
                .map(goods -> buildMiniGoodsVO(goods, goodsIdToVariants.getOrDefault(goods.getId(), List.of()), false))
                .toList();
    }

    @Override
    public MiniGoodsVO getGoodsDetail(Long goodsId) {
        MallGoodsEntity goodsEntity = mallGoodsMapper.selectById(goodsId);
        if (goodsEntity == null) {
            throw new IllegalArgumentException("商品不存在");
        }
        List<MallGoodsVariantEntity> variants = mallGoodsVariantMapper.selectList(
                new LambdaQueryWrapper<MallGoodsVariantEntity>()
                        .eq(MallGoodsVariantEntity::getGoodsId, goodsId)
                        .eq(MallGoodsVariantEntity::getStatus, true));
        return buildMiniGoodsVO(goodsEntity, variants, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniUserProfileVO login(MiniLoginDTO dto) {
        String openid = weChatMiniAuthClient.code2Session(dto.getCode());

        MallUserEntity userEntity = mallUserMapper.selectOne(new LambdaQueryWrapper<MallUserEntity>()
                .eq(MallUserEntity::getOpenid, openid)
                .last("limit 1"));

        if (userEntity == null) {
            userEntity = new MallUserEntity();
            String nick = (dto.getNickName() != null && !dto.getNickName().isBlank())
                    ? dto.getNickName().trim() : "微信用户";
            userEntity.setNickName(nick);
            userEntity.setMobile("138" + String.valueOf(System.currentTimeMillis()).substring(4, 12));
            userEntity.setLevelName("普通会员");
            userEntity.setPoints(0);
            userEntity.setOrderCount(0);
            userEntity.setConsumeAmount(BigDecimal.ZERO);
            userEntity.setOpenid(openid);
            mallUserMapper.insert(userEntity);
            issueCouponsForUser(userEntity.getId());
        } else {
            if (userEntity.getOpenid() == null) {
                userEntity.setOpenid(openid);
            }
            if (dto.getNickName() != null && !dto.getNickName().isBlank()) {
                userEntity.setNickName(dto.getNickName().trim());
            }
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expireTime = LocalDateTime.now().plusDays(7);
        userEntity.setToken(token);
        userEntity.setTokenExpireTime(expireTime);
        mallUserMapper.updateById(userEntity);

        return getUserProfile(userEntity.getId());
    }

    @Override
    public MiniUserProfileVO getUserProfile(Long userId) {
        MallUserEntity userEntity = mallUserMapper.selectById(userId);
        if (userEntity == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        Long couponCount = mallUserCouponMapper.selectCount(new LambdaQueryWrapper<MallUserCouponEntity>()
                .eq(MallUserCouponEntity::getUserId, userId)
                .eq(MallUserCouponEntity::getStatus, "unused"));

        return MiniUserProfileVO.builder()
                .id(userEntity.getId())
                .nickName(userEntity.getNickName())
                .mobile(userEntity.getMobile())
                .levelName(userEntity.getLevelName())
                .points(userEntity.getPoints())
                .couponCount(couponCount.intValue())
                .token(userEntity.getToken())
                .tokenExpireTime(userEntity.getTokenExpireTime() == null ? "" : userEntity.getTokenExpireTime().format(DATE_TIME_FORMATTER))
                .build();
    }

    @Override
    public List<MiniAddressVO> listAddress(Long userId) {
        return mallUserAddressMapper.selectList(new LambdaQueryWrapper<MallUserAddressEntity>()
                        .eq(MallUserAddressEntity::getUserId, userId)
                        .orderByDesc(MallUserAddressEntity::getIsDefault, MallUserAddressEntity::getId))
                .stream()
                .map(item -> MiniAddressVO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .mobile(item.getMobile())
                        .province(item.getProvince())
                        .city(item.getCity())
                        .district(item.getDistrict())
                        .detail(item.getDetail())
                        .isDefault(item.getIsDefault())
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniAddressVO createAddress(MiniAddressCreateDTO dto) {
        if (dto.getIsDefault()) {
            mallUserAddressMapper.update(null, new LambdaUpdateWrapper<MallUserAddressEntity>()
                    .eq(MallUserAddressEntity::getUserId, dto.getUserId())
                    .set(MallUserAddressEntity::getIsDefault, false));
        }

        MallUserAddressEntity addressEntity = new MallUserAddressEntity();
        addressEntity.setUserId(dto.getUserId());
        addressEntity.setName(dto.getName());
        addressEntity.setMobile(dto.getMobile());
        addressEntity.setProvince(dto.getProvince());
        addressEntity.setCity(dto.getCity());
        addressEntity.setDistrict(dto.getDistrict());
        addressEntity.setDetail(dto.getDetail());
        addressEntity.setIsDefault(dto.getIsDefault());
        mallUserAddressMapper.insert(addressEntity);

        return MiniAddressVO.builder()
                .id(addressEntity.getId())
                .name(addressEntity.getName())
                .mobile(addressEntity.getMobile())
                .province(addressEntity.getProvince())
                .city(addressEntity.getCity())
                .district(addressEntity.getDistrict())
                .detail(addressEntity.getDetail())
                .isDefault(addressEntity.getIsDefault())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniAddressVO updateAddress(MiniAddressUpdateDTO dto) {
        MallUserAddressEntity addressEntity = mallUserAddressMapper.selectById(dto.getId());
        if (addressEntity == null || !addressEntity.getUserId().equals(dto.getUserId())) {
            throw new IllegalArgumentException("地址不存在");
        }

        if (dto.getIsDefault()) {
            mallUserAddressMapper.update(null, new LambdaUpdateWrapper<MallUserAddressEntity>()
                    .eq(MallUserAddressEntity::getUserId, dto.getUserId())
                    .set(MallUserAddressEntity::getIsDefault, false));
        }

        addressEntity.setName(dto.getName());
        addressEntity.setMobile(dto.getMobile());
        addressEntity.setProvince(dto.getProvince());
        addressEntity.setCity(dto.getCity());
        addressEntity.setDistrict(dto.getDistrict());
        addressEntity.setDetail(dto.getDetail());
        addressEntity.setIsDefault(dto.getIsDefault());
        mallUserAddressMapper.updateById(addressEntity);
        return buildMiniAddressVO(addressEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAddress(Long userId, Long addressId) {
        MallUserAddressEntity addressEntity = mallUserAddressMapper.selectById(addressId);
        if (addressEntity == null || !addressEntity.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在");
        }

        int deleted = mallUserAddressMapper.delete(new LambdaQueryWrapper<MallUserAddressEntity>()
                .eq(MallUserAddressEntity::getId, addressId)
                .eq(MallUserAddressEntity::getUserId, userId));

        if (Boolean.TRUE.equals(addressEntity.getIsDefault())) {
            MallUserAddressEntity nextDefault = mallUserAddressMapper.selectOne(new LambdaQueryWrapper<MallUserAddressEntity>()
                    .eq(MallUserAddressEntity::getUserId, userId)
                    .orderByAsc(MallUserAddressEntity::getId)
                    .last("limit 1"));
            if (nextDefault != null) {
                nextDefault.setIsDefault(true);
                mallUserAddressMapper.updateById(nextDefault);
            }
        }
        return deleted > 0;
    }

    @Override
    public List<MiniCouponVO> listCoupon(Long userId) {
        List<MallUserCouponEntity> userCouponList = mallUserCouponMapper.selectList(new LambdaQueryWrapper<MallUserCouponEntity>()
                .eq(MallUserCouponEntity::getUserId, userId)
                .orderByDesc(MallUserCouponEntity::getId));

        if (userCouponList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, MallCouponEntity> couponMap = mallCouponMapper.selectBatchIds(
                        userCouponList.stream().map(MallUserCouponEntity::getCouponId).toList())
                .stream()
                .collect(Collectors.toMap(MallCouponEntity::getId, item -> item));

        return userCouponList.stream()
                .map(item -> {
                    MallCouponEntity couponEntity = couponMap.get(item.getCouponId());
                    if (couponEntity == null) {
                        return null;
                    }
                    return MiniCouponVO.builder()
                            .id(couponEntity.getId())
                            .name(couponEntity.getName())
                            .amount(couponEntity.getAmount())
                            .minAmount(couponEntity.getMinAmount())
                            .startTime(couponEntity.getStartTime() == null ? null : couponEntity.getStartTime().format(DATE_TIME_FORMATTER))
                            .expireTime(item.getExpireTime().format(DATE_TIME_FORMATTER))
                            .status(item.getStatus())
                            .build();
                })
                .filter(item -> item != null)
                .toList();
    }

    @Override
    public List<MiniCartItemVO> listCart(Long userId) {
        List<MallCartEntity> cartEntities = mallCartMapper.selectList(new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getUserId, userId)
                .orderByDesc(MallCartEntity::getUpdateTime, MallCartEntity::getId));

        if (cartEntities.isEmpty()) {
            return List.of();
        }

        List<Long> goodsIds = cartEntities.stream().map(MallCartEntity::getGoodsId).distinct().toList();
        List<MallGoodsEntity> goodsEntityList = mallGoodsMapper.selectBatchIds(goodsIds);
        Map<Long, MallGoodsEntity> goodsIdToGoods = goodsEntityList.stream()
                .collect(Collectors.toMap(MallGoodsEntity::getId, item -> item));

        // 覆盖两种情况：
        // 1) 购物车行携带 variant_id（新数据）
        // 2) 购物车行 variant_id 为空（旧数据），此时用 goods_id 找默认规格替代展示
        List<MallGoodsVariantEntity> variantEntities = mallGoodsVariantMapper.selectList(
                new LambdaQueryWrapper<MallGoodsVariantEntity>()
                        .in(MallGoodsVariantEntity::getGoodsId, goodsIds)
                        .eq(MallGoodsVariantEntity::getStatus, true));
        Map<Long, MallGoodsVariantEntity> variantIdToVariant = variantEntities.stream()
                .collect(Collectors.toMap(MallGoodsVariantEntity::getId, item -> item, (a, b) -> a));

        Map<Long, List<MallGoodsVariantEntity>> goodsIdToVariants = variantEntities.stream()
                .collect(Collectors.groupingBy(MallGoodsVariantEntity::getGoodsId));

        Map<Long, MallGoodsVariantEntity> defaultVariantByGoodsId = goodsIdToVariants.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .filter(v -> v.getPrice() != null)
                                .min((a, b) -> a.getPrice().compareTo(b.getPrice()) != 0
                                        ? a.getPrice().compareTo(b.getPrice())
                                        : a.getId().compareTo(b.getId()))
                                .orElse(e.getValue().get(0))
                ));

        return cartEntities.stream()
                .map(item -> {
                    MallGoodsVariantEntity variant = item.getVariantId() != null
                            ? variantIdToVariant.get(item.getVariantId())
                            : defaultVariantByGoodsId.get(item.getGoodsId());

                    MallGoodsEntity goods = goodsIdToGoods.get(item.getGoodsId());
                    BigDecimal fallbackPrice = goods == null ? BigDecimal.ZERO : goods.getPrice();

                    return MiniCartItemVO.builder()
                            .goodsId(item.getGoodsId())
                            .variantId(variant == null ? null : variant.getId())
                            .variantLabel(variant == null ? "默认规格" : variant.buildLabel())
                            .variantPrice(variant == null ? fallbackPrice : variant.getPrice())
                            .quantity(item.getQuantity())
                            .checked(item.getChecked())
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MiniCartItemVO> addCart(com.yuan.mall.dto.MiniCartAddDTO dto) {
        MallGoodsEntity goodsEntity = mallGoodsMapper.selectById(dto.getGoodsId());
        if (goodsEntity == null || Boolean.FALSE.equals(goodsEntity.getStatus())) {
            throw new IllegalArgumentException("商品不存在或已下架");
        }

        // 规格SKU：可空（旧前端可能只传 goodsId）
        Long targetVariantId = dto.getVariantId();
        MallGoodsVariantEntity variantEntity = null;
        if (targetVariantId != null) {
            variantEntity = mallGoodsVariantMapper.selectById(targetVariantId);
            if (variantEntity == null || Boolean.FALSE.equals(variantEntity.getStatus())) {
                throw new IllegalArgumentException("规格SKU不存在或已下架");
            }
        } else {
            // 自动找默认规格：按 SKU 销售价最小
            variantEntity = mallGoodsVariantMapper.selectOne(new LambdaQueryWrapper<MallGoodsVariantEntity>()
                    .eq(MallGoodsVariantEntity::getGoodsId, dto.getGoodsId())
                    .eq(MallGoodsVariantEntity::getStatus, true)
                    .orderByAsc(MallGoodsVariantEntity::getPrice)
                    .last("limit 1"));
            targetVariantId = variantEntity == null ? null : variantEntity.getId();
        }

        MallCartEntity cartEntity;
        if (targetVariantId != null) {
            cartEntity = mallCartMapper.selectOne(new LambdaQueryWrapper<MallCartEntity>()
                    .eq(MallCartEntity::getUserId, dto.getUserId())
                    .eq(MallCartEntity::getGoodsId, dto.getGoodsId())
                    .eq(MallCartEntity::getVariantId, targetVariantId)
                    .last("limit 1"));
            if (cartEntity == null) {
                // 兼容旧购物车行：若存在 variant_id 为空的旧行，则把它升级为当前 variant_id 并累加数量
                MallCartEntity legacy = mallCartMapper.selectOne(new LambdaQueryWrapper<MallCartEntity>()
                        .eq(MallCartEntity::getUserId, dto.getUserId())
                        .eq(MallCartEntity::getGoodsId, dto.getGoodsId())
                        .isNull(MallCartEntity::getVariantId)
                        .last("limit 1"));
                if (legacy != null) {
                    legacy.setVariantId(targetVariantId);
                    legacy.setQuantity(legacy.getQuantity() + dto.getQuantity());
                    legacy.setChecked(true);
                    mallCartMapper.updateById(legacy);
                    return listCart(dto.getUserId());
                }
            }
        } else {
            cartEntity = mallCartMapper.selectOne(new LambdaQueryWrapper<MallCartEntity>()
                    .eq(MallCartEntity::getUserId, dto.getUserId())
                    .eq(MallCartEntity::getGoodsId, dto.getGoodsId())
                    .isNull(MallCartEntity::getVariantId)
                    .last("limit 1"));
        }

        if (cartEntity == null) {
            cartEntity = new MallCartEntity();
            cartEntity.setUserId(dto.getUserId());
            cartEntity.setGoodsId(dto.getGoodsId());
            cartEntity.setVariantId(targetVariantId);
            cartEntity.setQuantity(dto.getQuantity());
            cartEntity.setChecked(true);
            mallCartMapper.insert(cartEntity);
        } else {
            cartEntity.setQuantity(cartEntity.getQuantity() + dto.getQuantity());
            cartEntity.setChecked(true);
            mallCartMapper.updateById(cartEntity);
        }

        return listCart(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MiniCartItemVO> updateCart(com.yuan.mall.dto.MiniCartUpdateDTO dto) {
        LambdaQueryWrapper<MallCartEntity> wrapper = new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getUserId, dto.getUserId())
                .eq(MallCartEntity::getGoodsId, dto.getGoodsId())
                .last("limit 1");
        if (dto.getVariantId() != null) {
            wrapper.eq(MallCartEntity::getVariantId, dto.getVariantId());
        } else {
            wrapper.isNull(MallCartEntity::getVariantId);
        }

        MallCartEntity cartEntity = mallCartMapper.selectOne(wrapper);
        if (cartEntity == null) {
            throw new IllegalArgumentException("购物车商品不存在");
        }
        if (dto.getQuantity() != null) {
            cartEntity.setQuantity(dto.getQuantity());
        }
        if (dto.getChecked() != null) {
            cartEntity.setChecked(dto.getChecked());
        }
        mallCartMapper.updateById(cartEntity);
        return listCart(dto.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MiniCartItemVO> removeCart(com.yuan.mall.dto.MiniCartRemoveDTO dto) {
        LambdaQueryWrapper<MallCartEntity> wrapper = new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getUserId, dto.getUserId())
                .eq(MallCartEntity::getGoodsId, dto.getGoodsId());
        if (dto.getVariantId() != null) {
            wrapper.eq(MallCartEntity::getVariantId, dto.getVariantId());
        } else {
            wrapper.isNull(MallCartEntity::getVariantId);
        }
        mallCartMapper.delete(wrapper);
        return listCart(dto.getUserId());
    }

    @Override
    public List<MiniOrderVO> listOrder(Long userId) {
        return mallOrderMapper.selectList(new LambdaQueryWrapper<MallOrderEntity>()
                        .eq(MallOrderEntity::getUserId, userId)
                        .orderByDesc(MallOrderEntity::getId))
                .stream()
                .map(this::buildMiniOrderVO)
                .toList();
    }

    @Override
    public MiniOrderVO getOrderDetail(String orderId) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, orderId)
                .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        return buildMiniOrderVO(orderEntity);
    }

    @Override
    public List<LogisticsTraceVO> getLogistics(String orderId) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, orderId)
                .last("limit 1"));
        if (orderEntity == null || orderEntity.getExpressNo() == null || orderEntity.getExpressNo().isBlank()) {
            return Collections.emptyList();
        }
        LocalDateTime shipTime = orderEntity.getShipTime() != null ? orderEntity.getShipTime() : orderEntity.getUpdateTime();
        List<LogisticsTraceVO> list = new ArrayList<>();
        list.add(LogisticsTraceVO.builder().time(shipTime.format(DATE_TIME_FORMATTER)).desc("商家已发货").build());
        list.add(LogisticsTraceVO.builder().time(shipTime.plusHours(2).format(DATE_TIME_FORMATTER)).desc("【" + (orderEntity.getExpressCompany() != null ? orderEntity.getExpressCompany() : "快递") + "】已揽收").build());
        list.add(LogisticsTraceVO.builder().time(shipTime.plusHours(8).format(DATE_TIME_FORMATTER)).desc("快件已发往杭州转运中心").build());
        list.add(LogisticsTraceVO.builder().time(shipTime.plusDays(1).format(DATE_TIME_FORMATTER)).desc("快件到达杭州转运中心").build());
        list.add(LogisticsTraceVO.builder().time(shipTime.plusDays(1).plusHours(6).format(DATE_TIME_FORMATTER)).desc("快件正在派送中，请保持电话畅通").build());
        list.add(LogisticsTraceVO.builder().time(shipTime.plusDays(2).format(DATE_TIME_FORMATTER)).desc("已签收，签收人：本人").build());
        Collections.reverse(list);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniOrderVO createOrder(MiniOrderCreateDTO dto) {
        return createOrderInternal(dto.getUserId(), dto.getAddressId(), dto.getCouponId(), dto.getItemList(), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniOrderVO createOrderFromCart(MiniOrderCreateFromCartDTO dto) {
        List<MallCartEntity> checkedList = mallCartMapper.selectList(new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getUserId, dto.getUserId())
                .eq(MallCartEntity::getChecked, true));
        if (checkedList.isEmpty()) {
            throw new IllegalArgumentException("请先勾选购物车商品");
        }
        List<MiniOrderItemDTO> itemList = checkedList.stream().map(item -> {
            MiniOrderItemDTO dtoItem = new MiniOrderItemDTO();
            dtoItem.setGoodsId(item.getGoodsId());
            dtoItem.setVariantId(item.getVariantId());
            dtoItem.setName("-");
            dtoItem.setPrice(BigDecimal.ZERO);
            dtoItem.setQuantity(item.getQuantity());
            return dtoItem;
        }).toList();

        MiniOrderVO orderVO = createOrderInternal(dto.getUserId(), dto.getAddressId(), dto.getCouponId(), itemList, false);

        // 下单后清理已结算条目
        mallCartMapper.delete(new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getUserId, dto.getUserId())
                .eq(MallCartEntity::getChecked, true));
        return orderVO;
    }

    private MiniOrderVO createOrderInternal(Long userId,
                                           Long addressId,
                                           Long couponId,
                                           List<MiniOrderItemDTO> itemList,
                                           boolean allowClientItemPayload) {
        MallUserEntity userEntity = mallUserMapper.selectById(userId);
        MallUserAddressEntity addressEntity = mallUserAddressMapper.selectById(addressId);
        if (userEntity == null || addressEntity == null) {
            throw new IllegalArgumentException("用户或地址不存在");
        }
        if (!userId.equals(addressEntity.getUserId())) {
            throw new IllegalArgumentException("收货地址不属于当前用户");
        }
        if (itemList == null || itemList.isEmpty()) {
            throw new IllegalArgumentException("订单商品不能为空");
        }

        // 后端重算：只信 goodsId/variantId + quantity，名称/价格以 DB 为准
        Map<Long, Integer> variantQuantityMap = itemList.stream()
                .filter(i -> i.getVariantId() != null)
                .collect(Collectors.toMap(MiniOrderItemDTO::getVariantId, MiniOrderItemDTO::getQuantity, Integer::sum));
        Map<Long, Integer> goodsQuantityMap = itemList.stream()
                .filter(i -> i.getVariantId() == null)
                .collect(Collectors.toMap(MiniOrderItemDTO::getGoodsId, MiniOrderItemDTO::getQuantity, Integer::sum));

        List<Long> variantIds = variantQuantityMap.keySet().stream().toList();
        List<Long> goodsIds = goodsQuantityMap.keySet().stream().toList();

        List<MallGoodsVariantEntity> variantEntityList = variantIds.isEmpty() ? List.of() : mallGoodsVariantMapper.selectBatchIds(variantIds);
        Map<Long, MallGoodsVariantEntity> variantMap = variantEntityList.stream()
                .collect(Collectors.toMap(MallGoodsVariantEntity::getId, item -> item));

        List<MallGoodsEntity> goodsEntityList = goodsIds.isEmpty() ? List.of() : mallGoodsMapper.selectBatchIds(goodsIds);
        Map<Long, MallGoodsEntity> goodsMap = goodsEntityList.stream()
                .collect(Collectors.toMap(MallGoodsEntity::getId, item -> item));

        for (Long variantId : variantIds) {
            MallGoodsVariantEntity v = variantMap.get(variantId);
            if (v == null || Boolean.FALSE.equals(v.getStatus())) {
                throw new IllegalArgumentException("规格SKU不存在或已下架: " + variantId);
            }
        }
        for (Long goodsId : goodsIds) {
            MallGoodsEntity g = goodsMap.get(goodsId);
            if (g == null || Boolean.FALSE.equals(g.getStatus())) {
                throw new IllegalArgumentException("商品不存在或已下架: " + goodsId);
            }
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Long variantId : variantIds) {
            int qty = variantQuantityMap.getOrDefault(variantId, 0);
            if (qty <= 0) {
                throw new IllegalArgumentException("SKU 数量不合法");
            }
            MallGoodsVariantEntity v = variantMap.get(variantId);
            totalAmount = totalAmount.add(v.getPrice().multiply(BigDecimal.valueOf(qty)));
        }
        for (Long goodsId : goodsIds) {
            int qty = goodsQuantityMap.getOrDefault(goodsId, 0);
            if (qty <= 0) {
                throw new IllegalArgumentException("商品数量不合法");
            }
            MallGoodsEntity g = goodsMap.get(goodsId);
            totalAmount = totalAmount.add(g.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        // 优惠券折扣（后端计算）
        BigDecimal discountAmount = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();
        if (couponId != null) {
            MallCouponEntity couponEntity = mallCouponMapper.selectById(couponId);
            if (couponEntity == null || Boolean.FALSE.equals(couponEntity.getStatus())) {
                throw new IllegalArgumentException("优惠券不存在或已下线");
            }
            if (couponEntity.getStartTime() != null && now.isBefore(couponEntity.getStartTime())) {
                throw new IllegalArgumentException("优惠券未到可用时间");
            }
            if (couponEntity.getExpireTime() != null && couponEntity.getExpireTime().isBefore(now)) {
                throw new IllegalArgumentException("优惠券已过期");
            }
            if (totalAmount.compareTo(couponEntity.getMinAmount()) >= 0) {
                discountAmount = couponEntity.getAmount();
            }
        }
        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        int cancelMinutes = getCancelMinutes();
        LocalDateTime payDeadline = now.plusMinutes(cancelMinutes);

        // 1) 锁库存（原子更新，防超卖）
        for (Long variantId : variantIds) {
            int qty = variantQuantityMap.getOrDefault(variantId, 0);
            int updated = mallGoodsVariantMapper.update(null, new UpdateWrapper<MallGoodsVariantEntity>()
                    .eq("id", variantId)
                    .eq("status", 1)
                    .apply("stock - locked_stock >= {0}", qty)
                    .setSql("locked_stock = locked_stock + " + qty));
            if (updated <= 0) {
                throw new IllegalArgumentException("规格库存不足: " + variantMap.get(variantId).buildLabel());
            }
        }
        for (Long goodsId : goodsIds) {
            int qty = goodsQuantityMap.getOrDefault(goodsId, 0);
            int updated = mallGoodsMapper.update(null, new UpdateWrapper<MallGoodsEntity>()
                    .eq("id", goodsId)
                    .eq("status", 1)
                    .apply("stock - locked_stock >= {0}", qty)
                    .setSql("locked_stock = locked_stock + " + qty));
            if (updated <= 0) {
                throw new IllegalArgumentException("库存不足: " + goodsMap.get(goodsId).getName());
            }
        }

        MallOrderEntity orderEntity = new MallOrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setCouponId(couponId);
        orderEntity.setOrderNo(OrderNoGenerator.next());
        orderEntity.setUserName(userEntity.getNickName());
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setPayAmount(payAmount);
        orderEntity.setAddressText(buildAddressText(addressEntity));
        orderEntity.setStatus("待支付");
        orderEntity.setPayDeadline(payDeadline);
        mallOrderMapper.insert(orderEntity);

        // 2) 写订单明细（按 SKU/变体扣减）
        for (Long variantId : variantIds) {
            MallGoodsVariantEntity v = variantMap.get(variantId);
            int qty = variantQuantityMap.getOrDefault(variantId, 0);
            MallOrderItemEntity orderItemEntity = new MallOrderItemEntity();
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setGoodsId(v.getGoodsId());
            orderItemEntity.setVariantId(v.getId());
            orderItemEntity.setGoodsName(v.getGoodsName());
            orderItemEntity.setGoodsPrice(v.getPrice());
            orderItemEntity.setQuantity(qty);
            mallOrderItemMapper.insert(orderItemEntity);
        }
        for (Long goodsId : goodsIds) {
            MallGoodsEntity g = goodsMap.get(goodsId);
            int qty = goodsQuantityMap.getOrDefault(goodsId, 0);
            MallOrderItemEntity orderItemEntity = new MallOrderItemEntity();
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setGoodsId(goodsId);
            orderItemEntity.setVariantId(null);
            orderItemEntity.setGoodsName(g.getName());
            orderItemEntity.setGoodsPrice(g.getPrice());
            orderItemEntity.setQuantity(qty);
            mallOrderItemMapper.insert(orderItemEntity);
        }

        if (couponId != null) {
            int locked = mallUserCouponMapper.update(null, new LambdaUpdateWrapper<MallUserCouponEntity>()
                    .eq(MallUserCouponEntity::getUserId, userId)
                    .eq(MallUserCouponEntity::getCouponId, couponId)
                    .eq(MallUserCouponEntity::getStatus, "unused")
                    .gt(MallUserCouponEntity::getExpireTime, now)
                    .set(MallUserCouponEntity::getStatus, "locked"));
            if (locked <= 0) {
                throw new IllegalArgumentException("优惠券不可用");
            }
        }

        return getOrderDetail(orderEntity.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniOrderVO payOrderSuccess(MiniOrderPayDTO dto) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                .eq(MallOrderEntity::getUserId, dto.getUserId())
                .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!"待支付".equals(orderEntity.getStatus())) {
            return getOrderDetail(orderEntity.getOrderNo());
        }

        LocalDateTime now = LocalDateTime.now();
        if (orderEntity.getPayDeadline() != null && orderEntity.getPayDeadline().isBefore(now)) {
            cancelOrderInternal(orderEntity, "超时未支付");
            throw new IllegalArgumentException("订单已超时关闭");
        }

        // 先把订单原子置为“支付处理中”，避免并发取消/关单与回调打架
        int processing = mallOrderMapper.update(null, new UpdateWrapper<MallOrderEntity>()
                .eq("order_no", orderEntity.getOrderNo())
                .eq("user_id", dto.getUserId())
                .eq("status", "待支付")
                .set("status", "支付处理中"));
        if (processing <= 0) {
            return getOrderDetail(orderEntity.getOrderNo());
        }

        List<MallOrderItemEntity> orderItemList = mallOrderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItemEntity>()
                .eq(MallOrderItemEntity::getOrderId, orderEntity.getId()));

        // 1) 确认扣减库存（stock--, locked_stock--）并累计销量
        for (MallOrderItemEntity item : orderItemList) {
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            if (qty <= 0) {
                throw new IllegalArgumentException("订单明细数量不合法");
            }

            if (item.getVariantId() != null) {
                int updated = mallGoodsVariantMapper.update(null, new UpdateWrapper<MallGoodsVariantEntity>()
                        .eq("id", item.getVariantId())
                        .apply("locked_stock >= {0}", qty)
                        .apply("stock >= {0}", qty)
                        .setSql("stock = stock - " + qty + ", locked_stock = locked_stock - " + qty));
                if (updated <= 0) {
                    throw new IllegalArgumentException("SKU 库存确认失败，请稍后重试");
                }

                // 仅累计销量：月销口径仍统计到 mall_goods.monthly_sales
                mallGoodsMapper.update(null, new UpdateWrapper<MallGoodsEntity>()
                        .eq("id", item.getGoodsId())
                        .setSql("monthly_sales = monthly_sales + " + qty));
            } else {
                int updated = mallGoodsMapper.update(null, new UpdateWrapper<MallGoodsEntity>()
                        .eq("id", item.getGoodsId())
                        .apply("locked_stock >= {0}", qty)
                        .apply("stock >= {0}", qty)
                        .setSql("stock = stock - " + qty + ", locked_stock = locked_stock - " + qty + ", monthly_sales = monthly_sales + " + qty));
                if (updated <= 0) {
                    throw new IllegalArgumentException("库存确认失败，请稍后重试");
                }
            }
        }

        // 2) 订单状态原子变更：支付处理中 -> 待发货
        mallOrderMapper.update(null, new UpdateWrapper<MallOrderEntity>()
                .eq("order_no", orderEntity.getOrderNo())
                .eq("user_id", dto.getUserId())
                .eq("status", "支付处理中")
                .set("status", "待发货")
                .set("pay_time", now));

        // 3) 若订单使用了优惠券：locked -> used
        if (orderEntity.getCouponId() != null) {
            mallUserCouponMapper.update(null, new LambdaUpdateWrapper<MallUserCouponEntity>()
                    .eq(MallUserCouponEntity::getUserId, dto.getUserId())
                    .eq(MallUserCouponEntity::getCouponId, orderEntity.getCouponId())
                    .eq(MallUserCouponEntity::getStatus, "locked")
                    .set(MallUserCouponEntity::getStatus, "used"));
        }

        // 4) 累计用户数据（支付成功后才结算）
        MallUserEntity userEntity = mallUserMapper.selectById(dto.getUserId());
        if (userEntity != null) {
            userEntity.setPoints(userEntity.getPoints() + orderEntity.getPayAmount().intValue());
            userEntity.setOrderCount(userEntity.getOrderCount() + 1);
            userEntity.setConsumeAmount(userEntity.getConsumeAmount().add(orderEntity.getPayAmount()));
            mallUserMapper.updateById(userEntity);
        }

        return getOrderDetail(orderEntity.getOrderNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(MiniOrderCancelDTO dto, String reason) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                .eq(MallOrderEntity::getUserId, dto.getUserId())
                .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        return cancelOrderInternal(orderEntity, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer closeExpiredPendingOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<MallOrderEntity> expiredList = mallOrderMapper.selectList(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getStatus, "待支付")
                .isNotNull(MallOrderEntity::getPayDeadline)
                .le(MallOrderEntity::getPayDeadline, now)
                .orderByAsc(MallOrderEntity::getPayDeadline)
                .last("limit 100"));

        int closed = 0;
        for (MallOrderEntity orderEntity : expiredList) {
            if (cancelOrderInternal(orderEntity, "超时未支付")) {
                closed++;
            }
        }
        return closed;
    }

    private MiniGoodsVO buildMiniGoodsVO(MallGoodsEntity goodsEntity,
                                         List<MallGoodsVariantEntity> variantEntities,
                                         boolean isDetail) {
        List<MallGoodsVariantEntity> variants = variantEntities == null ? List.of() : variantEntities;

        List<String> fallbackSpecNames = List.of("规格");
        Map<String, String> fallbackSpecs = Map.of();

        // 兜底：若旧库尚未创建 mall_goods_variant，仍按 goods 级别返回一个"默认规格"
        if (variants.isEmpty()) {
            int lockedStock = goodsEntity.getLockedStock() == null ? 0 : goodsEntity.getLockedStock();
            int stock = goodsEntity.getStock() == null ? 0 : goodsEntity.getStock();
            int availableStock = Math.max(stock - lockedStock, 0);
            List<String> detailList = splitString(goodsEntity.getDetailList());
            return MiniGoodsVO.builder()
                    .id(goodsEntity.getId())
                    .categoryId(goodsEntity.getCategoryId())
                    .categoryName(goodsEntity.getCategoryName())
                    .name(goodsEntity.getName())
                    .characteristic(goodsEntity.getCharacteristic())
                    .price(goodsEntity.getPrice())
                    .originalPrice(goodsEntity.getOriginalPrice())
                    .unit(goodsEntity.getUnit())
                    .stock(availableStock)
                    .monthlySales(goodsEntity.getMonthlySales())
                    .tags(splitString(goodsEntity.getTags()))
                    .bannerList(splitString(goodsEntity.getBannerList()))
                    .detailList(detailList)
                    .detailRichList(buildDetailRichList(detailList))
                    .defaultVariantId(null)
                    .variants(List.of(MiniGoodsVariantVO.builder()
                            .id(null)
                            .specs(fallbackSpecs)
                            .specNames(fallbackSpecNames)
                            .price(goodsEntity.getPrice())
                            .originalPrice(goodsEntity.getOriginalPrice())
                            .unit(goodsEntity.getUnit())
                            .stock(availableStock)
                            .build()))
                    .specNames(fallbackSpecNames)
                    .build();
        }

        // 从第一个 variant 取维度名列表
        List<String> specNames = variants.get(0).getSpecNamesList();
        if (specNames == null || specNames.isEmpty()) {
            specNames = fallbackSpecNames;
        }

        // 选择默认规格：按 SKU 销售价最小
        MallGoodsVariantEntity defaultVariant = variants.stream()
                .filter(v -> v.getPrice() != null)
                .min((a, b) -> a.getPrice().compareTo(b.getPrice()) != 0
                        ? a.getPrice().compareTo(b.getPrice())
                        : a.getId().compareTo(b.getId()))
                .orElse(variants.get(0));

        int defaultAvailableStock = defaultVariant.getAvailableStock();

        int totalAvailableStock = variants.stream()
                .map(MallGoodsVariantEntity::getAvailableStock)
                .reduce(0, Integer::sum);

        List<MiniGoodsVariantVO> variantVOs = variants.stream()
                .map(v -> MiniGoodsVariantVO.builder()
                        .id(v.getId())
                        .specs(v.getSpecsMap())
                        .specNames(v.getSpecNamesList())
                        .price(v.getPrice())
                        .originalPrice(v.getOriginalPrice())
                        .unit(v.getUnit())
                        .stock(v.getAvailableStock())
                        .build())
                .toList();

        List<String> detailList = splitString(goodsEntity.getDetailList());
        return MiniGoodsVO.builder()
                .id(goodsEntity.getId())
                .categoryId(goodsEntity.getCategoryId())
                .categoryName(goodsEntity.getCategoryName())
                .name(goodsEntity.getName())
                .characteristic(goodsEntity.getCharacteristic())
                .price(defaultVariant.getPrice())
                .originalPrice(defaultVariant.getOriginalPrice())
                .unit(goodsEntity.getUnit())
                .stock(isDetail ? defaultAvailableStock : totalAvailableStock)
                .monthlySales(goodsEntity.getMonthlySales())
                .tags(splitString(goodsEntity.getTags()))
                .bannerList(splitString(goodsEntity.getBannerList()))
                .detailList(detailList)
                .detailRichList(buildDetailRichList(detailList))
                .defaultVariantId(defaultVariant.getId())
                .variants(variantVOs)
                .specNames(specNames)
                .build();
    }

    private List<MiniGoodsDetailBlockVO> buildDetailRichList(List<String> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return Collections.emptyList();
        }
        return detailList.stream()
                .map(item -> {
                    String value = item == null ? "" : item.trim();
                    if (value.isBlank()) {
                        return null;
                    }
                    return MiniGoodsDetailBlockVO.builder()
                            .type(isImageLike(value) ? "image" : "text")
                            .value(value)
                            .build();
                })
                .filter(item -> item != null)
                .toList();
    }

    private boolean isImageLike(String value) {
        if (value == null) {
            return false;
        }
        String s = value.trim();
        if (s.isBlank()) {
            return false;
        }
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return true;
        }
        if (s.startsWith("/") || s.startsWith("upload") || s.contains("/upload")) {
            return true;
        }
        if (IMAGE_EXT_RE.matcher(s).find()) {
            return true;
        }
        // 兼容“仅文件名/UUID”形式的上传图
        if (s.length() >= 10 && s.matches("^[a-zA-Z0-9_.-]+$")) {
            return true;
        }
        return false;
    }

    private MiniAddressVO buildMiniAddressVO(MallUserAddressEntity addressEntity) {
        return MiniAddressVO.builder()
                .id(addressEntity.getId())
                .name(addressEntity.getName())
                .mobile(addressEntity.getMobile())
                .province(addressEntity.getProvince())
                .city(addressEntity.getCity())
                .district(addressEntity.getDistrict())
                .detail(addressEntity.getDetail())
                .isDefault(addressEntity.getIsDefault())
                .build();
    }

    private MiniOrderVO buildMiniOrderVO(MallOrderEntity orderEntity) {
        List<MallOrderItemEntity> orderItems = mallOrderItemMapper.selectList(
                new LambdaQueryWrapper<MallOrderItemEntity>()
                        .eq(MallOrderItemEntity::getOrderId, orderEntity.getId())
                        .orderByAsc(MallOrderItemEntity::getId));

        // 规格 SKU 标签：用于订单详情展示
        List<Long> variantIds = orderItems.stream()
                .map(MallOrderItemEntity::getVariantId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        Map<Long, String> variantIdToLabel = variantIds.isEmpty()
                ? Map.of()
                : mallGoodsVariantMapper.selectBatchIds(variantIds).stream()
                .collect(Collectors.toMap(MallGoodsVariantEntity::getId, MallGoodsVariantEntity::buildLabel, (a, b) -> a));

        List<Long> orderItemIds = orderItems.stream().map(MallOrderItemEntity::getId).toList();
        Map<Long, String> orderItemIdToAfterSaleStatus = Collections.emptyMap();
        if (!orderItemIds.isEmpty()) {
            List<MallAfterSaleItemEntity> afterSaleItems = mallAfterSaleItemMapper.selectList(
                    new LambdaQueryWrapper<MallAfterSaleItemEntity>()
                            .in(MallAfterSaleItemEntity::getOrderItemId, orderItemIds));
            if (!afterSaleItems.isEmpty()) {
                List<Long> afterSaleIds = afterSaleItems.stream()
                        .map(MallAfterSaleItemEntity::getAfterSaleId)
                        .distinct()
                        .toList();
                List<MallAfterSaleOrderEntity> afterSaleOrders = mallAfterSaleOrderMapper.selectBatchIds(afterSaleIds);
                Map<Long, String> afterSaleIdToStatus = afterSaleOrders.stream()
                        .collect(Collectors.toMap(MallAfterSaleOrderEntity::getId, MallAfterSaleOrderEntity::getStatus, (a, b) -> a));
                orderItemIdToAfterSaleStatus = afterSaleItems.stream()
                        .collect(Collectors.toMap(MallAfterSaleItemEntity::getOrderItemId, ai -> afterSaleIdToStatus.getOrDefault(ai.getAfterSaleId(), ""), (a, b) -> a));
            }
        }
        Map<Long, String> statusMap = orderItemIdToAfterSaleStatus;
        List<MiniOrderItemVO> itemList = orderItems.stream()
                .map(item -> MiniOrderItemVO.builder()
                        .id(item.getId())
                        .goodsId(item.getGoodsId())
                        .variantId(item.getVariantId())
                        .variantLabel(item.getVariantId() == null ? null : variantIdToLabel.get(item.getVariantId()))
                        .name(item.getGoodsName())
                        .price(item.getGoodsPrice())
                        .quantity(item.getQuantity())
                        .afterSaleStatus(statusMap.get(item.getId()))
                        .build())
                .toList();

        return MiniOrderVO.builder()
                .id(orderEntity.getOrderNo())
                .status(convertMiniOrderStatus(orderEntity.getStatus()))
                .createTime(orderEntity.getCreateTime().format(DATE_TIME_FORMATTER))
                .totalAmount(orderEntity.getTotalAmount())
                .payAmount(orderEntity.getPayAmount())
                .addressText(orderEntity.getAddressText())
                .itemList(itemList)
                .expressCompany(orderEntity.getExpressCompany())
                .expressNo(orderEntity.getExpressNo())
                .shipTime(orderEntity.getShipTime() == null ? null : orderEntity.getShipTime().format(DATE_TIME_FORMATTER))
                .build();
    }

    private void issueCouponsForUser(Long userId) {
        List<MallCouponEntity> couponEntityList = mallCouponMapper.selectList(new LambdaQueryWrapper<MallCouponEntity>()
                .eq(MallCouponEntity::getStatus, true));

        for (MallCouponEntity couponEntity : couponEntityList) {
            MallUserCouponEntity userCouponEntity = new MallUserCouponEntity();
            userCouponEntity.setUserId(userId);
            userCouponEntity.setCouponId(couponEntity.getId());
            userCouponEntity.setStatus("unused");
            userCouponEntity.setExpireTime(couponEntity.getExpireTime());
            mallUserCouponMapper.insert(userCouponEntity);
        }
    }

    private String buildAddressText(MallUserAddressEntity addressEntity) {
        return String.format("%s %s %s%s%s%s",
                addressEntity.getName(),
                addressEntity.getMobile(),
                addressEntity.getProvince(),
                addressEntity.getCity(),
                addressEntity.getDistrict(),
                addressEntity.getDetail());
    }

    private String convertMiniOrderStatus(String orderStatus) {
        if ("待支付".equals(orderStatus)) {
            return "pending";
        }
        if ("待发货".equals(orderStatus)) {
            return "paid";
        }
        if ("配送中".equals(orderStatus)) {
            return "shipping";
        }
        if ("已完成".equals(orderStatus)) {
            return "finished";
        }
        if ("已取消".equals(orderStatus)) {
            return "cancelled";
        }
        return "pending";
    }

    private Boolean cancelOrderInternal(MallOrderEntity orderEntity, String reason) {
        LocalDateTime now = LocalDateTime.now();
        int updated = mallOrderMapper.update(null, new UpdateWrapper<MallOrderEntity>()
                .eq("order_no", orderEntity.getOrderNo())
                .eq("user_id", orderEntity.getUserId())
                .eq("status", "待支付")
                .set("status", "已取消")
                .set("cancel_time", now));
        if (updated <= 0) {
            return false;
        }

        List<MallOrderItemEntity> orderItemList = mallOrderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItemEntity>()
                .eq(MallOrderItemEntity::getOrderId, orderEntity.getId()));

        for (MallOrderItemEntity item : orderItemList) {
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            if (qty <= 0) {
                continue;
            }
            if (item.getVariantId() != null) {
                int variantUpdated = mallGoodsVariantMapper.update(null, new UpdateWrapper<MallGoodsVariantEntity>()
                        .eq("id", item.getVariantId())
                        .apply("locked_stock >= {0}", qty)
                        .setSql("locked_stock = locked_stock - " + qty));
                if (variantUpdated <= 0) {
                    throw new IllegalArgumentException("SKU 锁库存释放失败");
                }
            } else {
                mallGoodsMapper.update(null, new UpdateWrapper<MallGoodsEntity>()
                        .eq("id", item.getGoodsId())
                        .apply("locked_stock >= {0}", qty)
                        .setSql("locked_stock = locked_stock - " + qty));
            }
        }

        // 释放锁券（locked -> unused）
        if (orderEntity.getCouponId() != null) {
            mallUserCouponMapper.update(null, new LambdaUpdateWrapper<MallUserCouponEntity>()
                    .eq(MallUserCouponEntity::getUserId, orderEntity.getUserId())
                    .eq(MallUserCouponEntity::getCouponId, orderEntity.getCouponId())
                    .eq(MallUserCouponEntity::getStatus, "locked")
                    .set(MallUserCouponEntity::getStatus, "unused"));
        }

        return true;
    }

    @Override
    public MiniPrepayVO prepay(MiniPayPrepayDTO dto) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                .eq(MallOrderEntity::getUserId, dto.getUserId())
                .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!"待支付".equals(orderEntity.getStatus())) {
            throw new IllegalArgumentException("订单状态不允许发起支付");
        }
        String payChannel = dto.getPayChannel() == null || dto.getPayChannel().isBlank() ? "wx_mp" : dto.getPayChannel().trim();

        // 记录支付渠道（真实接入时可在此处调用支付网关生成 prepay_id）
        mallOrderMapper.update(null, new UpdateWrapper<MallOrderEntity>()
                .eq("order_no", orderEntity.getOrderNo())
                .eq("user_id", dto.getUserId())
                .set("pay_channel", payChannel));

        return MiniPrepayVO.builder()
                .orderId(orderEntity.getOrderNo())
                .payChannel(payChannel)
                .payDeadline(orderEntity.getPayDeadline() == null ? "" : orderEntity.getPayDeadline().format(DATE_TIME_FORMATTER))
                .raw("{}")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payNotify(MiniPayNotifyDTO dto) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                .last("limit 1"));
        if (orderEntity == null) {
            return true;
        }

        // 写回调日志（tradeNo 有唯一索引，可用于回调幂等）
        MallPayLogEntity logEntity = new MallPayLogEntity();
        logEntity.setOrderNo(orderEntity.getOrderNo());
        logEntity.setTradeNo(dto.getTradeNo() == null || dto.getTradeNo().isBlank() ? null : dto.getTradeNo().trim());
        logEntity.setPayChannel(orderEntity.getPayChannel());
        logEntity.setResult(dto.getResult());
        logEntity.setRaw("{\"orderId\":\"" + dto.getOrderId() + "\",\"result\":\"" + dto.getResult() + "\"}");
        try {
            mallPayLogMapper.insert(logEntity);
        } catch (Exception ignore) {
            // tradeNo 重复等情况：视为幂等重放，继续走状态幂等判断
        }

        // 幂等：已支付/已取消直接返回成功，避免回调重复导致异常
        if ("待发货".equals(orderEntity.getStatus()) || "配送中".equals(orderEntity.getStatus()) || "已完成".equals(orderEntity.getStatus()) || "已取消".equals(orderEntity.getStatus())) {
            return true;
        }

        // 记录交易号（可选）
        if (dto.getTradeNo() != null && !dto.getTradeNo().isBlank()) {
            mallOrderMapper.update(null, new UpdateWrapper<MallOrderEntity>()
                    .eq("order_no", orderEntity.getOrderNo())
                    .set("pay_trade_no", dto.getTradeNo().trim()));
        }

        if ("SUCCESS".equalsIgnoreCase(dto.getResult())) {
            // 真实回调里应有 userId，这里用订单上的 userId
            MiniOrderPayDTO payDTO = new MiniOrderPayDTO();
            payDTO.setUserId(orderEntity.getUserId());
            payDTO.setOrderId(orderEntity.getOrderNo());
            payOrderSuccess(payDTO);
            return true;
        }

        // FAIL：关闭订单并释放
        cancelOrderInternal(orderEntity, "支付失败回调");
        return true;
    }

    private int getCancelMinutes() {
        MallSystemSettingEntity settingEntity = mallSystemSettingMapper.selectOne(new LambdaQueryWrapper<MallSystemSettingEntity>()
                .orderByAsc(MallSystemSettingEntity::getId)
                .last("limit 1"));
        String minutes = settingEntity == null ? null : settingEntity.getCancelMinutes();
        if (minutes == null || minutes.isBlank()) {
            return 30;
        }
        try {
            int value = Integer.parseInt(minutes.trim());
            return value <= 0 ? 30 : value;
        } catch (Exception ignore) {
            return 30;
        }
    }

    private List<String> splitString(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
