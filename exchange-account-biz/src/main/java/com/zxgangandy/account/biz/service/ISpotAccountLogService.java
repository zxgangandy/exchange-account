package com.zxgangandy.account.biz.service;

import com.zxgangandy.account.biz.entity.SpotAccountLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Andy
 * @since 2019-11-08
 */
public interface ISpotAccountLogService extends IService<SpotAccountLog> {

    String BIZ_TYPE_PLACE_ORDER = "Place_Order_Frozen_Account";

    /**
     *  根据用户id和币种获取用户账户日志信息
     */
    SpotAccountLog getAccountLog(long orderId, String bizType);
}
