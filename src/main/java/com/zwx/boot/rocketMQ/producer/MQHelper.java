package com.zwx.boot.rocketMQ.producer;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.zwx.boot.rocketMQ.common.RocketMQServiceConstant;
import com.zwx.boot.rocketMQ.model.ProducerMessageBean;
import com.zwx.boot.rocketMQ.tools.DateUtil;
import com.zwx.boot.rocketMQ.tools.AssertValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;

/**
 * Created by hhbbz on 2017/11/9.
 * @Explain: 生成消息和选择消息发送方式
 */
@Component
public class MQHelper<T> {
    private static final Logger logger = LoggerFactory.getLogger(ProducerHelper.class);
    @Autowired
    private ProducerHelper producerHelper;
    /**
     * 生成消息体
     *
     * @param producerMessage
     * @return
     */
    public Message generateMessage(ProducerMessageBean<T> producerMessage) throws ParseException {
        Message msg = new Message();
        msg.setTag(producerMessage.getTags());
        msg.setKey(producerMessage.getKey());
        msg.setTopic(producerMessage.getTopic());
        msg.setBody(JSON.toJSONString(producerMessage.getBody()).getBytes());

        if (!AssertValue.isNotEmpty(producerMessage.getType())) {
            producerMessage.setType(RocketMQServiceConstant.ORDER_MESSAGE);
        }

        switch (producerMessage.getType()) {
            case RocketMQServiceConstant.TRANSACTION_MESSAGE:
                msg.setShardingKey(producerMessage.getShardingKey());
                break;
            // 生成 延时消息
            case RocketMQServiceConstant.DELAY_MESSAGE:
                msg.setStartDeliverTime(System.currentTimeMillis() + producerMessage.getDelayTime());
                break;
            // 生成 定时消息
            case RocketMQServiceConstant.TIMING_MESSAGE:
                long startDeliverTime = DateUtil.getTimestampByDateString(producerMessage.getStartDeliveryTime());
                // if start deliver time early than now, send message right now
                if (startDeliverTime < System.currentTimeMillis()) {
                    startDeliverTime = System.currentTimeMillis();
                }
                if(producerMessage.getShardingKey()!=null){
                    msg.setShardingKey(producerMessage.getShardingKey());
                }
                msg.setStartDeliverTime(startDeliverTime);
                break;
            case RocketMQServiceConstant.ORDER_MESSAGE:
                msg.setShardingKey(producerMessage.getShardingKey());
                break;
            // 生成 分区顺序消息
            default:
                if(producerMessage.getShardingKey()!=null){
                    msg.setShardingKey(producerMessage.getShardingKey());
                }else{
                    throw new RuntimeException("请设置shardingKey");
                }
                break;

        }

        return msg;
    }

    /**
     * 根据method发送消息
     *
     * @param message
     * @param producerMessage
     * @return
     */
    public SendResult sendMessage(Message message, ProducerMessageBean<T> producerMessage) {
        SendResult sendResult = null;
        if (!AssertValue.isNotEmpty(producerMessage.getMethod())) {
            producerMessage.setMethod(RocketMQServiceConstant.SYNCHRONOUS_MESSAGE);
        }

        switch (producerMessage.getMethod()) {
            // 发送异步消息
            case RocketMQServiceConstant.ASYNCHRONOUS_MESSAGE:
                sendResult = producerHelper.sendAsynchronousMessage(message);
                break;
            // 发送单向消息
            case RocketMQServiceConstant.ONE_WAY_MESSAGE:
                sendResult = producerHelper.sendOneWayMessage(message);
                break;
            // 发送同步消息
            case RocketMQServiceConstant.SYNCHRONOUS_MESSAGE:
                sendResult = producerHelper.sendSynchronousMessage(message);
                break;
            // 发送分区顺序同步消息
            case RocketMQServiceConstant.SYNCHRONOUS_ORDER_MESSAGE:
                sendResult = producerHelper.sendSynchronousOrderMessage(message);
                break;
            // 发送事务同步消息
            case RocketMQServiceConstant.SYNCHRONOUS_TRAN_MESSAGE:
                sendResult = producerHelper.sendSyncTranMessage(message,producerMessage.getIsCommit());
                break;
            default:
                sendResult = producerHelper.sendSynchronousOrderMessage(message);
                break;
        }
        return sendResult;
    }
}
