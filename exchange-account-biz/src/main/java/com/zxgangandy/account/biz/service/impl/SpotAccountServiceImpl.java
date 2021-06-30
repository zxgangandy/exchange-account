package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.zxgangandy.account.biz.bo.*;
import com.zxgangandy.account.biz.entity.*;
import com.zxgangandy.account.biz.mapper.SpotAccountMapper;
import com.zxgangandy.account.biz.service.*;
import io.jingwei.base.idgen.UidGenerator;
import io.jingwei.base.utils.exception.BizErr;
import io.jingwei.base.utils.exception.SysErr;
import io.jingwei.base.utils.tx.TxTemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
@SuppressWarnings("Duplicates")
public class SpotAccountServiceImpl extends ServiceImpl<SpotAccountMapper, SpotAccount> implements ISpotAccountService {
    private final TxTemplateService             txTemplateService;
    private final ISpotAccountLogService        spotAccountLogService;
    private final SpotAccountMapper             spotAccountMapper;
    private final ISpotAccountFrozenService     spotAccountFrozenService;
    private final ISpotAccountUnfrozenService   spotAccountUnfrozenService;
    private final ISpotAccountTradeService      spotAccountTradeService;
    private final UidGenerator                  defaultUidGenerator;

    @Override
    public boolean createAccount(long userId, String currency) {

        return save(new SpotAccount()
                .setAccountId(defaultUidGenerator.getUID())
                .setBalance(BigDecimal.ZERO)
                .setFrozen(BigDecimal.ZERO)
                .setUserId(userId)
                .setCurrency(currency));
    }

    @Override
    public void createAccount(List<Long> uids, List<String> currencies) {
        Assert.isTrue(uids.size() <= 20,
                "user id list size must not be bigger than 20");

        txTemplateService.doInTransaction(() ->
                uids.stream().forEach(uid -> {
                    try {
                        saveBatch(buildAccounts(uid, currencies));
                    } catch (DuplicateKeyException ex) {
                        throw new BizErr(USER_ACCOUNT_DUPLICATE);
                    }
                })
        );
    }

