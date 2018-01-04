package com.zwx.boot.rocketMQ.producer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.zwx.boot.rocketMQ.config.MQConfig;
import com.zwx.boot.rocketMQ.tools.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Properties;

/**
 * Created by hhbbz on 2017/11/9.
 * @Explain: 初始化producer 发送消息
 */
@Component
public class ProducerHelper {

    private static final Logger logger = LoggerFactory.getLogger(ProducerHelper.class);

    @Autowired
    private MQConfig mqConfig;

    @Autowired
    @Qualifier("producer")
    private Producer producer;
    @Autowired
    @Qualifier("orderProducer")
    private OrderProducer orderProducer;
    @Autowired
    @Qualifier("tranProducer")
    private TransactionProducer transactionProducer;




    @Bean("producer")
    public Producer producer(){
        long startTimestamp = System.currentTimeMillis();
        logger.info("Producer 开始初始化");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, mqConfig.getProducerId());
        properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, mqConfig.getOnsAddr());
        //消息生产失败重试次数
        properties.put(PropertyKeyConst.MaxReconsumeTimes,"2");
        producer = ONSFactory.createProducer(properties);
        producer.start();
        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("Producer 初始化成功 耗时 " + costTime + " ms");
        return producer;
    }

    @Bean("orderProducer")
    public OrderProducer orderProducer(){
        long startTimestamp = System.currentTimeMillis();
        logger.info("orderProducer 开始初始化");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, mqConfig.getProducerId());
        properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, mqConfig.getOnsAddr());
        //消息生产失败重试次数
        properties.put(PropertyKeyConst.MaxReconsumeTimes,"2");
        orderProducer = ONSFactory.createOrderProducer(properties);
        orderProducer.start();
        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("orderProducer 初始化成功 耗时 " + costTime + " ms");
        return orderProducer;
    }

    @Bean("tranProducer")
    public TransactionProducer transactionProducer(){
        long startTimestamp = System.currentTimeMillis();
        logger.info("transactionProducer 开始初始化");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ProducerId, mqConfig.getProducerId());
        properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqConfig.getSecretKey());
        properties.put(PropertyKeyConst.ONSAddr, mqConfig.getOnsAddr());
        //消息生产失败重试次数
        properties.put(PropertyKeyConst.MaxReconsumeTimes,"2");
        //初始化事务消息Producer时,需要注册一个本地事务状态的的Checker
        LocalTransactionCheckerImpl localTransactionChecker = new LocalTransactionCheckerImpl();
        transactionProducer = ONSFactory.createTransactionProducer(properties,localTransactionChecker);
        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("transactionProducer 初始化成功 耗时 " + costTime + " ms");
        return transactionProducer;
    }
    /**
     * 发送普通、定时、延时同步信息
     *
     * @param message
     * @return
     */
    public SendResult sendSynchronousMessage(Message message) {
        logger.info("开始发送发送普通、定时、延时同步信息 ");

        long startTimestamp = System.currentTimeMillis();
        SendResult sendResult = new SendResult();
        try {
            sendResult = producer.send(message);
            assert sendResult != null;
            System.out.println(new Date() + " 同步消息发送成功! Topic:" + message.getTopic() + " msgId: " + sendResult.getMessageId());
        }catch (ONSClientException e){
            e.printStackTrace();
            System.out.println("发送失败");
        }

        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("同步消息发送完成 耗时： " + costTime + " ms");

        return sendResult;
    }

    /**
     * 发送分区顺序同步信息
     *
     * @param message
     * @return
     */
    public SendResult sendSynchronousOrderMessage(Message message) {
        logger.info("开始发送分区顺序同步信息 ");

        long startTimestamp = System.currentTimeMillis();
        SendResult sendResult = new SendResult();
        try {
            sendResult = orderProducer.send(message, message.getShardingKey());
            assert sendResult != null;
            System.out.println(new Date() + " 发送消息成功 Topic:" + message.getTopic() + " msgId: " + sendResult.getMessageId());
        } catch (ONSClientException e) {
            e.printStackTrace();
            System.out.println("发送失败");
            //出现异常意味着发送失败，为了避免消息丢失，建议缓存该消息然后进行重试。
        }

        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("发送分区顺序同步信息完成 耗时： " + costTime + " ms");

        return sendResult;
    }

    /**
     * 发送事务同步信息
     *
     * @param message
     * @return
     */
    public SendResult sendSyncTranMessage(Message message,int isCommit) {
        logger.info("开始发送事务同步信息");

        long startTimestamp = System.currentTimeMillis();
        //初始化事务消息Producer时,需要注册一个本地事务状态的的Checker
        SendResult sendResult = new SendResult();
        //初始化事务消息Producer时,需要注册一个本地事务状态的的Checker
        transactionProducer.start();
        try {
            sendResult = transactionProducer.send(message, new LocalTransactionExecuter() {
                @Override
                public TransactionStatus execute(Message msg, Object arg) {
                    // 消息ID(有可能消息体一样，但消息ID不一样, 当前消息ID在控制台无法查询)
                    String msgId = msg.getMsgID();
                    // 消息体内容进行crc32, 也可以使用其它的如MD5
                    long crc32Id = HashUtil.crc32Code(msg.getBody());
                    // 消息ID和crc32id主要是用来防止消息重复
                    // 如果业务本身是幂等的, 可以忽略, 否则需要利用msgId或crc32Id来做幂等
                    // 如果要求消息绝对不重复, 推荐做法是对消息体body使用crc32或md5来防止重复消息
                    Object businessServiceArgs = new Object();
                    TransactionStatus transactionStatus = TransactionStatus.Unknow;
                    try {
                        if (isCommit>0) {
                            // 本地事务成功、提交消息
                            transactionStatus = TransactionStatus.CommitTransaction;
                        } else {
                            // 本地事务失败、回滚消息
                            transactionStatus = TransactionStatus.RollbackTransaction;
                        }
                    } catch (Exception e) {
                        logger.error("Message Id:{}", msgId, e);
                    }
                    System.out.println(msg.getMsgID());
                    logger.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
                    return transactionStatus;
                }
            }, null);
        }
        catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " 发送消息失败. Topic:" + message.getTopic());
            e.printStackTrace();
        }

        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("发送事务同步信息完成 耗时： " + costTime + " ms");

        return sendResult;
    }

    /**
     * 发送单向消息
     *
     * @param message
     * @return
     */
    public SendResult sendOneWayMessage(Message message) {
        logger.info("开始发送单向消息 ");

        long startTimestamp = System.currentTimeMillis();

        producer.sendOneway(message);
        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("发送单向消息完成 耗时：" + costTime + " ms");

        return null;
    }

    /**
     * 发送异步信息
     *
     * @param message
     * @return
     */
    public SendResult sendAsynchronousMessage(Message message) {
        logger.info("开始发送异步信息 ");

        long startTimestamp = System.currentTimeMillis();

        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(final SendResult sendResult) {
                logger.info("发送异步信息成功，response： " + JSON.toJSONString(sendResult));
            }

            @Override
            public void onException(OnExceptionContext onExceptionContext) {
                logger.info("发送异步信息失败, error ：" + onExceptionContext.getException().getMessage());
            }
        });

        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("发送异步信息完成 耗时： " + costTime + " ms");

        return null;
    }
}

