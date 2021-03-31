package com.zxgangandy.account.assembly;

import com.zxgangandy.account.biz.bo.FrozenReqBO;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.zxgangandy.account"})
//@MapperScan({"com.zxgangandy.account.biz.mapper"})
public class AccountApplication  implements CommandLineRunner {

    @Autowired
    private ISpotAccountService spotAccountService;
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        spotAccountService.frozen(new FrozenReqBO()
//                .setAmount(new BigDecimal(20000))
//                .setBizType("frozen1")
//                .setCurrency("USDT")
//                .setOrderId(1L)
//                .setUserId(456L));
    }
}
