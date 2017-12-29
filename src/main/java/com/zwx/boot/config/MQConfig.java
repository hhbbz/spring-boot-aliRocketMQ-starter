package com.zwx.boot.config;

import lombok.Data;

/**
 * Created by hhbbz on 2017/11/14.
 * @Explain: rocketMQ配置
 */
@Data
public class MQConfig {

    private String producerId;

    private String consumerId;

    private String accessKey;

    private String secretKey;

    private String onsAddr;

    private String topic;

    private String tag;

}
