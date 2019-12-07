package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mapper.SpotAccountMapper;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxgangandy.base.exception.BizErr;
import com.zxgangandy.base.tx.TxCallback;
import com.zxgangandy.base.tx.TxTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.zxgangandy.account.base.exception.AccountErrCode.ACCOUNT_NOT_FOUND;
import static com.zxgangandy.account.base.exception.AccountErrCode.FROZEN_ACCOUNT_FAILED;

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
    public void frozen(long userId, long orderId, String currency, String bizType, BigDecimal amount, String remark) {
        txTemplateService.withTransaction(new TxCallback() {
            @Override
            public void execute() {
                SpotAccount account = getLockedAccount(userId, currency);

                if (account == null) {
                    log.warn("Account not exists, userId={}, currency={}", userId, currency);
                    throw new BizErr(ACCOUNT_NOT_FOUND);
                }

                updateSpotAccount(account, userId, currency, amount);
                saveAccountLog(account, userId, orderId, currency, bizType, amount, remark);
            }
        });
    }

    @Override
    public void unfrozen() {

    }

    @Override
    public void transfer() {

    }


    private void updateSpotAccount(SpotAccount account, long userId, String currency, BigDecimal amount) {
        SpotAccount set = new SpotAccount()
                .setBalance(account.getBalance().subtract(amount))
                .setFrozen(account.getFrozen().add(amount));
        SpotAccount where = new SpotAccount()
                .setUserId(userId)
                .setCurrency(currency);
        UpdateWrapper<SpotAccount> wrapper = new UpdateWrapper<>(where);

        if (!update(set, wrapper)) {
            throw new BizErr(FROZEN_ACCOUNT_FAILED);
        }
    }

    private void saveAccountLog(SpotAccount account, long userId, long orderId, String currency, String bizType,
                                BigDecimal amount , String remark) {
        SpotAccountLog accountLog = new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(userId)
                .setToUserId(userId)
                .setAmount(amount)
                .setBizType(bizType)
                .setOrderId(orderId)
                .setRemark(remark);
        spotAccountLogService.save(accountLog);
    }
}
