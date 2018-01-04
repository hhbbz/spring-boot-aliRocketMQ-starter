package com.zwx.boot.rocketMQ.consumer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;

/**
 * Created by hhbbz on 2017/11/14.
 * @Explain: 处理订阅下来的MQ(判断事件，处理数据)
 */

public class ConsumerHandler implements MessageOrderListener {

    @Override
    public OrderAction consume(Message message, ConsumeOrderContext context) {
        return null;
    }
}
