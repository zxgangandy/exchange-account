package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mapper.SpotAccountMapper;
import com.zxgangandy.account.biz.service.ISpotAccountFrozenService;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import io.jingwei.base.utils.exception.BizErr;
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
    private final TxTemplateService         txTemplateService;
    private final ISpotAccountLogService    spotAccountLogService;
    private final SpotAccountMapper         spotAccountMapper;
    private final ISpotAccountFrozenService spotAccountFrozenService;

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

    @Override
    public boolean frozen(FrozenReqBO reqBO) {
        if(!hasEnoughBalance(reqBO.getUserId(), reqBO.getCurrency(), reqBO.getAmount())) {
            return false;
        }

        Optional<SpotAccountFrozen> optFrozen = spotAccountFrozenService.getUserOrderFrozen(
                reqBO.getUserId(), reqBO.getOrderId(), reqBO.getBizType());
        if (optFrozen.isPresent()) {
            log.warn("Frozen already present: user={} order={}, bizType={}",
                    reqBO.getUserId(),
                    reqBO.getOrderId(),
                    reqBO.getBizType());
            return true;
        }

        return txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());

            return updateAccountFrozen(reqBO)
                    & saveOrderFrozen(account, reqBO)
                    & saveFrozenLog(account, reqBO);
        });
    }

    @Override
    public boolean unfrozen(UnfrozenReqBO reqBO) {

        checkUnfrozenValid(reqBO);

        return txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            updateAccountUnfrozen(reqBO.getUserId(), reqBO.getCurrency(), reqBO.getUnfrozenAmount());
            updateOrderFrozen(reqBO);
            saveUnfrozenLog(account, reqBO);

            return true;
        });
    }

    @Override
    public void transfer() {

    }


    private boolean updateAccountFrozen(FrozenReqBO reqBO) {
        return SqlHelper.retBool(spotAccountMapper.frozenByUser(reqBO.getUserId(), reqBO.getCurrency(), reqBO.getAmount()));
    }

    private void updateAccountUnfrozen(long userId, String currency, BigDecimal amount) {
        if (!SqlHelper.retBool(spotAccountMapper.unfrozenByUser(userId, currency, amount))) {
            log.warn("update user={}, currency={} account unfrozen failed", userId, currency);
            throw new BizErr(UNFROZEN_ACCOUNT_FAILED);
        }
    }

    /***
     * @Description: check订单的历史冻结是否存在并且解冻金额不能大于剩余冻结金额
     * @date 1/22/21
     * @Param reqBO: 解冻请求参数
     * @return: void
     */
    private void checkUnfrozenValid(UnfrozenReqBO reqBO) {
        long userId    = reqBO.getUserId();
        long orderId   = reqBO.getOrderId();
        String bizType = reqBO.getBizType();

        Optional<SpotAccount> optAccount = getAccount(userId, reqBO.getCurrency());
        if (!optAccount.isPresent()) {
            log.warn("Account not exists, userId={}, currency={}", userId, reqBO.getCurrency());
            throw new BizErr(ACCOUNT_NOT_FOUND);
        }

        Optional<SpotAccountFrozen> opt = spotAccountFrozenService.getUserOrderFrozen(userId, orderId, bizType);
        if (!opt.isPresent()) {
            log.warn("There's no frozen record for user={}, order={}, bizType={}", userId, orderId, bizType);
            throw new BizErr(FROZEN_RECORD_NOT_FOUND);
        }

        SpotAccountFrozen frozenHistory = opt.get();
        if (reqBO.getUnfrozenAmount().compareTo(frozenHistory.getLeftFrozen()) > 0) {
            log.warn("User={}, order={}, bizType={}, unfrozen amount={} bigger than order left frozen amount={}",
                    userId, orderId, bizType, reqBO.getUnfrozenAmount(), frozenHistory.getLeftFrozen());
            throw new BizErr(UNFROZEN_AMOUNT_INVALID);
        }
    }

    /**
     * @Description: save订单冻结金额
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveOrderFrozen(SpotAccount account, FrozenReqBO reqBO) {
        SpotAccountFrozen frozen = createOrderFrozen(account, reqBO);

        return spotAccountFrozenService.save(frozen);
    }

    /**
     * @Description: 更新订单冻结金额
     * @date 1/22/21
     * @Param reqBO:
     * @return: void
     */
    private boolean updateOrderFrozen(UnfrozenReqBO reqBO) {
        return spotAccountFrozenService.updateOrderFrozen(
                reqBO.getUserId(),
                reqBO.getOrderId(),
                reqBO.getBizType(),
                reqBO.getUnfrozenAmount());
    }

    /**
     * @Description: save冻结log
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        SpotAccountLog accountLog = createFrozenLog(account, reqBO);

        return spotAccountLogService.save(accountLog);
    }

    /**
     * @Description: save解冻log
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private void saveUnfrozenLog(SpotAccount account,  UnfrozenReqBO reqBO) {
        SpotAccountLog newAccountLog = createUnfrozenLog(account, reqBO);

        if (!spotAccountLogService.save(newAccountLog)) {
            log.warn("save user={} account log failed", account);
            throw new BizErr(SAVE_UNFROZEN_LOG_FAILED);
        }
    }

}