    @Override
    public List<Long> getExistAccounts(List<Long> uids, String currency) {
        List<SpotAccount> accounts = lambdaQuery()
                .in(SpotAccount::getUserId, uids)
                .eq(SpotAccount::getCurrency, currency).list();
        if (CollectionUtils.isNotEmpty(accounts)) {
            return accounts.stream()
                    .map(SpotAccount::getUserId)
                    .collect(Collectors.toList());
        }

        return Lists.newArrayList();
    }

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
     * （不做查询的原因：1. App或者前端可以做余额判断； 2.大部分情况是余额足够下的单； 3.更新操作会失败）
     *  另外需要注意： 一个订单号只能被冻结一次
     *
     * @date 1/29/21
     * @Param reqBO:
     * @return: boolean
     */
    @Override
    public void frozen(FrozenReqBO reqBO) {
        log.info("[[begin frozen]]: reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("frozen locked account={}", account);

            if (!updateAccountFrozen(reqBO)) {
                log.warn("update account frozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(BALANCE_NOT_ENOUGH);
            }

            try {
                if (!saveOrderFrozen(account, reqBO)) {
                    log.error("save frozen order failed, account={} by order={}", account, reqBO);
                    throw new SysErr();
                }
            } catch (DuplicateKeyException ex) {
                throw new BizErr(ORDER_DUPLICATE_FROZEN);
            }

            if (!saveAccountFrozenLog(account, reqBO)) {
                log.error("save frozen log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }
        });

        log.info("[[end frozen]]:  order={} success",  reqBO);
    }

    /**
     * @Description: 解冻用户资产
     * 解冻的业务类型必须和冻结的业务类型（bizType）一致或相同
     * @date 3/30/21
     * @Param reqBO:
     * @return: void
     */
    @Override
    public void unfrozen(UnfrozenReqBO reqBO) {
        log.info("[[begin unfrozen]]: reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("unfrozen=>account={}", account);

            if (account.getFrozen().compareTo(reqBO.getAmount()) == -1) {
                throw new BizErr(UNFROZEN_AMOUNT_INVALID);
            }

            if (!updateAccountUnfrozen(reqBO)) {
                log.warn("update account unfrozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(UNFROZEN_AMOUNT_INVALID);
            }

            Optional<SpotAccountFrozen> accountFrozenOpt = getUserOrderFrozen(reqBO);
            if (!accountFrozenOpt.isPresent()) {
                log.warn("frozen record not found, account={}, reqBO={}", account, reqBO);
                throw new BizErr(FROZEN_RECORD_NOT_FOUND);
            }

            SpotAccountFrozen accountFrozen = accountFrozenOpt.get();
            if (!updateOrderUnfrozen(reqBO)) {
                log.warn("update order unfrozen failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(UNFROZEN_AMOUNT_INVALID);
            }

            try {
                if (!saveOrderUnfrozenDetail(accountFrozen, reqBO)) {
                    log.error("save order unfrozen failed, frozen={}, reqBO={}", accountFrozen, reqBO);
                    throw new SysErr();
                }
            } catch (DuplicateKeyException ex) {
                throw new BizErr(ORDER_DUPLICATE_UNFROZEN);
            }

            if (!saveAccountUnfrozenLog(account, reqBO)) {
                log.error("save unfrozen log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            log.info("[[end unfrozen]]: account={} by order={} success", account, reqBO);
        });
    }

    /**
     * @Description: 用户某币种因某业务入金
     * @date 3/30/21
     * @Param reqBO:
     * @return: void
     */
    @Override
    public void deposit(DepositReqBO reqBO) {
        log.info("[[begin deposit]]: reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("deposit=>account={}", account);

            if (!updateAccountDeposit(reqBO)) {
                log.warn("update account deposit failed, account={}, reqBO={}", account, reqBO);
                throw new SysErr();
            }

            try {
                if (!saveOrderDeposit(account, reqBO)) {
                    log.error("save deposit order failed, account={} by order={}", account, reqBO);
                    throw new SysErr();
                }
            }  catch (DuplicateKeyException ex) {
                throw new BizErr(ORDER_DUPLICATE_DEPOSIT);
            }

            if (!saveAccountDepositLog(account, reqBO)) {
                log.error("save deposit log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            log.info("[[end deposit]]: account={} by order={} success", account, reqBO);
        });
    }

    /**
     * @Description: 用户某币种因某业务出金
     * @date 3/30/21
     * @Param reqBO:
     * @return: void
     */
    @Override
    public void withdraw(WithdrawReqBO reqBO) {
        log.info("[[begin withdraw]]: reqBO={}", reqBO);

        txTemplateService.doInTransaction(() -> {
            SpotAccount account = getLockedAccount(reqBO.getUserId(), reqBO.getCurrency());
            if (account == null) {
                throw new BizErr(ACCOUNT_NOT_FOUND);
            }

            log.info("withdraw=>account={}", account);

            if (!updateAccountWithdraw(reqBO)) {
                log.warn("update account Withdraw failed, account={}, reqBO={}", account, reqBO);
                throw new BizErr(BALANCE_NOT_ENOUGH);
            }

            try {
                if (!saveOrderWithdraw(account, reqBO)) {
                    log.error("save Withdraw order failed, account={} by order={}", account, reqBO);
                    throw new SysErr();
                }
            }  catch (DuplicateKeyException ex) {
                throw new BizErr(ORDER_DUPLICATE_WITHDRAW);
            }

            if (!saveAccountWithdrawLog(account, reqBO)) {
                log.error("save Withdraw log failed, account={} by order={}", account, reqBO);
                throw new SysErr();
            }

            log.info("[[end withdraw]]: account={} by order={} success", account, reqBO);
        });
    }

    @Override
    public void transfer(TransferReqBO req) {
        WithdrawReqBO withdraw = new WithdrawReqBO()
                .setUserId(req.getFromUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setAmount(req.getAmount());

        DepositReqBO deposit = new DepositReqBO()
                .setUserId(req.getToUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setAmount(req.getAmount());

        log.info("[[begin transfer]]: withdraw={}, deposit={}", withdraw, deposit);

        transfer(withdraw, deposit);
    }

    private void transfer(WithdrawReqBO withdraw, DepositReqBO deposit) {
        log.info("[[begin transfer]]: withdraw={}, deposit={}", withdraw, deposit);

        txTemplateService.doInTransaction(() -> {
            sortAndLockTransferAccounts(withdraw,deposit);
            withdraw(withdraw);
            deposit(deposit);
        });
    }

    @Override
    public void unfrozenWithdraw(UnfrozenWithdrawReqBO req) {
        UnfrozenReqBO unfrozenReq = new UnfrozenReqBO()
                .setUserId(req.getUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setBizId(req.getBizId())
                .setAmount(req.getAmount());

        WithdrawReqBO withdraw = new WithdrawReqBO()
                .setUserId(req.getUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setAmount(req.getAmount());

        unfrozenWithdraw(unfrozenReq, withdraw);
    }

    @Override
    public void unfrozenTransfer(UnfrozenTransferReqBO req) {
        UnfrozenReqBO unfrozenReq = new UnfrozenReqBO()
                .setUserId(req.getFromUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setBizId(req.getBizId())
                .setAmount(req.getAmount());

        WithdrawReqBO withdraw = new WithdrawReqBO()
                .setUserId(req.getFromUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setAmount(req.getAmount());

        DepositReqBO deposit = new DepositReqBO()
                .setUserId(req.getToUserId())
                .setOrderId(req.getOrderId())
                .setBizType(req.getBizType())
                .setCurrency(req.getCurrency())
                .setAmount(req.getAmount());

        txTemplateService.doInTransaction(() -> {
            sortAndLockTransferAccounts(withdraw,deposit);
            unfrozenWithdraw(unfrozenReq, withdraw);
            deposit(deposit);
        });
    }


    private void unfrozenWithdraw(UnfrozenReqBO unfrozenReq, WithdrawReqBO withdraw) {
        txTemplateService.doInTransaction(() -> {
            unfrozen(unfrozenReq);
            withdraw(withdraw);
        });
    }

    /**
     * @Description: 根据单个用户id和币种来构建账号初始化列表
     * @date 4/5/21
     * @Param uid:
     * @Param currencies:
     * @return: java.util.List<com.zxgangandy.account.biz.entity.SpotAccount>
     */
    private List<SpotAccount> buildAccounts(Long uid, List<String> currencies) {
        List<SpotAccount> accounts = currencies.stream()
                .map(currency -> new SpotAccount()
                        .setAccountId(defaultUidGenerator.getUID())
                        .setBalance(BigDecimal.ZERO)
                        .setFrozen(BigDecimal.ZERO)
                        .setUserId(uid)
                        .setCurrency(currency)
                )
                .collect(Collectors.toList());

        return accounts;
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
                reqBO.getCurrency(), reqBO.getAmount()));
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
                reqBO.getAmount());
    }

    /**
     * @Description: 获取订单的历史冻结记录
     * @date 1/29/21
     * @Param reqBO:
     * @return: com.zxgangandy.account.biz.entity.SpotAccountFrozen
     */
    private Optional<SpotAccountFrozen> getUserOrderFrozen(UnfrozenReqBO reqBO) {
        return spotAccountFrozenService.getUserOrderFrozen(reqBO.getUserId(), reqBO.getOrderId(), reqBO.getBizType());
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

    private boolean updateAccountDeposit(DepositReqBO reqBO) {
        return SqlHelper.retBool(spotAccountMapper.depositByUser(reqBO.getUserId(),
                reqBO.getCurrency(), reqBO.getAmount()));
    }

    /**
     * @Description: save订单冻结金额
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveOrderDeposit(SpotAccount account, DepositReqBO reqBO) {
        SpotAccountTrade deposit = createOrderDeposit(account, reqBO);

        return spotAccountTradeService.save(deposit);
    }

    private boolean updateAccountWithdraw(WithdrawReqBO reqBO) {
        return SqlHelper.retBool(spotAccountMapper.depositByUser(reqBO.getUserId(),
                reqBO.getCurrency(), reqBO.getAmount()));
    }

    /**
     * @Description: save订单冻结金额
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveOrderWithdraw(SpotAccount account, WithdrawReqBO reqBO) {
        SpotAccountTrade withdraw = createOrderWithdraw(account, reqBO);

        return spotAccountTradeService.save(withdraw);
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

    /**
     * @Description: save账户的冻结日志
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveAccountDepositLog(SpotAccount account, DepositReqBO reqBO) {
        SpotAccountLog accountLog = createDepositLog(account, reqBO);

        return spotAccountLogService.save(accountLog);
    }

    /**
     * @Description: save账户的冻结日志
     * @date 1/22/21
     * @Param account:
     * @Param reqBO:
     * @return: void
     */
    private boolean saveAccountWithdrawLog(SpotAccount account, WithdrawReqBO reqBO) {
        SpotAccountLog accountLog = createWithdrawLog(account, reqBO);

        return spotAccountLogService.save(accountLog);
    }


    private void sortAndLockTransferAccounts(WithdrawReqBO withdraw, DepositReqBO deposit) {
        List<SpotAccount> accountInfos = Lists.newArrayList();
        SpotAccount withdrawAccount = getAccount(withdraw.getUserId(), withdraw.getCurrency())
                .orElseThrow(()->new BizErr(ACCOUNT_NOT_FOUND));
        accountInfos.add(withdrawAccount);
        SpotAccount depositAccount = getAccount(deposit.getUserId(), deposit.getCurrency())
                .orElseThrow(()->new BizErr(ACCOUNT_NOT_FOUND));
        accountInfos.add(depositAccount);

        accountInfos.stream()
                .sorted(Comparator.comparing(SpotAccount::getId))
                .map((accountInfo) ->
                        getLockedAccount(accountInfo.getUserId(), accountInfo.getCurrency())
                );
    }

}
