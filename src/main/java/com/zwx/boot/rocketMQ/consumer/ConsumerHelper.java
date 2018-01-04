package com.zwx.boot.rocketMQ.consumer;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.zwx.boot.rocketMQ.config.MQConfig;
import com.zwx.boot.rocketMQ.tools.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * Created by hhbbz on 2017/11/14.
 * @Explain: 初始化Consumer
 */
@Configurable
public class ConsumerHelper {

    @Autowired
    private MQConfig mqConfig;
    @Autowired
    private ApplicationContextProvider applicationContextProvider;
    /**
     * Initialize consumer
     */
    @Bean("orderConsumer")
    public OrderConsumer orderConsumer() {
        OrderConsumer consumer;
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.ConsumerId, mqConfig.getConsumerId());
        properties.put(PropertyKeyConst.AccessKey, mqConfig.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, mqConfig.getSecretKey());
        ConsumerHandler consumerHandler = (ConsumerHandler) applicationContextProvider.getBean("consumerHandler");
        consumer = ONSFactory.createOrderedConsumer(properties);
        consumer.subscribe(mqConfig.getTopic(), mqConfig.getTag(), consumerHandler);
        consumer.start();
        System.out.println("consumer start。。。");
        return consumer;
    }

    /**
     * To shut down consumer
     */
    public static void shutDown(OrderConsumer consumer) {
        consumer.shutdown();
    }

}
