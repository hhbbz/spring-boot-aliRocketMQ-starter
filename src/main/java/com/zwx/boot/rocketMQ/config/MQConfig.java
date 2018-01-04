package com.zwx.boot.rocketMQ.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hhbbz on 2017/11/14.
 * @Explain: rocketMQ配置
 */
@Data
@Configuration
@ConfigurationProperties(
        prefix = "mq-config",
        ignoreUnknownFields = false
)
public class MQConfig {

    private String producerId;

    private String consumerId;

    private String accessKey;

    private String secretKey;

    private String onsAddr;

    private String topic;

    private String tag;

}
