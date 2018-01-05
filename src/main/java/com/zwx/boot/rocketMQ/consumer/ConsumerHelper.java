package com.zwx.boot.rocketMQ.consumer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.zwx.boot.rocketMQ.config.MQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by hhbbz on 2017/11/14.
 * @Explain: 初始化Consumer
 */
@Component
public class ConsumerHelper {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerHelper.class);
    @Autowired
    private MQConfig mqConfig;

    /**
     * Initialize consumer
     */
    @Bean("orderConsumer")
    public OrderConsumer orderConsumer() {
        OrderConsumer orderConsumer;
        long startTimestamp = System.currentTimeMillis();
        logger.info("orderConsumer 开始初始化");
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, mqConfig.getConsumerId());
        properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqConfig.getSecretKey());
        orderConsumer = ONSFactory.createOrderedConsumer(properties);
        long costTime = System.currentTimeMillis() - startTimestamp;
        logger.info("orderConsumer 初始化成功 耗时 " + costTime + " ms");
        return orderConsumer;
    }

    /**
     * To shut down consumer
     */
    public static void shutDown(OrderConsumer consumer) {
        consumer.shutdown();
    }

}
