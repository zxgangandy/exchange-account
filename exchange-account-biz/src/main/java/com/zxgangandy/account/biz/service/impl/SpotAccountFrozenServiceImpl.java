package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.zxgangandy.account.biz.mapper.SpotAccountFrozenMapper;
import com.zxgangandy.account.biz.service.ISpotAccountFrozenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * <p>
 * 现货账户 服务实现类
 * </p>
 *
 * @author Andy
 * @since 2021-01-22
 */
@Service
@AllArgsConstructor
@Slf4j
public class SpotAccountFrozenServiceImpl extends ServiceImpl<SpotAccountFrozenMapper, SpotAccountFrozen> implements ISpotAccountFrozenService {

    private final SpotAccountFrozenMapper spotAccountFrozenMapper;
    @Override
    public Optional<SpotAccountFrozen> getUserOrderFrozen(long userId, long orderId, String bizType) {
        return lambdaQuery()
                .eq(SpotAccountFrozen::getUserId, userId)
                .eq(SpotAccountFrozen::getOrderId, orderId)
                .eq(SpotAccountFrozen::getBizType, bizType).oneOpt();
    }

    @Override
    public boolean updateOrderFrozen(long orderId, String bizType, BigDecimal amount) {
        return SqlHelper.retBool(spotAccountFrozenMapper.updateOrderFrozen(orderId, bizType, amount));
    }
}
