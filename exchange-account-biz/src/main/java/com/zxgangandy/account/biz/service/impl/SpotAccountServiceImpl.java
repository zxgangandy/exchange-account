package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.entity.SpotAccountUnfrozen;
import com.zxgangandy.account.biz.mapper.SpotAccountMapper;
import com.zxgangandy.account.biz.service.*;
import io.jingwei.base.utils.exception.BizErr;
import io.jingwei.base.utils.exception.SysErr;
import io.jingwei.base.utils.tx.TxTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.zxgangandy.account.biz.exception.AccountErrCode.*;
import static com.zxgangandy.account.biz.support.AccountSupport.*;

/**
 * <p>
 *  账务系统核心实现类
 *  可以调用所有其它账务相关接口
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
@Service
@AllArgsConstructor
@Slf4j
public class SpotAccountServiceImpl extends ServiceImpl<SpotAccountMapper, SpotAccount> implements ISpotAccountService {
    private final TxTemplateService             txTemplateService;
    private final ISpotAccountLogService        spotAccountLogService;
    private final SpotAccountMapper             spotAccountMapper;
    private final ISpotAccountFrozenService     spotAccountFrozenService;
    private final ISpotAccountUnfrozenService   spotAccountUnfrozenService;

    @Override
    public Optional<SpotAccount> getAccount(long userId, String currency) {
        return lambdaQuery()
                .eq(SpotAccount::getUserId, userId)
                .eq(SpotAccount::getCurrency, currency).oneOpt();
    }

    @Override
    public List<SpotAccount> getAccountsByUserId(long userId) {
        return lambdaQuery()
                .eq(SpotAccount::getUserId, userId).list();
    }

    @Override
    public boolean hasEnoughBalance(long userId, String currency, BigDecimal amount) {
        Optional<SpotAccount> optAccount = getAccount(userId, currency);
        if (!optAccount.isPresent()) {
            log.warn("Account not found for user={}, currency={}", userId, currency);
            return false;
        }

        BigDecimal balance = optAccount.get().getBalance();
        log.info("User' balance=>user={}, currency={}, balance: balance={}, amount={}",
                userId, currency, balance, amount);

        return balance.compareTo(amount) >= 0;
    }

    @Override
    public SpotAccount getLockedAccount(long userId, String currency) {
        return lambdaQuery()
                .eq(SpotAccount::getUserId, userId)
                .eq(SpotAccount::getCurrency, currency)
                .last(" for update").one();
    }

    /**
     * @Description: 冻结用户资金
     * （不做查询的原因：1. App或者前端可以做余额判断； 2.大部分情况是余额足够下的单）
     *
     * @date 1/29/21
     * @Param reqBO:
     * @return: boolean
     */
    @Override
    public void frozen(FrozenReqBO reqBO) {
        log.info("frozen=>reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("frozen=>account={}", account);

            if (!updateAccountFrozen(reqBO)) {
                log.warn("Update account frozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(BALANCE_NOT_ENOUGH);
            } else {
                // If frozen success should update account vale to avoid query account twice
                updateFrozenAccountValue(account, reqBO);
            }

            if (!saveOrderFrozen(account.getAccountId(), reqBO)) {
                log.error("Save frozen order failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            if (!saveAccountFrozenLog(account, reqBO)) {
                log.error("Save frozen log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            log.info("Frozen account={} by order={} success", account, reqBO);
        });
    }

    @Override
    public void unfrozen(UnfrozenReqBO reqBO) {
        log.info("unfrozen=>reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("unfrozen=>account={}", account);

            if (!updateAccountUnfrozen(reqBO)) {
                log.warn("update account unfrozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(FROZEN_NOT_ENOUGH);
            } else {
                // If frozen success should update account vale to avoid query account twice
                updateUnfrozenAccountValue(account, reqBO);
            }

            if (!updateOrderUnfrozen(reqBO)) {
                log.warn("update order unfrozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(FROZEN_NOT_ENOUGH);
            }

            SpotAccountFrozen accountFrozen = getUserOrderFrozen(reqBO);
            if (!saveOrderUnfrozenDetail(accountFrozen, reqBO)) {
                log.error("save order unfrozen failed, frozen={}, reqBO={}", accountFrozen, reqBO);
                throw new SysErr();
            }

            if (!saveAccountUnfrozenLog(account, reqBO)) {
                log.error("Save unfrozen log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            log.info("unfrozen account={} by order={} success", account, reqBO);
        });
    }

    @Override
    public void transfer() {

    }

    private void updateFrozenAccountValue(SpotAccount account, FrozenReqBO reqBO) {
        account.setBalance(account.getBalance().subtract(reqBO.getAmount()));
        account.setFrozen(account.getFrozen().add(reqBO.getAmount()));
    }

    /**
     * @Description: 更新账户的冻结金额
     * @date 1/29/21
     * @Param reqBO:
     * @return: boolean
     */
    private boolean updateAccountFrozen(FrozenReqBO reqBO) {
        return SqlHelper.retBool(spotAccountMapper.frozenByUser(reqBO.getUserId(),
                reqBO.getCurrency(), reqBO.getAmount()));
    }

    /**
     * @Description: 更新账户的解冻金额
     * @date 1/29/21
     * @Param reqBO:
     * @return: boolean
     */
    private boolean updateAccountUnfrozen(UnfrozenReqBO reqBO) {
        return SqlHelper.retBool(spotAccountMapper.unfrozenByUser(reqBO.getUserId(),
                reqBO.getCurrency(), reqBO.getUnfrozenAmount()));
    }

    private void updateUnfrozenAccountValue(SpotAccount account, UnfrozenReqBO reqBO) {
        account.setBalance(account.getBalance().add(reqBO.getUnfrozenAmount()));
        account.setFrozen(account.getFrozen().subtract(reqBO.getUnfrozenAmount()));
    }

    /**
     * @Description: save订单冻结金额
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveOrderFrozen(long accountId, FrozenReqBO reqBO) {
        SpotAccountFrozen frozen = createOrderFrozen(accountId, reqBO);

        return spotAccountFrozenService.save(frozen);
    }

    /**
     * @Description: 更新订单当前剩余冻结金额
     * @date 1/22/21
     * @Param reqBO:
     * @return: void
     */
    private boolean updateOrderUnfrozen(UnfrozenReqBO reqBO) {
        return spotAccountFrozenService.updateOrderFrozen(
                reqBO.getUserId(),
                reqBO.getOrderId(),
                reqBO.getBizType(),
                reqBO.getUnfrozenAmount());
    }

    /**
     * @Description: 获取订单的历史冻结记录
     * @date 1/29/21
     * @Param reqBO:
     * @return: com.zxgangandy.account.biz.entity.SpotAccountFrozen
     */
    private SpotAccountFrozen getUserOrderFrozen(UnfrozenReqBO reqBO) {
        return spotAccountFrozenService.getUserOrderFrozen(reqBO.getUserId(), reqBO.getOrderId(), reqBO.getBizType()).get();
    }

    /**
     * @Description: 保存订单的解冻明细
     * @date 1/29/21
     * @Param accountFrozen:
     * @Param reqBO:
     * @return: boolean
     */
    private boolean saveOrderUnfrozenDetail(SpotAccountFrozen accountFrozen, UnfrozenReqBO reqBO) {
        SpotAccountUnfrozen unfrozen = createOrderUnfrozen(accountFrozen, reqBO);
        return spotAccountUnfrozenService.save(unfrozen);
    }

    /**
     * @Description: save账户的冻结日志
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveAccountFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        SpotAccountLog accountLog = createFrozenLog(account, reqBO);

        return spotAccountLogService.save(accountLog);
    }

    /**
     * @Description: 保存账户的解冻日志
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveAccountUnfrozenLog(SpotAccount account,  UnfrozenReqBO reqBO) {
        SpotAccountLog newAccountLog = createUnfrozenLog(account, reqBO);

        return  spotAccountLogService.save(newAccountLog);
    }

}
