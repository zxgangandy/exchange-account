package com.zxgangandy.account.biz.mq.consumer;

import com.zxgangandy.account.biz.mq.consumer.listener.PlaceOrderFrozenListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.zxgangandy.account.biz.constant.MessageTopics.PLACE_ORDER_FROZEN_TOPIC;


@Component
@Slf4j
public class PlaceOrderFrozenConsumer {

    @Value("${rocketmq.nameServer:127.0.0.1:9876}")
    private String nameSrvAddr;

    @Autowired
    private PlaceOrderFrozenListener placeOrderFrozenListener;

    private DefaultMQPushConsumer defaultMQPushConsumer;

    @PostConstruct
    public void init() {
        defaultMQPushConsumer = new DefaultMQPushConsumer(PLACE_ORDER_FROZEN_TOPIC.getConsumerGroup());
        defaultMQPushConsumer.setNamesrvAddr(nameSrvAddr);
        // 从头开始消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 消费模式:集群模式
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        // 注册监听器
        defaultMQPushConsumer.registerMessageListener(placeOrderFrozenListener);
        // 订阅所有消息
        try {
            defaultMQPushConsumer.subscribe(PLACE_ORDER_FROZEN_TOPIC.getTopic(), "*");
            defaultMQPushConsumer.start();
        } catch (MQClientException e) {
            log.error("[创建订单冻结资金消息消费者]--PlaceOrderFrozenConsumer加载异常!e={}", e);
            throw new RuntimeException("[扣款消息消费者]--PlaceOrderFrozenConsumer加载异常!", e);
        }

        log.info("[创建订单冻结资金消息消费者]--PlaceOrderFrozenConsumer加载完成!");
    }
}
