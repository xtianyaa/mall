package com.yuan.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.yuan.mall.entity.MallBannerEntity;
import com.yuan.mall.entity.MallHomeDecoEntity;
import com.yuan.mall.entity.MallCategoryEntity;
import com.yuan.mall.entity.MallCouponEntity;
import com.yuan.mall.entity.MallGoodsEntity;
import com.yuan.mall.entity.MallGoodsVariantEntity;
import com.yuan.mall.entity.MallAfterSaleOrderEntity;
import com.yuan.mall.entity.MallCartEntity;
import com.yuan.mall.entity.MallOrderEntity;
import com.yuan.mall.entity.MallOrderItemEntity;
import com.yuan.mall.entity.MallSystemSettingEntity;
import com.yuan.mall.entity.MallUserAddressEntity;
import com.yuan.mall.entity.MallUserCouponEntity;
import com.yuan.mall.entity.MallUserEntity;
import com.yuan.mall.mapper.MallBannerMapper;
import com.yuan.mall.mapper.MallHomeDecoMapper;
import com.yuan.mall.mapper.MallCategoryMapper;
import com.yuan.mall.mapper.MallCouponMapper;
import com.yuan.mall.mapper.MallGoodsMapper;
import com.yuan.mall.mapper.MallGoodsVariantMapper;
import com.yuan.mall.mapper.MallAfterSaleOrderMapper;
import com.yuan.mall.mapper.MallCartMapper;
import com.yuan.mall.mapper.MallOrderItemMapper;
import com.yuan.mall.mapper.MallOrderMapper;
import com.yuan.mall.mapper.MallSystemSettingMapper;
import com.yuan.mall.mapper.MallUserAddressMapper;
import com.yuan.mall.mapper.MallUserCouponMapper;
import com.yuan.mall.mapper.MallUserMapper;
import com.yuan.mall.service.AdminMallService;
import com.yuan.mall.vo.MallBannerVO;
import com.yuan.mall.vo.HomeDecoVO;
import com.yuan.mall.vo.MallCategoryVO;
import com.yuan.mall.vo.MallCouponVO;
import com.yuan.mall.vo.MallDashboardVO;
import com.yuan.mall.vo.MallGoodsVO;
import com.yuan.mall.vo.MallGoodsVariantVO;
import com.yuan.mall.vo.MallOrderVO;
import com.yuan.mall.vo.MallSystemSettingVO;
import com.yuan.mall.vo.MiniOrderItemVO;
import com.yuan.mall.vo.MiniOrderVO;
import com.yuan.mall.vo.MallUserAddressVO;
import com.yuan.mall.vo.MallUserCouponVO;
import com.yuan.mall.vo.MallUserVO;
import com.yuan.mall.vo.PageResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * 管理后台商城服务实现
 */
