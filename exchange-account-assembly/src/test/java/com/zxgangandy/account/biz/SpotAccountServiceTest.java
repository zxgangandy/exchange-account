package com.zxgangandy.account.biz;

import com.zxgangandy.account.assembly.AccountApplication;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.bo.UnfrozenReqBO;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import io.jingwei.base.utils.exception.BizErr;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;

/**
 * @author yangyi
 * @deta 2018/2/26
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class SpotAccountServiceTest {

    @Autowired
    private ISpotAccountService spotAccountService;

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




}
