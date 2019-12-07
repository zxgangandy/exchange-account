package com.zxgangandy.account.biz.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 *
 */
@Getter
@AllArgsConstructor
public enum MessageTopics {

    /**
     * PLACE_ORDER_FROZEN_TOPIC 账务冻结
     */
    PLACE_ORDER_FROZEN_TOPIC("PLACE_ORDER_FROZEN_TOPIC",
            "PID_ACCOUNT_FREEZE_TOPIC",
            "CID_ACCOUNT_FREEZE_TOPIC",
            "账务冻结协议"),

    /**
     * ORDER_STATUS_UPDATE_TOPIC 返回创建订单账务冻结结果
     */
    ORDER_STATUS_UPDATE_TOPIC("ORDER_STATUS_UPDATE_TOPIC",
            "PID_ORDER_STATUS_UPDATE_TOPIC",
            "CID_ORDER_STATUS_UPDATE_TOPIC",
            "返回创建订单账务冻结结果协议"),

    ;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 生产者组
     */
    private String producerGroup;
    /**
     * 消费者组
     */
    private String consumerGroup;
    /**
     * 消息描述
     */
    private String desc;

}
