package com.zxgangandy.account.biz.support;

import com.zxgangandy.account.biz.bo.DepositReqBO;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.bo.WithdrawReqBO;
import com.zxgangandy.account.biz.entity.*;

public class AccountSupport {

    public static SpotAccountFrozen createOrderFrozen(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountFrozen()
                .setUserId(reqBO.getUserId())
                .setOrderId(reqBO.getOrderId())
                .setBizType(reqBO.getBizType())
                .setCurrency(reqBO.getCurrency())
                .setOriginFrozen(reqBO.getAmount())
                .setLeftFrozen(reqBO.getAmount())
                .setAccountId(account.getAccountId());
    }

    public static SpotAccountLog createFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBalance(account.getBalance().subtract(reqBO.getAmount()))
                .setFrozen(account.getFrozen().add(reqBO.getAmount()))
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId())
                .setCurrency(reqBO.getCurrency());
    }

    public static SpotAccountLog createUnfrozenLog(SpotAccount account, UnfrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setCurrency(reqBO.getCurrency())
                .setAmount(reqBO.getUnfrozenAmount())
                .setBalance(account.getBalance().add(reqBO.getUnfrozenAmount()))
                .setFrozen(account.getFrozen().subtract(reqBO.getUnfrozenAmount()))
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId());
    }

    public static SpotAccountUnfrozen createOrderUnfrozen(SpotAccountFrozen accountFrozen, UnfrozenReqBO reqBO) {
        return new SpotAccountUnfrozen()
                .setBizId(reqBO.getBizId())
                .setBizType(reqBO.getBizType())
                .setUnfrozen(reqBO.getUnfrozenAmount())
                .setUserId(reqBO.getUserId())
                .setCurrency(reqBO.getCurrency())
                .setOrderId(reqBO.getOrderId())
                .setAccountId(accountFrozen.getAccountId())
                .setOriginFrozen(accountFrozen.getOriginFrozen())
                .setLeftFrozen(accountFrozen.getLeftFrozen());

    }

    public static SpotAccountDeposit createOrderDeposit(SpotAccount account, DepositReqBO reqBO) {
        return new SpotAccountDeposit()
                .setUserId(reqBO.getUserId())
                .setOrderId(reqBO.getOrderId())
                .setBizType(reqBO.getBizType())
                .setCurrency(reqBO.getCurrency())
                .setBeforeDeposit(account.getBalance())
                .setDeposit(reqBO.getAmount())
                .setAfterDeposit(account.getBalance().add(reqBO.getAmount()));
    }

    public static SpotAccountLog createDepositLog(SpotAccount account, DepositReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBalance(account.getBalance().add(reqBO.getAmount()))
                .setFrozen(account.getFrozen())
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId())
                .setCurrency(reqBO.getCurrency());
    }

    public static SpotAccountWithdraw createOrderWithdraw(SpotAccount account, WithdrawReqBO reqBO) {
        return new SpotAccountWithdraw()
                .setUserId(reqBO.getUserId())
                .setOrderId(reqBO.getOrderId())
                .setBizType(reqBO.getBizType())
                .setCurrency(reqBO.getCurrency())
                .setBeforeWithdraw(account.getBalance())
                .setWithdraw(reqBO.getAmount())
                .setAfterWithdraw(account.getBalance().subtract(reqBO.getAmount()));
    }

    public static SpotAccountLog createWithdrawLog(SpotAccount account, WithdrawReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBalance(account.getBalance().subtract(reqBO.getAmount()))
                .setFrozen(account.getFrozen())
                .setBizType(reqBO.getBizType())
                .setOrderId(reqBO.getOrderId())
                .setCurrency(reqBO.getCurrency());
    }

}
