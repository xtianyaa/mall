package com.yuan.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuan.mall.dto.AfterSaleApplyDTO;
import com.yuan.mall.dto.AfterSaleHandleDTO;
import com.yuan.mall.dto.AfterSaleQueryDTO;
import com.yuan.mall.entity.MallAfterSaleItemEntity;
import com.yuan.mall.entity.MallAfterSaleLogEntity;
import com.yuan.mall.entity.MallAfterSaleOrderEntity;
import com.yuan.mall.entity.MallOrderEntity;
import com.yuan.mall.entity.MallOrderItemEntity;
import com.yuan.mall.entity.MallUserEntity;
import com.yuan.mall.entity.MallUserCouponEntity;
import com.yuan.mall.mapper.MallAfterSaleItemMapper;
import com.yuan.mall.mapper.MallAfterSaleLogMapper;
import com.yuan.mall.mapper.MallAfterSaleOrderMapper;
import com.yuan.mall.mapper.MallOrderItemMapper;
import com.yuan.mall.mapper.MallOrderMapper;
import com.yuan.mall.mapper.MallUserMapper;
import com.yuan.mall.mapper.MallUserCouponMapper;
import com.yuan.mall.service.AfterSaleService;
import com.yuan.mall.vo.AfterSaleLogVO;
import com.yuan.mall.vo.AfterSaleVO;
import com.yuan.mall.vo.MiniOrderItemVO;
import com.yuan.mall.vo.PageResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 售后服务实现
 */
