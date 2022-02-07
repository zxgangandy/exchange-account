package com.zxgangandy.account.biz.service;

import com.zxgangandy.account.biz.bo.*;
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
     * @Description: 根据用户和币种创建账务账户
     * @date 4/16/21
     * @Param userId: 用户id
     * @Param currency: 币种
     * @return: boolean
     */
    boolean createAccount(long userId, String currency);

    /**
     * @Description: 根据用户列表和币种列表创建账务账户
     * @date 4/16/21
     * @Param uidList: 用户列表
     * @Param currencies: 币种列表
     * @return: void
     */
    void createAccount(List<Long> uidList, List<String> currencies);

    /**
     * @Description: 根据用户id列表和币种获取已经存在的用户列表
     * @date 4/16/21
     * @Param uidList: 用户列表
     * @Param currency:
     * @return: java.util.List<java.lang.Long> 已经存在的用户列表
     */
    List<Long> getExistAccounts(List<Long> uidList, String currency);

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
     * @Description: 入金
     * @date 3/30/21
     * @Param reqBO:
     * @return: void
     */
    void deposit(DepositReqBO reqBO) ;

    /**
     * @Description: 出金
     * @date 3/30/21
     * @Param reqBO:
     * @return: void
     */
    void withdraw(WithdrawReqBO reqBO);

    /**
     * @Description:  转账
     * @date 4/21/21
     * @Param req:
     * @return: void
     */
    void transfer(TransferReqBO req);

    /**
     * @Description: 解冻并出金
     * @date 4/21/21
     * @Param req:
     * @return: void
     */
    void unfrozenWithdraw(UnfrozenWithdrawReqBO req);

    /**
     * @Description: 解冻并转账
     * @date 4/21/21
     * @Param req:
     * @return: void
     */
    void unfrozenTransfer(UnfrozenTransferReqBO req);

}
