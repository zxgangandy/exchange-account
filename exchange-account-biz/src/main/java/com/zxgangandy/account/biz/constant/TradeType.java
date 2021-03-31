package com.zxgangandy.account.biz.constant;

import lombok.Getter;

/**
 * @className: TradeType
 * @description: TODO 类描述
 * @author: andy
 * @date: 3/31/21
 **/
@Getter
public enum  TradeType {

    IN("in"),
    OUT("out");


    private final String type;

    private TradeType(String type) {
        this.type = type;
    }
}
