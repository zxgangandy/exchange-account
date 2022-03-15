package com.zxgangandy.account.biz;

import com.zxgangandy.account.assembly.AccountApplication;
import com.zxgangandy.account.biz.bo.*;
import com.zxgangandy.account.biz.entity.SpotAccount;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import io.jingwei.base.utils.exception.BizErr;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class SpotAccountServiceTest {

    @Autowired
    private ISpotAccountService spotAccountService;

    @Test
    public void TestCreateOneAccount() {
        spotAccountService.createAccount(1L, "HCH");
        Assert.assertNotNull(spotAccountService.getAccount(1L, "HCH").get());
    }

    @Test
    public void TestCreateMultiAccounts() {
        try {
            spotAccountService.createAccount(Lists.newArrayList(3L, 4L), Lists.newArrayList("ETH"));
        } catch (BizErr err) {

        }

        Assert.assertNotNull(spotAccountService.getAccount(3L, "ETH").get());
    }

    @Test
    public void getExistAccount() {
        Assert.assertNotNull(spotAccountService.getAccount(3L, "ETH"));
        Assert.assertNotNull(spotAccountService.getAccount(4L, "ETH"));
    }

    @Test
    public void getExistAccounts() {
        List<Long> actualExits = Lists.newArrayList(3L, 4L);
        List<Long> exits = spotAccountService.getExistAccounts(Lists.newArrayList(3L, 4L, 0L), "ETH");

        Assert.assertEquals(exits, actualExits);
    }

    @Test
    public void getAccountList() {
        List<SpotAccount> accounts = spotAccountService.getAccountsByUserId(3L);
        Assert.assertNotNull(accounts);
        Assert.assertTrue(accounts.size() == 1);
    }

    @Test
    public void testDepositAccountNotCreate() {
        try {
            spotAccountService.deposit(new DepositReqBO()
                    .setAmount(new BigDecimal(10000))
                    .setBizType("deposit1")
                    .setCurrency("USDT")
                    .setOrderId(1L)
                    .setUserId(1L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void testDepositAccountOk() {

        SpotAccount oldAccount = spotAccountService.getAccount(1L, "BTC").get();

        spotAccountService.deposit(new DepositReqBO()
                .setAmount(new BigDecimal(10000))
                .setBizType("deposit1")
                .setCurrency("BTC")
                .setOrderId(1L)
                .setUserId(1L));

        SpotAccount newAccount = spotAccountService.getAccount(1L, "BTC").get();

        Assert.assertEquals(10000, newAccount.getBalance().subtract(oldAccount.getBalance()).longValue());
    }


    @Test
    public void testWithdrawAccountNotCreate() {
        try {
            spotAccountService.withdraw(new WithdrawReqBO()
                    .setAmount(new BigDecimal(10000))
                    .setBizType("withdraw1")
                    .setCurrency("USDT")
                    .setOrderId(1L)
                    .setUserId(1L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void testWithdrawBalanceNotEnough() {
        try {
            spotAccountService.withdraw(new WithdrawReqBO()
                    .setAmount(new BigDecimal(20000))
                    .setBizType("withdraw1")
                    .setCurrency("BTC")
                    .setOrderId(1L)
                    .setUserId(1L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12502"));
        }
    }

    @Test
    public void testWithdrawOK() {

        spotAccountService.withdraw(new WithdrawReqBO()
                .setAmount(new BigDecimal(10000))
                .setBizType("withdraw1")
                .setCurrency("BTC")
                .setOrderId(1L)
                .setUserId(1L));

        SpotAccount newAccount = spotAccountService.getAccount(1L, "BTC").get();

        Assert.assertEquals(0, newAccount.getBalance().subtract(newAccount.getBalance()).longValue());

    }

    @Test
    public void TestFrozenUserNotFound(){
        try {
            spotAccountService.frozen(new FrozenReqBO().setAmount(new BigDecimal(100))
                    .setBizType("frozen1")
                    .setCurrency("USDT")
                    .setOrderId(1L)
                    .setUserId(1L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void TestFrozenAmountInvalid(){
        try {
            spotAccountService.frozen(new FrozenReqBO()
                    .setAmount(new BigDecimal(2000000))
                    .setBizType("frozen1")
                    .setCurrency("USDT")
                    .setOrderId(3L)
                    .setUserId(456L));

        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12501"));
        }
    }

    @Test
    public void TestFrozenAmountDup(){
        spotAccountService.frozen(new FrozenReqBO()
                .setAmount(new BigDecimal(20000))
                .setBizType("frozen1")
                .setCurrency("USDT")
                .setOrderId(4L)
                .setUserId(456L));

        try {
            spotAccountService.frozen(new FrozenReqBO()
                    .setAmount(new BigDecimal(20000))
                    .setBizType("frozen1")
                    .setCurrency("USDT")
                    .setOrderId(4L)
                    .setUserId(456L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12502"));
        }
    }

    @Test
    public void TestUnFrozenUserNotFound(){
        try {
            spotAccountService.unfrozen(new UnfrozenReqBO()
                    .setBizId(1L)
                    .setAmount(new BigDecimal(100))
                    .setBizType("unfrozen1")
                    .setCurrency("USDT")
                    .setOrderId(1L)
                    .setUserId(1L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void TestUnFrozenAmountOk(){
        try {
            spotAccountService.unfrozen(new UnfrozenReqBO()
                    .setAmount(new BigDecimal(1))
                    .setBizId(2L)
                    .setBizType("frozen1")
                    .setCurrency("USDT")
                    .setOrderId(4L)
                    .setUserId(456L));

        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12504"));
        }
    }

    @Test
    public void TestUnFrozenAmountInvalid(){
        try {
            spotAccountService.unfrozen(new UnfrozenReqBO()
                    .setAmount(new BigDecimal(2000000))
                    .setBizType("frozen1")
                    .setBizId(2L)
                    .setCurrency("USDT")
                    .setOrderId(4L)
                    .setUserId(456L));

        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12504"));
        }
    }

    @Test
    public void TestUnFrozenAmountDup(){
        spotAccountService.unfrozen(new UnfrozenReqBO()
                .setAmount(new BigDecimal(20000))
                .setBizType("frozen1")
                .setBizId(3L)
                .setCurrency("USDT")
                .setOrderId(4L)
                .setUserId(456L));

        try {
            spotAccountService.unfrozen(new UnfrozenReqBO()
                    .setAmount(new BigDecimal(20000))
                    .setBizType("frozen1")
                    .setBizId(3L)
                    .setCurrency("USDT")
                    .setOrderId(4L)
                    .setUserId(456L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12502"));
        }
    }


    @Test
    public void TestTransferUserNotFound(){
        try {
            spotAccountService.transfer(new TransferReqBO()
                    .setOrderId(1L)
                    .setAmount(new BigDecimal(100))
                    .setBizType("transfer1")
                    .setCurrency("USDT")
                    .setFromUserId(7L)
                    .setToUserId(8L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void TestTransferUserNotFound2(){
        try {
            spotAccountService.transfer(new TransferReqBO()
                    .setOrderId(1L)
                    .setAmount(new BigDecimal(100))
                    .setBizType("transfer1")
                    .setCurrency("USDT")
                    .setFromUserId(1L)
                    .setToUserId(8L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12500"));
        }
    }

    @Test
    public void TestTransferAmountNotEnough(){
        try {
            spotAccountService.transfer(new TransferReqBO()
                    .setOrderId(1L)
                    .setAmount(new BigDecimal(100))
                    .setBizType("transfer1")
                    .setCurrency("ETH")
                    .setFromUserId(4L)
                    .setToUserId(3L));
        } catch (BizErr e) {
            log.error("e={}", e);
            Assert.assertThat(e.getCode().getCode(), is("12502"));
        }
    }

    @Test
    public void TestTransferAmountOK(){

        spotAccountService.transfer(new TransferReqBO()
                .setOrderId(1L)
                .setAmount(new BigDecimal(1))
                .setBizType("transfer1")
                .setCurrency("ETH")
                .setFromUserId(4L)
                .setToUserId(3L));
    }
}
