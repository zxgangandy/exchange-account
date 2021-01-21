package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mapper.SpotAccountMapper;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import com.zxgangandy.base.utils.exception.BizErr;
import com.zxgangandy.base.utils.tx.TxCallback;
import com.zxgangandy.base.utils.tx.TxTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.zxgangandy.account.biz.exception.AccountErrCode.*;
import static com.zxgangandy.account.biz.support.AccountSupport.createFrozenLog;
import static com.zxgangandy.account.biz.support.AccountSupport.createUnfrozenLog;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
@Service
@AllArgsConstructor
@Slf4j
public class SpotAccountServiceImpl extends ServiceImpl<SpotAccountMapper, SpotAccount> implements ISpotAccountService {
    private final TxTemplateService      txTemplateService;
    private final ISpotAccountLogService spotAccountLogService;
    private final SpotAccountMapper      spotAccountMapper;

    @Override
    public SpotAccount getAccount(long userId, String currency) {
        QueryWrapper<SpotAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(SpotAccount::getUserId, userId)
                .eq(SpotAccount::getCurrency, currency);
        return  getOne(wrapper);

    }

    @Override
    public List<SpotAccount> getAccountsByUserId(long userId) {
        QueryWrapper<SpotAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SpotAccount::getUserId, userId);
        return  list(wrapper);
    }

    @Override
    public boolean hasEnoughBalance(long userId, String currency, BigDecimal amount) {
        SpotAccount account = getAccount(userId, currency);
        if (account == null) {
            log.warn("Account not exists, userId={}, currency={}", userId, currency);
            return false;
        }

        return account.getBalance().compareTo(amount) >= 0;
    }

    @Override
    public SpotAccount getLockedAccount(long userId, String currency) {
        QueryWrapper<SpotAccount> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(SpotAccount::getUserId, userId)
                .eq(SpotAccount::getCurrency, currency)
                .last(" for update");
        return  getOne(wrapper);
    }

    @Override
    public void frozen(FrozenReqBO reqBO) {
        txTemplateService.withTransaction(new TxCallback() {
            @Override
            public void execute() {
                frozenAccount(reqBO.getUserId(), reqBO.getCurrency(), reqBO.getAmount());
                saveFrozenLog(getAccount(reqBO.getUserId(), reqBO.getCurrency()),reqBO);
            }
        });
    }

    @Override
    public void unfrozen(UnfrozenReqBO reqBO) {
        Long orderId   = reqBO.getOrderId();
        String bizType = reqBO.getBizType();
        SpotAccountLog accountLog = spotAccountLogService.getAccountLog(orderId, bizType);
        if (accountLog == null) {
            log.warn("There's no frozen account log for order={}", orderId);
            throw new BizErr(UNFROZEN_ACCOUNT_FAILED);
        }

        txTemplateService.withTransaction(new TxCallback() {
            @Override
            public void execute() {
                unfrozenAccount(reqBO.getUserId(), reqBO.getCurrency(), accountLog.getAmount());
                saveUnfrozenLog(getAccount(reqBO.getUserId(), reqBO.getCurrency()), accountLog, reqBO);
            }
        });
    }

    @Override
    public void transfer() {

    }


    private void frozenAccount(long userId, String currency, BigDecimal amount) {
        if (!SqlHelper.retBool(spotAccountMapper.frozenByUser(userId, currency, amount))) {
            log.warn("update user={} spot account with currency={} failed", userId, currency);
            throw new BizErr(FROZEN_ACCOUNT_FAILED);
        }
    }

    private void unfrozenAccount(long userId, String currency, BigDecimal amount) {
        if (!SqlHelper.retBool(spotAccountMapper.unfrozenByUser(userId, currency, amount))) {
            log.warn("update user={} spot account with currency={} failed", userId, currency);
            throw new BizErr(FROZEN_ACCOUNT_FAILED);
        }
    }

    private void saveFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        if (account == null) {
            throw new BizErr(ACCOUNT_NOT_FOUND);
        }

        SpotAccountLog accountLog = createFrozenLog(account, reqBO);

        if (!spotAccountLogService.save(accountLog)) {
            log.warn("save user={} account log failed", account);
            throw new BizErr(FROZEN_ACCOUNT_FAILED);
        }
    }

    private void saveUnfrozenLog(SpotAccount account, SpotAccountLog oldAccountLog, UnfrozenReqBO reqBO) {
        if (account == null) {
            throw new BizErr(ACCOUNT_NOT_FOUND);
        }

        SpotAccountLog newAccountLog = createUnfrozenLog(account, oldAccountLog, reqBO);

        if (!spotAccountLogService.save(newAccountLog)) {
            log.warn("save user={} account log failed", account);
            throw new BizErr(UNFROZEN_ACCOUNT_FAILED);
        }
    }



}
