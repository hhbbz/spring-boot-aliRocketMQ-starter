package com.zwx.boot.rocketMQ.common;
/**
 * Created by hhbbz on 2017/11/9.
 * @Explain: mq相关
 */

public class RocketMQServiceConstant {

    /**
     * Message send method(同步，异步，单向)
     */
    public static final String SYNCHRONOUS_MESSAGE = "SYNCHRONOUS";//发送普通、定时、延时同步信息

    public static final String SYNCHRONOUS_ORDER_MESSAGE = "SYNCHRONOUS_ORDER";//发送分区顺序同步信息

    public static final String SYNCHRONOUS_TRAN_MESSAGE = "SYNCHRONOUS_TRAN";//发送事务同步信息

    public static final String ASYNCHRONOUS_MESSAGE = "ASYNCHRONOUS";//发送异步信息

    public static final String ONE_WAY_MESSAGE = "ONE_WAY";//发送单向信息

    /**
     * Message type（定时消息，延时消息，分区顺序消息，事务消息）
     */
    public static final String TIMING_MESSAGE = "TIMING";

    public static final String DELAY_MESSAGE = "DELAY";

    public static final String ORDER_MESSAGE = "ORDER";

    public static final String TRANSACTION_MESSAGE = "TRANSACTION";
}