@Service
public class AdminMallServiceImpl implements AdminMallService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MallCategoryMapper mallCategoryMapper;
    private final MallGoodsMapper mallGoodsMapper;
    private final MallGoodsVariantMapper mallGoodsVariantMapper;
    private final MallCartMapper mallCartMapper;
    private final MallOrderMapper mallOrderMapper;
    private final MallOrderItemMapper mallOrderItemMapper;
    private final MallAfterSaleOrderMapper mallAfterSaleOrderMapper;
    private final MallUserMapper mallUserMapper;
    private final MallCouponMapper mallCouponMapper;
    private final MallBannerMapper mallBannerMapper;
    private final MallHomeDecoMapper mallHomeDecoMapper;
    private final MallSystemSettingMapper mallSystemSettingMapper;
    private final MallUserAddressMapper mallUserAddressMapper;
    private final MallUserCouponMapper mallUserCouponMapper;

    public AdminMallServiceImpl(MallCategoryMapper mallCategoryMapper,
                                MallGoodsMapper mallGoodsMapper,
                                MallGoodsVariantMapper mallGoodsVariantMapper,
                                MallCartMapper mallCartMapper,
                                MallOrderMapper mallOrderMapper,
                                MallOrderItemMapper mallOrderItemMapper,
                                MallAfterSaleOrderMapper mallAfterSaleOrderMapper,
                                MallUserMapper mallUserMapper,
                                MallCouponMapper mallCouponMapper,
                                MallBannerMapper mallBannerMapper,
                                MallHomeDecoMapper mallHomeDecoMapper,
                                MallSystemSettingMapper mallSystemSettingMapper,
                                MallUserAddressMapper mallUserAddressMapper,
                                MallUserCouponMapper mallUserCouponMapper) {
        this.mallCategoryMapper = mallCategoryMapper;
        this.mallGoodsMapper = mallGoodsMapper;
        this.mallGoodsVariantMapper = mallGoodsVariantMapper;
        this.mallCartMapper = mallCartMapper;
        this.mallOrderMapper = mallOrderMapper;
        this.mallOrderItemMapper = mallOrderItemMapper;
        this.mallAfterSaleOrderMapper = mallAfterSaleOrderMapper;
        this.mallUserMapper = mallUserMapper;
        this.mallCouponMapper = mallCouponMapper;
        this.mallBannerMapper = mallBannerMapper;
        this.mallHomeDecoMapper = mallHomeDecoMapper;
        this.mallSystemSettingMapper = mallSystemSettingMapper;
        this.mallUserAddressMapper = mallUserAddressMapper;
        this.mallUserCouponMapper = mallUserCouponMapper;
    }

    @Override
    public MallDashboardVO dashboard() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<MallOrderEntity> todayOrderList = mallOrderMapper.selectList(new LambdaQueryWrapper<MallOrderEntity>()
                .ge(MallOrderEntity::getCreateTime, todayStart)
                .ne(MallOrderEntity::getStatus, "待支付")
                .ne(MallOrderEntity::getStatus, "已取消"));

        BigDecimal todayAmount = todayOrderList.stream()
                .map(MallOrderEntity::getPayAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long waitShipCount = mallOrderMapper.selectCount(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getStatus, "待发货"));
        Long userCount = mallUserMapper.selectCount(null);

        return MallDashboardVO.builder()
                .todayOrderCount(todayOrderList.size())
                .waitShipCount(waitShipCount.intValue())
                .userCount(userCount.intValue())
                .todayAmount(todayAmount.toPlainString())
                .build();
    }

    @Override
    public PageResultVO<MallCategoryVO> listCategory(String nameKeyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<MallCategoryEntity> wrapper = new LambdaQueryWrapper<MallCategoryEntity>()
                .orderByAsc(MallCategoryEntity::getSort, MallCategoryEntity::getId);
        if (nameKeyword != null && !nameKeyword.isBlank()) {
            wrapper.like(MallCategoryEntity::getName, nameKeyword.trim());
        }
        Page<MallCategoryEntity> page = mallCategoryMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        List<MallCategoryEntity> categoryEntityList = page.getRecords();
        if (categoryEntityList.isEmpty()) {
            return PageResultVO.<MallCategoryVO>builder().list(List.of()).total(page.getTotal()).build();
        }
        List<Long> categoryIds = categoryEntityList.stream().map(MallCategoryEntity::getId).toList();
        List<MallGoodsEntity> goodsInCategories = mallGoodsMapper.selectList(
                new LambdaQueryWrapper<MallGoodsEntity>().in(MallGoodsEntity::getCategoryId, categoryIds));
        Map<Long, Long> goodsCountMap = goodsInCategories.stream()
                .collect(Collectors.groupingBy(MallGoodsEntity::getCategoryId, Collectors.counting()));

        List<MallCategoryVO> list = categoryEntityList.stream()
                .map(item -> MallCategoryVO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .icon(item.getIcon())
                        .sort(item.getSort())
                        .status(item.getStatus())
                        .goodsCount(goodsCountMap.getOrDefault(item.getId(), 0L).intValue())
                        .build())
                .toList();
        return PageResultVO.<MallCategoryVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCategoryVO createCategory(CategoryCreateDTO dto) {
        MallCategoryEntity categoryEntity = new MallCategoryEntity();
        categoryEntity.setName(dto.getName());
        categoryEntity.setIcon(dto.getIcon());
        categoryEntity.setSort(dto.getSort());
        categoryEntity.setStatus(dto.getStatus());
        mallCategoryMapper.insert(categoryEntity);

        return MallCategoryVO.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .sort(categoryEntity.getSort())
                .status(categoryEntity.getStatus())
                .goodsCount(0)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCategoryStatus(CategoryStatusUpdateDTO dto) {
        int updated = mallCategoryMapper.update(null, new LambdaUpdateWrapper<MallCategoryEntity>()
                .eq(MallCategoryEntity::getId, dto.getId())
                .set(MallCategoryEntity::getStatus, dto.getStatus()));
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCategoryVO updateCategory(CategoryUpdateDTO dto) {
        MallCategoryEntity entity = mallCategoryMapper.selectById(dto.getId());
        if (entity == null) {
            throw new IllegalArgumentException("分类不存在: " + dto.getId());
        }
        entity.setName(dto.getName());
        entity.setIcon(dto.getIcon());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus());
        mallCategoryMapper.updateById(entity);
        Long goodsCount = mallGoodsMapper.selectCount(new LambdaQueryWrapper<MallGoodsEntity>()
                .eq(MallGoodsEntity::getCategoryId, entity.getId()));
        return MallCategoryVO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .sort(entity.getSort())
                .status(entity.getStatus())
                .goodsCount(goodsCount.intValue())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCategory(CategoryDeleteDTO dto) {
        MallCategoryEntity entity = mallCategoryMapper.selectById(dto.getId());
        if (entity == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        long cnt = mallGoodsMapper.selectCount(new LambdaQueryWrapper<MallGoodsEntity>()
                .eq(MallGoodsEntity::getCategoryId, dto.getId()));
        if (cnt > 0) {
            throw new IllegalArgumentException("该分类下仍有商品，请先删除或迁移商品后再删分类");
        }
        return mallCategoryMapper.deleteById(dto.getId()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteCategory(CategoryBatchDeleteDTO dto) {
        List<Long> ids = dto.getIds().stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("请选择要删除的分类");
        }
        for (Long id : ids) {
            long cnt = mallGoodsMapper.selectCount(new LambdaQueryWrapper<MallGoodsEntity>()
                    .eq(MallGoodsEntity::getCategoryId, id));
            if (cnt > 0) {
                throw new IllegalArgumentException("分类 ID " + id + " 下仍有商品，无法删除");
            }
        }
        int rows = 0;
        for (Long id : ids) {
            rows += mallCategoryMapper.deleteById(id);
        }
        return rows > 0;
    }

    @Override
    public PageResultVO<MallGoodsVO> listGoods(String keyword, Long categoryId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MallGoodsEntity> wrapper = new LambdaQueryWrapper<MallGoodsEntity>()
                .orderByDesc(MallGoodsEntity::getId);
        if (categoryId != null) {
            wrapper.eq(MallGoodsEntity::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            wrapper.and(w -> w
                    .like(MallGoodsEntity::getName, k)
                    .or()
                    .like(MallGoodsEntity::getCharacteristic, k));
        }
        Page<MallGoodsEntity> page = mallGoodsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<MallGoodsEntity> goodsList = page.getRecords();
        if (goodsList.isEmpty()) {
            return PageResultVO.<MallGoodsVO>builder().list(List.of()).total(page.getTotal()).build();
        }

        List<Long> goodsIds = goodsList.stream().map(MallGoodsEntity::getId).toList();
        List<MallGoodsVariantEntity> variantEntities = goodsIds.isEmpty() ? List.of() : mallGoodsVariantMapper.selectList(
                new LambdaQueryWrapper<MallGoodsVariantEntity>().in(MallGoodsVariantEntity::getGoodsId, goodsIds));

        Map<Long, List<MallGoodsVariantEntity>> goodsIdToVariants = variantEntities.stream()
                .collect(Collectors.groupingBy(MallGoodsVariantEntity::getGoodsId));

        List<MallGoodsVO> list = goodsList.stream()
                .map(goods -> buildGoodsVO(goods, goodsIdToVariants.getOrDefault(goods.getId(), List.of())))
                .toList();

        return PageResultVO.<MallGoodsVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallGoodsVO createGoods(GoodsCreateDTO dto) {
        validateGoodsPayload(dto);
        MallCategoryEntity categoryEntity = mallCategoryMapper.selectOne(new LambdaQueryWrapper<MallCategoryEntity>()
                .eq(MallCategoryEntity::getName, dto.getCategoryName())
                .last("limit 1"));

        if (categoryEntity == null) {
            throw new IllegalArgumentException("分类不存在，请先创建分类");
        }

        MallGoodsEntity goodsEntity = new MallGoodsEntity();
        goodsEntity.setCategoryId(categoryEntity.getId());
        goodsEntity.setCategoryName(categoryEntity.getName());
        goodsEntity.setMonthlySales(0);
        applyGoodsPayload(goodsEntity, dto);
        mallGoodsMapper.insert(goodsEntity);

        List<MallGoodsVariantEntity> variantEntities = buildVariantEntities(goodsEntity, resolveVariants(dto));
        for (MallGoodsVariantEntity variantEntity : variantEntities) {
            mallGoodsVariantMapper.insert(variantEntity);
        }

        applyGoodsSummaryFromVariants(goodsEntity, variantEntities);
        mallGoodsMapper.updateById(goodsEntity);

        return buildGoodsVO(goodsEntity, variantEntities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallGoodsVO updateGoods(GoodsUpdateDTO dto) {
        validateGoodsPayload(dto);
        MallGoodsEntity goodsEntity = mallGoodsMapper.selectById(dto.getId());
        if (goodsEntity == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        MallCategoryEntity categoryEntity = mallCategoryMapper.selectOne(new LambdaQueryWrapper<MallCategoryEntity>()
                .eq(MallCategoryEntity::getName, dto.getCategoryName())
                .last("limit 1"));

        if (categoryEntity == null) {
            throw new IllegalArgumentException("分类不存在，请先创建分类");
        }

        goodsEntity.setCategoryId(categoryEntity.getId());
        goodsEntity.setCategoryName(categoryEntity.getName());
        applyGoodsPayload(goodsEntity, dto);
        mallGoodsMapper.updateById(goodsEntity);

        // 规格SKU：更新时直接重建，避免复杂的增删改同步逻辑
        mallGoodsVariantMapper.delete(new LambdaQueryWrapper<MallGoodsVariantEntity>()
                .eq(MallGoodsVariantEntity::getGoodsId, goodsEntity.getId()));

        List<MallGoodsVariantEntity> variantEntities = buildVariantEntities(goodsEntity, resolveVariants(dto));
        for (MallGoodsVariantEntity variantEntity : variantEntities) {
            mallGoodsVariantMapper.insert(variantEntity);
        }

        applyGoodsSummaryFromVariants(goodsEntity, variantEntities);
        mallGoodsMapper.updateById(goodsEntity);

        return buildGoodsVO(goodsEntity, variantEntities);
    }

    private void applyGoodsPayload(MallGoodsEntity goodsEntity, GoodsCreateDTO dto) {
        goodsEntity.setName(dto.getName());
        goodsEntity.setDiscreetName(dto.getDiscreetName());
        goodsEntity.setCharacteristic(dto.getCharacteristic());
        goodsEntity.setPrice(dto.getPrice());
        goodsEntity.setOriginalPrice(dto.getOriginalPrice());
        goodsEntity.setUnit(dto.getUnit());
        goodsEntity.setStock(dto.getStock());
        goodsEntity.setStatus(dto.getStatus());
        goodsEntity.setTags(defaultString(dto.getTags(), "新品"));
        goodsEntity.setBannerList(defaultString(dto.getBannerList(), "📦"));
        goodsEntity.setDetailList(defaultString(dto.getDetailList(), "请在管理后台补充商品详情"));
    }

    private void validateGoodsPayload(GoodsCreateDTO dto) {
        if (dto.getOriginalPrice().compareTo(dto.getPrice()) < 0) {
            throw new IllegalArgumentException("划线价不能低于销售价");
        }

        List<com.yuan.mall.dto.GoodsVariantCreateDTO> variants = dto.getVariants();
        if (variants == null || variants.isEmpty()) {
            return;
        }
        for (com.yuan.mall.dto.GoodsVariantCreateDTO v : variants) {
            if (v == null) continue;
            if (v.getOriginalPrice().compareTo(v.getPrice()) < 0) {
                throw new IllegalArgumentException("SKU 划线价不能低于销售价");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateGoodsStatus(GoodsStatusUpdateDTO dto) {
        int updated = mallGoodsMapper.update(null, new LambdaUpdateWrapper<MallGoodsEntity>()
                .eq(MallGoodsEntity::getId, dto.getId())
                .set(MallGoodsEntity::getStatus, dto.getStatus()));
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGoods(GoodsDeleteDTO dto) {
        deleteGoodsCascade(dto.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteGoods(GoodsBatchDeleteDTO dto) {
        List<Long> ids = dto.getIds().stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            throw new IllegalArgumentException("请选择要删除的商品");
        }
        for (Long id : ids) {
            deleteGoodsCascade(id);
        }
        return true;
    }

    /**
     * 删除商品：规格 SKU、购物车行、商品主表（订单明细保留历史快照）
     */
    private void deleteGoodsCascade(Long goodsId) {
        MallGoodsEntity goods = mallGoodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new IllegalArgumentException("商品不存在: " + goodsId);
        }
        mallGoodsVariantMapper.delete(new LambdaQueryWrapper<MallGoodsVariantEntity>()
                .eq(MallGoodsVariantEntity::getGoodsId, goodsId));
        mallCartMapper.delete(new LambdaQueryWrapper<MallCartEntity>()
                .eq(MallCartEntity::getGoodsId, goodsId));
        mallGoodsMapper.deleteById(goodsId);
    }

    private MallGoodsVO buildGoodsVO(MallGoodsEntity goodsEntity, List<MallGoodsVariantEntity> variantEntities) {
        List<MallGoodsVariantVO> variantVOs;
        List<String> fallbackSpecNames = List.of("规格");
        Map<String, String> fallbackSpecs = Map.of();

        if (variantEntities == null || variantEntities.isEmpty()) {
            variantVOs = List.of(MallGoodsVariantVO.builder()
                    .goodsId(goodsEntity.getId())
                    .specs(fallbackSpecs)
                    .specNames(fallbackSpecNames)
                    .price(goodsEntity.getPrice())
                    .originalPrice(goodsEntity.getOriginalPrice())
                    .unit(goodsEntity.getUnit())
                    .stock(goodsEntity.getStock())
                    .build());
        } else {
            variantVOs = variantEntities.stream()
                    .map(v -> MallGoodsVariantVO.builder()
                            .id(v.getId())
                            .goodsId(v.getGoodsId())
                            .specs(v.getSpecsMap())
                            .specNames(v.getSpecNamesList())
                            .price(v.getPrice())
                            .originalPrice(v.getOriginalPrice())
                            .unit(v.getUnit())
                            .stock(v.getStock())
                            .build())
                    .toList();
        }

        BigDecimal price = (variantEntities == null || variantEntities.isEmpty())
                ? goodsEntity.getPrice()
                : variantEntities.stream()
                    .map(MallGoodsVariantEntity::getPrice)
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo)
                    .orElse(goodsEntity.getPrice());

        BigDecimal originalPrice = (variantEntities == null || variantEntities.isEmpty())
                ? goodsEntity.getOriginalPrice()
                : variantEntities.stream()
                    .map(MallGoodsVariantEntity::getOriginalPrice)
                    .filter(p -> p != null)
                    .min(BigDecimal::compareTo)
                    .orElse(goodsEntity.getOriginalPrice());

        Integer stock = (variantEntities == null || variantEntities.isEmpty())
                ? goodsEntity.getStock()
                : variantEntities.stream()
                    .map(MallGoodsVariantEntity::getStock)
                    .filter(s -> s != null)
                    .reduce(0, Integer::sum);

        return MallGoodsVO.builder()
                .id(goodsEntity.getId())
                .name(goodsEntity.getName())
                .discreetName(goodsEntity.getDiscreetName())
                .categoryName(goodsEntity.getCategoryName())
                .characteristic(goodsEntity.getCharacteristic())
                .price(price)
                .originalPrice(originalPrice)
                .unit(goodsEntity.getUnit())
                .stock(stock)
                .monthlySales(goodsEntity.getMonthlySales())
                .tags(goodsEntity.getTags())
                .bannerList(goodsEntity.getBannerList())
                .detailList(goodsEntity.getDetailList())
                .status(goodsEntity.getStatus())
                .variants(variantVOs)
                .build();
    }

    private List<com.yuan.mall.dto.GoodsVariantCreateDTO> resolveVariants(GoodsCreateDTO dto) {
        List<com.yuan.mall.dto.GoodsVariantCreateDTO> variants = dto.getVariants();
        if (variants != null && !variants.isEmpty()) {
            return variants;
        }
        // 老商品兼容：自动生成一个默认SKU（specs={}，specNames=["规格"]）
        com.yuan.mall.dto.GoodsVariantCreateDTO defaultVariant = new com.yuan.mall.dto.GoodsVariantCreateDTO();
        defaultVariant.setSpecs(Map.of());
        defaultVariant.setSpecNames(List.of("规格"));
        defaultVariant.setPrice(dto.getPrice());
        defaultVariant.setOriginalPrice(dto.getOriginalPrice());
        defaultVariant.setUnit(dto.getUnit());
        defaultVariant.setStock(dto.getStock());
        return List.of(defaultVariant);
    }

    private List<MallGoodsVariantEntity> buildVariantEntities(MallGoodsEntity goodsEntity,
                                                                List<com.yuan.mall.dto.GoodsVariantCreateDTO> variantDTOs) {
        Long goodsId = goodsEntity.getId();
        String goodsName = goodsEntity.getName();
        Boolean status = goodsEntity.getStatus();
        List<com.yuan.mall.dto.GoodsVariantCreateDTO> variants = variantDTOs == null ? List.of() : variantDTOs;

        return variants.stream().map(v -> {
            MallGoodsVariantEntity entity = new MallGoodsVariantEntity();
            entity.setGoodsId(goodsId);
            entity.setGoodsName(goodsName);
            // specs：直接使用 DTO 的 Map
            entity.setSpecsFromMap(v.getSpecs() == null ? Map.of() : v.getSpecs());
            // specNames：直接使用 DTO 的 List
            entity.setSpecNamesFromList(v.getSpecNames() == null ? List.of() : v.getSpecNames());
            entity.setPrice(v.getPrice());
            entity.setOriginalPrice(v.getOriginalPrice());
            entity.setUnit(v.getUnit() == null || v.getUnit().isBlank() ? goodsEntity.getUnit() : v.getUnit().trim());
            entity.setStock(v.getStock());
            entity.setLockedStock(0);
            entity.setStatus(status);
            return entity;
        }).toList();
    }

    private void applyGoodsSummaryFromVariants(MallGoodsEntity goodsEntity,
                                                List<MallGoodsVariantEntity> variantEntities) {
        if (variantEntities == null || variantEntities.isEmpty()) {
            return;
        }

        BigDecimal price = variantEntities.stream()
                .map(MallGoodsVariantEntity::getPrice)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(goodsEntity.getPrice());

        BigDecimal originalPrice = variantEntities.stream()
                .map(MallGoodsVariantEntity::getOriginalPrice)
                .filter(p -> p != null)
                .min(BigDecimal::compareTo)
                .orElse(goodsEntity.getOriginalPrice());

        Integer stock = variantEntities.stream()
                .map(MallGoodsVariantEntity::getStock)
                .filter(s -> s != null)
                .reduce(0, Integer::sum);

        goodsEntity.setPrice(price);
        goodsEntity.setOriginalPrice(originalPrice);
        goodsEntity.setStock(stock);
    }

    private String defaultString(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    @Override
    public PageResultVO<MallOrderVO> listOrder(String status, Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MallOrderEntity> wrapper = new LambdaQueryWrapper<MallOrderEntity>()
                .orderByDesc(MallOrderEntity::getId);
        if (status != null && !status.isBlank()) {
            wrapper.eq(MallOrderEntity::getStatus, status.trim());
        }
        if (userId != null) {
            wrapper.eq(MallOrderEntity::getUserId, userId);
        }
        Page<MallOrderEntity> page = mallOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<MallOrderEntity> records = page.getRecords();
        List<Long> orderIds = records.stream().map(MallOrderEntity::getId).toList();
        Map<Long, Integer> orderIdToAfterSaleCount = orderIds.isEmpty() ? Map.of() : mallAfterSaleOrderMapper.selectList(
                        new LambdaQueryWrapper<MallAfterSaleOrderEntity>().in(MallAfterSaleOrderEntity::getOrderId, orderIds))
                .stream()
                .collect(Collectors.groupingBy(MallAfterSaleOrderEntity::getOrderId, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        Map<Long, Integer> countMap = orderIdToAfterSaleCount;
        List<MallOrderVO> list = records.stream()
                .map(item -> MallOrderVO.builder()
                        .userId(item.getUserId())
                        .orderId(item.getOrderNo())
                        .userName(item.getUserName())
                        .payAmount(item.getPayAmount())
                        .isDiscreet(item.getIsDiscreet())
                        .status(item.getStatus())
                        .createTime(item.getCreateTime() == null ? "" : item.getCreateTime().format(DATE_TIME_FORMATTER))
                        .afterSaleCount(countMap.getOrDefault(item.getId(), 0))
                        .build())
                .toList();
        return PageResultVO.<MallOrderVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    public MiniOrderVO getOrderDetail(String orderId) {
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, orderId)
                .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }

        List<MiniOrderItemVO> itemList = mallOrderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItemEntity>()
                        .eq(MallOrderItemEntity::getOrderId, orderEntity.getId())
                        .orderByAsc(MallOrderItemEntity::getId))
                .stream()
                .map(item -> MiniOrderItemVO.builder()
                        .goodsId(item.getGoodsId())
                        .name(item.getGoodsName())
                        .price(item.getGoodsPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        return MiniOrderVO.builder()
                .id(orderEntity.getOrderNo())
                .status(convertMiniOrderStatus(orderEntity.getStatus()))
                .createTime(orderEntity.getCreateTime().format(DATE_TIME_FORMATTER))
                .totalAmount(orderEntity.getTotalAmount())
                .payAmount(orderEntity.getPayAmount())
                .addressText(orderEntity.getAddressText())
                .isDiscreet(orderEntity.getIsDiscreet())
                .itemList(itemList)
                .expressCompany(orderEntity.getExpressCompany())
                .expressNo(orderEntity.getExpressNo())
                .shipTime(orderEntity.getShipTime() == null ? null : orderEntity.getShipTime().format(DATE_TIME_FORMATTER))
                .build();
    }

    private String convertMiniOrderStatus(String orderStatus) {
        if ("待支付".equals(orderStatus)) return "pending";
        if ("待发货".equals(orderStatus)) return "paid";
        if ("配送中".equals(orderStatus)) return "shipping";
        if ("已完成".equals(orderStatus)) return "finished";
        if ("已取消".equals(orderStatus)) return "cancelled";
        return "pending";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderStatus(OrderStatusUpdateDTO dto) {
        String target = dto.getTargetStatus();
        if (!List.of("已取消", "已完成").contains(target)) {
            throw new IllegalArgumentException("不支持的目标状态");
        }
        // 仅允许：待支付->已取消（关单/取消），配送中->已完成
        LambdaUpdateWrapper<MallOrderEntity> wrapper = new LambdaUpdateWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId());
        if ("已取消".equals(target)) {
            MallOrderEntity order = mallOrderMapper.selectOne(new LambdaQueryWrapper<MallOrderEntity>()
                    .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                    .last("limit 1"));
            int updated = mallOrderMapper.update(null, wrapper
                    .eq(MallOrderEntity::getStatus, "待支付")
                    .set(MallOrderEntity::getStatus, "已取消")
                    .set(MallOrderEntity::getCancelTime, LocalDateTime.now()));
            if (updated > 0 && order != null && order.getCouponId() != null) {
                LambdaUpdateWrapper<MallUserCouponEntity> uw = new LambdaUpdateWrapper<MallUserCouponEntity>()
                        .eq(MallUserCouponEntity::getUserId, order.getUserId())
                        .eq(MallUserCouponEntity::getCouponId, order.getCouponId())
                        .eq(MallUserCouponEntity::getStatus, "locked")
                        .set(MallUserCouponEntity::getStatus, "unused");
                mallUserCouponMapper.update(null, uw.last("LIMIT 1"));
            }
            return updated > 0;
        }
        int updated = mallOrderMapper.update(null, wrapper
                .eq(MallOrderEntity::getStatus, "配送中")
                .set(MallOrderEntity::getStatus, "已完成"));
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean shipOrder(OrderShipDTO dto) {
        int updated = mallOrderMapper.update(null, new LambdaUpdateWrapper<MallOrderEntity>()
                .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                .set(MallOrderEntity::getStatus, "配送中")
                .set(MallOrderEntity::getExpressCompany, dto.getExpressCompany())
                .set(MallOrderEntity::getExpressNo, dto.getExpressNo())
                .set(MallOrderEntity::getShipTime, LocalDateTime.now()));
        return updated > 0;
    }

    @Override
    public PageResultVO<MallUserVO> listUser(int pageNum, int pageSize) {
        Page<MallUserEntity> page = mallUserMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MallUserEntity>().orderByDesc(MallUserEntity::getId));
        List<MallUserVO> list = page.getRecords().stream()
                .map(item -> MallUserVO.builder()
                        .id(item.getId())
                        .nickName(item.getNickName())
                        .mobile(item.getMobile())
                        .levelName(item.getLevelName())
                        .orderCount(item.getOrderCount())
                        .consumeAmount(item.getConsumeAmount())
                        .build())
                .toList();
        return PageResultVO.<MallUserVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    public PageResultVO<MallUserAddressVO> listUserAddress(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MallUserAddressEntity> wrapper = new LambdaQueryWrapper<MallUserAddressEntity>()
                .orderByDesc(MallUserAddressEntity::getId);
        if (userId != null) {
            wrapper.eq(MallUserAddressEntity::getUserId, userId);
        }
        Page<MallUserAddressEntity> page = mallUserAddressMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<MallUserAddressEntity> addressEntityList = page.getRecords();
        if (addressEntityList.isEmpty()) {
            return PageResultVO.<MallUserAddressVO>builder().list(List.of()).total(page.getTotal()).build();
        }
        List<Long> userIds = addressEntityList.stream().map(MallUserAddressEntity::getUserId).distinct().toList();
        Map<Long, MallUserEntity> userMap = mallUserMapper.selectList(new LambdaQueryWrapper<MallUserEntity>()
                        .in(MallUserEntity::getId, userIds))
                .stream()
                .collect(Collectors.toMap(MallUserEntity::getId, item -> item));

        List<MallUserAddressVO> list = addressEntityList.stream()
                .map(item -> {
                    MallUserEntity userEntity = userMap.get(item.getUserId());
                    return MallUserAddressVO.builder()
                            .id(item.getId())
                            .userId(item.getUserId())
                            .userName(userEntity == null ? "-" : userEntity.getNickName())
                            .mobile(item.getMobile())
                            .consignee(item.getName())
                            .addressText(item.getProvince() + item.getCity() + item.getDistrict() + item.getDetail())
                            .isDefault(item.getIsDefault())
                            .build();
                })
                .toList();
        return PageResultVO.<MallUserAddressVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    public PageResultVO<MallUserCouponVO> listUserCoupon(Long userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<MallUserCouponEntity> wrapper = new LambdaQueryWrapper<MallUserCouponEntity>()
                .orderByDesc(MallUserCouponEntity::getId);
        if (userId != null) {
            wrapper.eq(MallUserCouponEntity::getUserId, userId);
        }
        Page<MallUserCouponEntity> page = mallUserCouponMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<MallUserCouponEntity> userCouponEntityList = page.getRecords();
        if (userCouponEntityList.isEmpty()) {
            return PageResultVO.<MallUserCouponVO>builder().list(List.of()).total(page.getTotal()).build();
        }
        List<Long> userIds = userCouponEntityList.stream().map(MallUserCouponEntity::getUserId).distinct().toList();
        List<Long> couponIds = userCouponEntityList.stream().map(MallUserCouponEntity::getCouponId).distinct().toList();
        Map<Long, MallUserEntity> userMap = mallUserMapper.selectList(new LambdaQueryWrapper<MallUserEntity>()
                        .in(MallUserEntity::getId, userIds))
                .stream()
                .collect(Collectors.toMap(MallUserEntity::getId, item -> item));
        Map<Long, MallCouponEntity> couponMap = mallCouponMapper.selectList(new LambdaQueryWrapper<MallCouponEntity>()
                        .in(MallCouponEntity::getId, couponIds))
                .stream()
                .collect(Collectors.toMap(MallCouponEntity::getId, item -> item));

        List<MallUserCouponVO> list = userCouponEntityList.stream()
                .map(item -> {
                    MallUserEntity userEntity = userMap.get(item.getUserId());
                    MallCouponEntity couponEntity = couponMap.get(item.getCouponId());
                    return MallUserCouponVO.builder()
                            .id(item.getId())
                            .userId(item.getUserId())
                            .userName(userEntity == null ? "-" : userEntity.getNickName())
                            .couponName(couponEntity == null ? "-" : couponEntity.getName())
                            .couponAmount(couponEntity == null ? "0.00" : couponEntity.getAmount().toPlainString())
                            .useThreshold(couponEntity == null ? "0.00" : couponEntity.getMinAmount().toPlainString())
                            .status(item.getStatus())
                            .expireTime(item.getExpireTime() == null ? "" : item.getExpireTime().format(DATE_TIME_FORMATTER))
                            .build();
                })
                .toList();
        return PageResultVO.<MallUserCouponVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    public PageResultVO<MallCouponVO> listCoupon(int pageNum, int pageSize) {
        Page<MallCouponEntity> page = mallCouponMapper.selectPage(
                new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MallCouponEntity>().orderByDesc(MallCouponEntity::getId));
        List<MallCouponVO> list = page.getRecords().stream()
                .map(item -> MallCouponVO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .amount(item.getAmount())
                        .minAmount(item.getMinAmount())
                        .receiveCount(item.getReceiveCount())
                        .status(item.getStatus())
                        .startTime(item.getStartTime())
                        .expireTime(item.getExpireTime())
                        .build())
                .toList();
        return PageResultVO.<MallCouponVO>builder().list(list).total(page.getTotal()).build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCouponVO createCoupon(CouponCreateDTO dto) {
        MallCouponEntity couponEntity = new MallCouponEntity();
        couponEntity.setName(dto.getName());
        couponEntity.setAmount(dto.getAmount());
        couponEntity.setMinAmount(dto.getMinAmount());
        couponEntity.setReceiveCount(0);
        couponEntity.setStatus(dto.getStatus());
        couponEntity.setStartTime(dto.getStartTime());
        couponEntity.setExpireTime(dto.getExpireTime() != null ? dto.getExpireTime() : LocalDateTime.now().plusDays(30));
        mallCouponMapper.insert(couponEntity);

        return MallCouponVO.builder()
                .id(couponEntity.getId())
                .name(couponEntity.getName())
                .amount(couponEntity.getAmount())
                .minAmount(couponEntity.getMinAmount())
                .receiveCount(couponEntity.getReceiveCount())
                .status(couponEntity.getStatus())
                .startTime(couponEntity.getStartTime())
                .expireTime(couponEntity.getExpireTime())
                .build();
    }

    @Override
    public List<MallBannerVO> listBanner() {
        return mallBannerMapper.selectList(new LambdaQueryWrapper<MallBannerEntity>()
                        .orderByAsc(MallBannerEntity::getSort, MallBannerEntity::getId))
                .stream()
                .map(item -> MallBannerVO.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .linkType(item.getLinkType())
                        .imageUrl(item.getImageUrl())
                        .linkUrl(item.getLinkUrl())
                        .status(item.getStatus())
                        .sort(item.getSort())
                        .build())
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallBannerVO createBanner(BannerCreateDTO dto) {
        MallBannerEntity entity = new MallBannerEntity();
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setLinkType(dto.getLinkType());
        entity.setLinkUrl(dto.getLinkUrl() == null ? "" : dto.getLinkUrl().trim());
        entity.setStatus(dto.getStatus());
        entity.setSort(dto.getSort());
        mallBannerMapper.insert(entity);
        return MallBannerVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .linkType(entity.getLinkType())
                .linkUrl(entity.getLinkUrl())
                .status(entity.getStatus())
                .sort(entity.getSort())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallBannerVO updateBanner(BannerUpdateDTO dto) {
        MallBannerEntity entity = mallBannerMapper.selectById(dto.getId());
        if (entity == null) {
            throw new IllegalArgumentException("Banner 不存在");
        }
        entity.setTitle(dto.getTitle());
        entity.setImageUrl(dto.getImageUrl());
        entity.setLinkType(dto.getLinkType());
        entity.setLinkUrl(dto.getLinkUrl() == null ? "" : dto.getLinkUrl().trim());
        entity.setStatus(dto.getStatus());
        entity.setSort(dto.getSort());
        mallBannerMapper.updateById(entity);
        return MallBannerVO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .linkType(entity.getLinkType())
                .linkUrl(entity.getLinkUrl())
                .status(entity.getStatus())
                .sort(entity.getSort())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBanner(BannerDeleteDTO dto) {
        return mallBannerMapper.deleteById(dto.getId()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBannerSort(BannerSortUpdateDTO dto) {
        int updated = mallBannerMapper.update(null, new LambdaUpdateWrapper<MallBannerEntity>()
                .eq(MallBannerEntity::getId, dto.getId())
                .set(MallBannerEntity::getSort, dto.getSort()));
        return updated > 0;
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
    @Transactional(rollbackFor = Exception.class)
    public HomeDecoVO updateHomeDeco(HomeDecoUpdateDTO dto) {
        MallHomeDecoEntity entity = mallHomeDecoMapper.selectById(1L);
        if (entity == null) {
            entity = new MallHomeDecoEntity();
            entity.setId(1L);
            entity.setSearchPlaceholder(dto.getSearchPlaceholder());
            entity.setStat1Value(dto.getStat1Value());
            entity.setStat1Label(dto.getStat1Label());
            entity.setStat2Value(dto.getStat2Value());
            entity.setStat2Label(dto.getStat2Label());
            entity.setStat3Value(dto.getStat3Value());
            entity.setStat3Label(dto.getStat3Label());
            mallHomeDecoMapper.insert(entity);
        } else {
            entity.setSearchPlaceholder(dto.getSearchPlaceholder());
            entity.setStat1Value(dto.getStat1Value());
            entity.setStat1Label(dto.getStat1Label());
            entity.setStat2Value(dto.getStat2Value());
            entity.setStat2Label(dto.getStat2Label());
            entity.setStat3Value(dto.getStat3Value());
            entity.setStat3Label(dto.getStat3Label());
            mallHomeDecoMapper.updateById(entity);
        }
        return getHomeDeco();
    }

    @Override
    public MallSystemSettingVO getSystemSetting() {
        MallSystemSettingEntity settingEntity = mallSystemSettingMapper.selectOne(new LambdaQueryWrapper<MallSystemSettingEntity>()
                .orderByAsc(MallSystemSettingEntity::getId)
                .last("limit 1"));

        if (settingEntity == null) {
            return MallSystemSettingVO.builder()
                    .id(0L)
                    .mallName("元选商城")
                    .serviceMobile("400-100-2026")
                    .cancelMinutes("30")
                    .freeShippingAmount("99")
                    .build();
        }

        return MallSystemSettingVO.builder()
                .id(settingEntity.getId())
                .mallName(settingEntity.getMallName())
                .serviceMobile(settingEntity.getServiceMobile())
                .cancelMinutes(settingEntity.getCancelMinutes())
                .freeShippingAmount(settingEntity.getFreeShippingAmount())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallSystemSettingVO updateSystemSetting(SystemSettingUpdateDTO dto) {
        MallSystemSettingEntity settingEntity = mallSystemSettingMapper.selectOne(new LambdaQueryWrapper<MallSystemSettingEntity>()
                .orderByAsc(MallSystemSettingEntity::getId)
                .last("limit 1"));

        if (settingEntity == null) {
            settingEntity = new MallSystemSettingEntity();
        }

        settingEntity.setMallName(dto.getMallName());
        settingEntity.setServiceMobile(dto.getServiceMobile());
        settingEntity.setCancelMinutes(dto.getCancelMinutes());
        settingEntity.setFreeShippingAmount(dto.getFreeShippingAmount());

        if (settingEntity.getId() == null) {
            mallSystemSettingMapper.insert(settingEntity);
        } else {
            mallSystemSettingMapper.updateById(settingEntity);
        }

        return MallSystemSettingVO.builder()
                .id(settingEntity.getId())
                .mallName(settingEntity.getMallName())
                .serviceMobile(settingEntity.getServiceMobile())
                .cancelMinutes(settingEntity.getCancelMinutes())
                .freeShippingAmount(settingEntity.getFreeShippingAmount())
                .build();
    }
}
