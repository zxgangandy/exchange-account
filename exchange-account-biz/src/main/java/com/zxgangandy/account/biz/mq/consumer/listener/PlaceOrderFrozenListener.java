package com.zxgangandy.account.biz.mq.consumer.listener;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mq.msg.PlaceOrderFrozenMsg;
import com.zxgangandy.account.biz.mq.msg.PlaceOrderFrozenResultMsg;
import com.zxgangandy.account.biz.mq.producer.PlaceOrderFrozenResultProducer;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.zxgangandy.account.biz.constant.MessageTopics.ORDER_STATUS_UPDATE_TOPIC;

/**
 * @author Andy
 * @desc 冻结消息消费监听回调实现
 */
@Component
@Slf4j
public class PlaceOrderFrozenListener implements MessageListenerConcurrently {

    @Autowired
    private ISpotAccountLogService         spotAccountLogService;
    @Autowired
    private PlaceOrderFrozenResultProducer orderFrozenResultProducer;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt msg : msgs) {
                int reconsumeTimes = msg.getReconsumeTimes();
                String msgId = msg.getMsgId();
                log.info("msgId={},消费次数={}", msgId, reconsumeTimes);
                return prepareFrozenCurrency(msg, msgId);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("钱包扣款消费异常,e={}", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    /**
     * 同样使用事务消息的方式，准备开始冻结用户币种，并返回冻结的结果（冻结用户币种，并插入扣款流水）
     */
    private ConsumeConcurrentlyStatus prepareFrozenCurrency(MessageExt msg, String msgId) {
        String message = new String(msg.getBody());
        log.info("msgId={},钱包扣款消费者接收到消息, message={}", msgId, message);
        PlaceOrderFrozenMsg frozenMsg = JSON.parseObject(message, PlaceOrderFrozenMsg.class);

        QueryWrapper<SpotAccountLog> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SpotAccountLog::getOrderId, frozenMsg.getOrderId());
        SpotAccountLog accountLog = spotAccountLogService.getOne(wrapper);

        // 幂等消费逻辑: 根据订单号查询扣款流水，如果存在则直接返回消费成功
        if (accountLog != null) {
            log.info("[冻结消息消费]-本地已经存在orderId=[{}]对应的冻结流水,不需要重复消费, msgId={}",
                    frozenMsg.getOrderId(),
                    msgId);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        try {
            // 组装半消息：冻结币种成功修改订单状态为OrderStatus.PREPARE
            PlaceOrderFrozenResultMsg resultMsg = new PlaceOrderFrozenResultMsg()
                    .setBizType(frozenMsg.getBizType())
                    .setOrderId(frozenMsg.getOrderId())
                    .setUserId(frozenMsg.getUserId());
            String msgBody = JSON.toJSONString(resultMsg);

            Message updateOrderStatusMsg = new Message(ORDER_STATUS_UPDATE_TOPIC.getTopic(), msgBody.getBytes());
            // 半消息发送
            TransactionSendResult transactionSendResult = orderFrozenResultProducer.getProducer()
                    .sendMessageInTransaction(updateOrderStatusMsg, frozenMsg);

            if (transactionSendResult == null) {
                // 发送未知重新消费
                log.info("msgId={},订单状态更新半消息发送状态未知,消费状态[RECONSUME_LATER],等待重新消费,orderId={}",
                        msgId,
                        frozenMsg.getOrderId(),
                        message);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.COMMIT_MESSAGE)) {
                log.info("msgId={},订单状态更新半消息发送成功,消费状态[CONSUME_SUCCESS], orderId={}, sendResult={}",
                        msgId,
                        frozenMsg.getOrderId(),
                        transactionSendResult);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

            if (transactionSendResult.getLocalTransactionState().equals(LocalTransactionState.UNKNOW)) {
                log.warn("msgId={},订单状态更新本地事务执行状态未知,消费状态[RECONSUME_LATER], orderId={}, sendResult={}",
                        msgId,
                        frozenMsg.getOrderId(),
                        transactionSendResult);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        } catch (Exception e) {
            log.error("msgId={},订单状态更新半消息发送异常,消费状态[RECONSUME_LATER],e={}", msgId, e);
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
