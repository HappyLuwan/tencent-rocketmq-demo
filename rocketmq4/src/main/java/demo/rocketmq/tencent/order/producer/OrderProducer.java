/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package demo.rocketmq.tencent.order.producer;

import demo.rocketmq.tencent.common.ClientCreater;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Description: 顺序发送
 * 消息的顺序发送在原生RocketMQ使用本方式实现。通过设定key值，保证同一个key值的消息发送到同一个队列中。
 */
public class OrderProducer {

    /**
     * topic名称
     */
    private static final String TOPIC_NAME = "order_topic";

    /**
     * 生产者组名称
     */
    private static final String GROUP_NAME = "group2";

    public static void main(String[] args) throws Exception {
        // 创建消息消费者
        DefaultMQProducer producer = ClientCreater.createProducer(GROUP_NAME);
        for (int i = 0; i < 3; i++) {
            int orderId = i % 3;
            // 构造消息示例
            Message msg = new Message(TOPIC_NAME, "your tag", "KEY" + i,
                    ("Hello RocketMQ " + i).getBytes(StandardCharsets.UTF_8));
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg1, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);

            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }
}
