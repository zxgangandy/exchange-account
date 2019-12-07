package com.zxgangandy.account.biz.mq.producer;

import com.zxgangandy.account.biz.mq.producer.listener.PlaceOrderFrozenResultListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.zxgangandy.account.biz.constant.MessageTopics.ORDER_STATUS_UPDATE_TOPIC;


@Component
@Slf4j
public class PlaceOrderFrozenResultProducer {

    /**事务回查线程池*/
    private ExecutorService executorService;
    /**事务消息生产者*/
    private TransactionMQProducer transactionMQProducer;

    @Value("${rocketmq.nameServer:127.0.0.1:9876}")
    private String nameSrvAddr;

    @Autowired
    private PlaceOrderFrozenResultListener orderFrozenResultListener;

    @PostConstruct
    public void init() {
        // 初始化回查线程池
        executorService = new ThreadPoolExecutor(
                5,
                512,
                10000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(512),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName(ORDER_STATUS_UPDATE_TOPIC.getProducerGroup() + "-check-thread");
                    return null;
                });

        transactionMQProducer = new TransactionMQProducer(ORDER_STATUS_UPDATE_TOPIC.getProducerGroup());
        transactionMQProducer.setNamesrvAddr(nameSrvAddr);
        transactionMQProducer.setExecutorService(executorService);
        transactionMQProducer.setTransactionListener(orderFrozenResultListener);
        transactionMQProducer.setVipChannelEnabled(false);
        try {
            transactionMQProducer.start();
        } catch (MQClientException e) {
            throw new RuntimeException("启动[下单的订单状态修改生产者]PlaceOrderStatusUpdateProducer异常", e);
        }
        log.info("启动[下单的订单状态修改生产者]PlaceOrderStatusUpdateProducer成功, topic={}",
                ORDER_STATUS_UPDATE_TOPIC.getTopic());
    }

    public TransactionMQProducer getProducer() {
        return transactionMQProducer;
    }
}
