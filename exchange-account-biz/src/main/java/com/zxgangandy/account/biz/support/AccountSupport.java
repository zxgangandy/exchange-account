package com.zxgangandy.account.biz.support;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.entity.SpotAccountFrozen;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.entity.SpotAccountUnfrozen;

public class AccountSupport {

    public static SpotAccountFrozen createOrderFrozen(long accountId, FrozenReqBO reqBO) {
        return new SpotAccountFrozen()
                .setUserId(reqBO.getUserId())
                .setOrderId(reqBO.getOrderId())
                .setBizType(reqBO.getBizType())
                .setCurrency(reqBO.getCurrency())
                .setOriginFrozen(reqBO.getAmount())
                .setLeftFrozen(reqBO.getAmount())
                .setAccountId(accountId);
    }
    public static SpotAccountLog createFrozenLog(SpotAccount account, FrozenReqBO reqBO) {
        return new SpotAccountLog()
                .setFromAccountId(account.getAccountId())
                .setToAccountId(account.getAccountId())
                .setFromUserId(reqBO.getUserId())
                .setToUserId(reqBO.getUserId())
                .setAmount(reqBO.getAmount())
                .setBalance(account.getBalance())
                .setFrozen(account.getFrozen())
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
                .setBalance(account.getBalance())
                .setFrozen(account.getFrozen())
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

}
