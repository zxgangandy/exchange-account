package com.zxgangandy.account.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.zxgangandy.account.biz.mapper.SpotAccountLogMapper;
import com.zxgangandy.account.biz.service.ISpotAccountLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
@Service
public class SpotAccountLogServiceImpl extends ServiceImpl<SpotAccountLogMapper, SpotAccountLog> implements ISpotAccountLogService {

    @Override
    public SpotAccountLog getAccountLog(long orderId, String bizType) {
        QueryWrapper<SpotAccountLog> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(SpotAccountLog::getOrderId, orderId)
                .eq(SpotAccountLog::getBizType, bizType);
        return  getOne(wrapper);
    }
}
