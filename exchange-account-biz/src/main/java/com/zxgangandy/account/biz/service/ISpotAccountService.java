package com.zxgangandy.account.biz.service;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  整个账务系统的核心接口
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
public interface ISpotAccountService extends IService<SpotAccount> {

    /**
     *  根据用户id和币种获取用户账户信息
     */
    Optional<SpotAccount> getAccount(long userId, String currency);

    /**
     *  根据用户id获取用户账户信息列表
     */
    List<SpotAccount> getAccountsByUserId(long userId);

    /**
     *  判断用户账户是否足额
     */
    boolean hasEnoughBalance(long userId, String currency, BigDecimal amount);

    /**
     *  根据用户id和币种锁定账户并获取用户账户信息
     */
    SpotAccount getLockedAccount(long userId, String currency);

    /**
     *  冻结用户部分资产
     */
    void frozen(FrozenReqBO frozenAccountReqBO);

    /**
     *  根据订单信息解冻账号
     */
    void unfrozen(UnfrozenReqBO reqBO);


    /**
     *  转账
     */
    void transfer();

}
