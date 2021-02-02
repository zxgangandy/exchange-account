package com.zxgangandy.account.biz.service;

import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * <p>
 * 现货账户 服务类
 * </p>
 *
 * @author Andy
 * @since 2021-01-22
 */
public interface ISpotAccountFrozenService extends IService<SpotAccountFrozen> {

    Optional<SpotAccountFrozen> getUserOrderFrozen(long userId, long orderId, String bizType);

    /**
     * 根据用户uid、orderId、bizType更新订单冻结金额
     */
    boolean updateOrderFrozen(long userId, long orderId, String bizType, BigDecimal amount);

}
