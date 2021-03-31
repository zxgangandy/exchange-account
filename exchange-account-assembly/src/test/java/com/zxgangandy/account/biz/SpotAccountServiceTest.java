package com.zxgangandy.account.biz;

import com.zxgangandy.account.assembly.AccountApplication;
import com.zxgangandy.account.biz.bo.FrozenReqBO;
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
        //LearnResource learnResource=learnService.selectByKey(1001L);
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






}
