package com.zxgangandy.account.biz.mq.producer.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mq.msg.PlaceOrderFrozenMsg;
import com.zxgangandy.account.biz.mq.msg.PlaceOrderFrozenResultMsg;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import com.zxgangandy.account.biz.service.ISpotAccountService;
import com.zxgangandy.base.exception.BizErr;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Andy
 * @className PlaceOrderFrozenResultListener
 * @desc 返回冻结结果本地事务监听回调
 */
@Component
@Slf4j
public class PlaceOrderFrozenResultListener implements TransactionListener {

    @Autowired
    private ISpotAccountService    spotAccountService;
    @Autowired
    private ISpotAccountLogService spotAccountLogService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String message = new String(msg.getBody());
        log.info("[冻结币种返回结果本地事务监听回调]执行逻辑--接收到消息, message={}", message);

        PlaceOrderFrozenMsg frozenMsg = (PlaceOrderFrozenMsg) arg;
        log.info("[冻结币种返回结果本地事务监听回调]开始进行冻结操作,orderId={}", frozenMsg.getOrderId());

        boolean hasEnoughBalance = spotAccountService.hasEnoughBalance(frozenMsg.getUserId(),
                frozenMsg.getCurrency(), frozenMsg.getAmount());

        if (!hasEnoughBalance) {
            log.warn("执行冻结币种返回结果,账户不足扣减,消息回滚. orderId={}", frozenMsg.getOrderId());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        // 足够扣减,进行账户冻结
        try {
            spotAccountService.frozen(frozenMsg.getUserId(), frozenMsg.getOrderId(), frozenMsg.getCurrency(),
                    frozenMsg.getBizType(), frozenMsg.getAmount(), "");
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (BizErr e) {
            log.error("执行账户冻结BizErr异常, orderId={}, e={}", frozenMsg.getOrderId(), e);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } catch (Exception e) {
            log.error("执行账户冻结异常, orderId={}, e={}", frozenMsg.getOrderId(), e);
            return LocalTransactionState.UNKNOW;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String message = new String(msg.getBody());
        String msgId = msg.getMsgId();

        log.info("[返回创建订单冻结结果本地事务回查]-接收到消息,msgId={}, message={}", msgId, message);
        PlaceOrderFrozenResultMsg frozenMsg = JSON.parseObject(message, PlaceOrderFrozenResultMsg.class);

        QueryWrapper<SpotAccountLog> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(SpotAccountLog::getOrderId, frozenMsg.getOrderId())
                .eq(SpotAccountLog::getBizType, frozenMsg.getBizType());
        SpotAccountLog accountLog = spotAccountLogService.getOne(wrapper);

        if (accountLog == null) {
            log.info("[返回冻结结果本地事务回查]-本地不存在扣款流水,消息回滚,msgId={}", msgId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }

        log.info("[返回创建订单冻结结果本地事务回查]-本地存在扣款流水,消息提交,msgId={}", msgId);
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