@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MallAfterSaleOrderMapper afterSaleOrderMapper;
    private final MallAfterSaleItemMapper afterSaleItemMapper;
    private final MallAfterSaleLogMapper afterSaleLogMapper;
    private final MallOrderMapper mallOrderMapper;
    private final MallOrderItemMapper mallOrderItemMapper;
    private final MallUserMapper mallUserMapper;
    private final MallUserCouponMapper mallUserCouponMapper;

    public AfterSaleServiceImpl(MallAfterSaleOrderMapper afterSaleOrderMapper,
                                MallAfterSaleItemMapper afterSaleItemMapper,
                                MallAfterSaleLogMapper afterSaleLogMapper,
                                MallOrderMapper mallOrderMapper,
                                MallOrderItemMapper mallOrderItemMapper,
                                MallUserMapper mallUserMapper,
                                MallUserCouponMapper mallUserCouponMapper) {
        this.afterSaleOrderMapper = afterSaleOrderMapper;
        this.afterSaleItemMapper = afterSaleItemMapper;
        this.afterSaleLogMapper = afterSaleLogMapper;
        this.mallOrderMapper = mallOrderMapper;
        this.mallOrderItemMapper = mallOrderItemMapper;
        this.mallUserMapper = mallUserMapper;
        this.mallUserCouponMapper = mallUserCouponMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AfterSaleVO createAfterSale(AfterSaleApplyDTO dto) {
        // orderId 为订单号（如 MO1773409046226000），按 orderNo 查询
        MallOrderEntity orderEntity = mallOrderMapper.selectOne(
                new LambdaQueryWrapper<MallOrderEntity>()
                        .eq(MallOrderEntity::getOrderNo, dto.getOrderId())
                        .last("limit 1"));
        if (orderEntity == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if (!Objects.equals(orderEntity.getUserId(), dto.getUserId())) {
            throw new IllegalArgumentException("订单不属于当前用户");
        }
        Long orderId = orderEntity.getId();

        MallOrderItemEntity orderItem = mallOrderItemMapper.selectById(dto.getOrderItemId());
        if (orderItem == null || !Objects.equals(orderItem.getOrderId(), orderId)) {
            throw new IllegalArgumentException("订单商品不存在");
        }

        // TODO 可根据订单状态和时间做更细的限制

        MallAfterSaleOrderEntity afterSaleOrder = new MallAfterSaleOrderEntity();
        afterSaleOrder.setOrderId(orderId);
        afterSaleOrder.setUserId(dto.getUserId());
        afterSaleOrder.setType(dto.getType());
        afterSaleOrder.setStatus("APPLIED");
        afterSaleOrder.setReason(dto.getReason());
        BigDecimal applyAmount = orderItem.getGoodsPrice()
                .multiply(BigDecimal.valueOf(dto.getQuantity()));
        afterSaleOrder.setApplyAmount(applyAmount);
        afterSaleOrder.setActualAmount(applyAmount);
        afterSaleOrder.setApplyTime(LocalDateTime.now());
        afterSaleOrder.setUpdateTime(LocalDateTime.now());
        afterSaleOrderMapper.insert(afterSaleOrder);

        MallAfterSaleItemEntity itemEntity = new MallAfterSaleItemEntity();
        itemEntity.setAfterSaleId(afterSaleOrder.getId());
        itemEntity.setOrderItemId(orderItem.getId());
        itemEntity.setGoodsId(orderItem.getGoodsId());
        itemEntity.setApplyQuantity(dto.getQuantity());
        itemEntity.setApprovedQuantity(dto.getQuantity());
        itemEntity.setCreateTime(LocalDateTime.now());
        itemEntity.setUpdateTime(LocalDateTime.now());
        afterSaleItemMapper.insert(itemEntity);

        MallAfterSaleLogEntity logEntity = new MallAfterSaleLogEntity();
        logEntity.setAfterSaleId(afterSaleOrder.getId());
        logEntity.setAction("APPLY");
        logEntity.setOperatorId(dto.getUserId());
        logEntity.setOperatorRole("USER");
        logEntity.setRemark(dto.getReason());
        logEntity.setCreateTime(LocalDateTime.now());
        afterSaleLogMapper.insert(logEntity);

        return buildAfterSaleVO(afterSaleOrder);
    }

    @Override
    public AfterSaleVO getAfterSaleDetail(Long id) {
        MallAfterSaleOrderEntity entity = afterSaleOrderMapper.selectById(id);
        if (entity == null) {
            throw new IllegalArgumentException("售后单不存在");
        }
        return buildAfterSaleVO(entity);
    }

    @Override
    public PageResultVO<AfterSaleVO> pageAfterSale(AfterSaleQueryDTO dto) {
        int pageNum = dto.getPageNum() == null || dto.getPageNum() < 1 ? 1 : dto.getPageNum();
        int pageSize = dto.getPageSize() == null || dto.getPageSize() < 1 ? 10 : dto.getPageSize();

        LambdaQueryWrapper<MallAfterSaleOrderEntity> wrapper = new LambdaQueryWrapper<>();
        if (dto.getType() != null && !dto.getType().isBlank()) {
            wrapper.eq(MallAfterSaleOrderEntity::getType, dto.getType());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            wrapper.eq(MallAfterSaleOrderEntity::getStatus, dto.getStatus());
        }
        if (dto.getApplyTimeStart() != null) {
            wrapper.ge(MallAfterSaleOrderEntity::getApplyTime, dto.getApplyTimeStart());
        }
        if (dto.getApplyTimeEnd() != null) {
            wrapper.le(MallAfterSaleOrderEntity::getApplyTime, dto.getApplyTimeEnd());
        }
        wrapper.orderByDesc(MallAfterSaleOrderEntity::getId);

        IPage<MallAfterSaleOrderEntity> page = afterSaleOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<AfterSaleVO> records = page.getRecords().stream()
                .map(this::buildAfterSaleVO)
                .toList();

        return PageResultVO.<AfterSaleVO>builder()
                .list(records)
                .total(page.getTotal())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleAfterSale(AfterSaleHandleDTO dto) {
        MallAfterSaleOrderEntity entity = afterSaleOrderMapper.selectById(dto.getAfterSaleId());
        if (entity == null) {
            throw new IllegalArgumentException("售后单不存在");
        }
        String action = dto.getAction();
        if ("APPROVE".equalsIgnoreCase(action)) {
            entity.setStatus("APPROVED");
            if (dto.getActualAmount() != null) {
                entity.setActualAmount(dto.getActualAmount());
            } else {
                entity.setActualAmount(entity.getApplyAmount());
            }
        } else if ("REJECT".equalsIgnoreCase(action)) {
            entity.setStatus("REJECTED");
        } else if ("COMPLETE".equalsIgnoreCase(action)) {
            entity.setStatus("COMPLETED");
        } else {
            throw new IllegalArgumentException("不支持的操作类型");
        }
        entity.setUpdateTime(LocalDateTime.now());
        afterSaleOrderMapper.updateById(entity);

        MallAfterSaleLogEntity logEntity = new MallAfterSaleLogEntity();
        logEntity.setAfterSaleId(entity.getId());
        logEntity.setAction(action.toUpperCase());
        logEntity.setOperatorId(dto.getOperatorId());
        logEntity.setOperatorRole("ADMIN");
        logEntity.setRemark(dto.getRemark());
        logEntity.setCreateTime(LocalDateTime.now());
        afterSaleLogMapper.insert(logEntity);

        // 仅退款审核通过或完成后，将订单状态从「待发货/配送中」更新为「已取消」，并释放该订单使用的优惠券
        if ("REFUND_ONLY".equals(entity.getType()) && ("APPROVE".equalsIgnoreCase(action) || "COMPLETE".equalsIgnoreCase(action))) {
            MallOrderEntity order = mallOrderMapper.selectById(entity.getOrderId());
            int orderUpdated = mallOrderMapper.update(null, new LambdaUpdateWrapper<MallOrderEntity>()
                    .eq(MallOrderEntity::getId, entity.getOrderId())
                    .in(MallOrderEntity::getStatus, "待发货", "配送中")
                    .set(MallOrderEntity::getStatus, "已取消")
                    .set(MallOrderEntity::getCancelTime, LocalDateTime.now()));
            if (orderUpdated > 0 && order != null && order.getCouponId() != null) {
                LambdaUpdateWrapper<MallUserCouponEntity> uw = new LambdaUpdateWrapper<MallUserCouponEntity>()
                        .eq(MallUserCouponEntity::getUserId, order.getUserId())
                        .eq(MallUserCouponEntity::getCouponId, order.getCouponId())
                        .eq(MallUserCouponEntity::getStatus, "used")
                        .set(MallUserCouponEntity::getStatus, "unused");
                mallUserCouponMapper.update(null, uw.last("LIMIT 1"));
            }
        }

        return Boolean.TRUE;
    }

    private AfterSaleVO buildAfterSaleVO(MallAfterSaleOrderEntity entity) {
        MallOrderEntity orderEntity = mallOrderMapper.selectById(entity.getOrderId());
        if (orderEntity == null) {
            return null;
        }
        MallUserEntity userEntity = mallUserMapper.selectById(orderEntity.getUserId());

        List<MallAfterSaleItemEntity> itemEntities = afterSaleItemMapper.selectList(
                new LambdaQueryWrapper<MallAfterSaleItemEntity>()
                        .eq(MallAfterSaleItemEntity::getAfterSaleId, entity.getId()));
        if (itemEntities == null) {
            itemEntities = Collections.emptyList();
        }
        List<Long> orderItemIds = itemEntities.stream()
                .map(MallAfterSaleItemEntity::getOrderItemId)
                .toList();
        List<MallOrderItemEntity> orderItems = orderItemIds.isEmpty()
                ? Collections.emptyList()
                : mallOrderItemMapper.selectBatchIds(orderItemIds);

        List<MiniOrderItemVO> itemVOList = orderItems.stream()
                .map(item -> MiniOrderItemVO.builder()
                        .goodsId(item.getGoodsId())
                        .name(item.getGoodsName())
                        .price(item.getGoodsPrice())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        List<MallAfterSaleLogEntity> logEntities = afterSaleLogMapper.selectList(
                new LambdaQueryWrapper<MallAfterSaleLogEntity>()
                        .eq(MallAfterSaleLogEntity::getAfterSaleId, entity.getId())
                        .orderByAsc(MallAfterSaleLogEntity::getId));
        List<AfterSaleLogVO> logVOList = logEntities.stream()
                .map(log -> AfterSaleLogVO.builder()
                        .action(log.getAction())
                        .remark(log.getRemark())
                        .operatorRole(log.getOperatorRole())
                        .createTime(log.getCreateTime() == null ? null : log.getCreateTime().format(DATE_TIME_FORMATTER))
                        .build())
                .toList();

        return AfterSaleVO.builder()
                .id(entity.getId())
                .orderNo(orderEntity.getOrderNo())
                .orderId(String.valueOf(entity.getOrderId()))
                .userId(orderEntity.getUserId())
                .userName(userEntity != null ? userEntity.getNickName() : null)
                .type(entity.getType())
                .status(entity.getStatus())
                .applyAmount(entity.getApplyAmount())
                .actualAmount(entity.getActualAmount())
                .reason(entity.getReason())
                .description(null)
                .applyTime(entity.getApplyTime() == null ? null : entity.getApplyTime().format(DATE_TIME_FORMATTER))
                .updateTime(entity.getUpdateTime() == null ? null : entity.getUpdateTime().format(DATE_TIME_FORMATTER))
                .itemList(itemVOList)
                .logList(logVOList)
                .build();
    }
}

