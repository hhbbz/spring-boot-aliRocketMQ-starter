package com.zwx.boot.rocketMQ.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by hhbbz on 2017/11/9.
 * @Explain: 在实体类中一对一使用
 */
//TODO:key和shardingKey统一是业务唯一标识，这里约定key为业务事件，shardingKey为实体ID
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class ProducerMessageBean<T>{

    private Long id;

    private String name;

    private String topic;

    private String tags;

    private String key;

    //TODO:，同一shardingKey发送到同一个consumer，
    // TODO:也有可能同一个consumer消费了多个shardingKey的消息，这时候要通过shardingKey去判断是否需要消费
    private String shardingKey;   //shardingKey 用作消息分区

    private String method; // 发送模式：同步，异步，单向

    private String type; // 消息类型：定时消息，延时消息，分区顺序消息，事务消息

    private long delayTime;//延时时长

    private String startDeliveryTime; //开始时间

    private T body;

    private int isCommit;

    private String state;

    private Date runDate;
}
