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
package demo.rocketmq.tencent.transaction.producer;

import demo.rocketmq.tencent.common.ClientCreater;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TransactionProducer {


    public static void main(String[] args) throws MQClientException, InterruptedException, IOException {
        TransactionListener transactionListener = new TransactionListenerImpl();
        // 实例化事务消息生产者Producer
        TransactionMQProducer producer = new TransactionMQProducer("transaction_group",
                // ACL权限
                new AclClientRPCHook(new SessionCredentials(ClientCreater.ACCESS_KEY, ClientCreater.SECRET_KEY)));
        // 设置NameServer的地址
        producer.setNamesrvAddr(ClientCreater.NAMESERVER);
        producer.setTransactionListener(transactionListener);
        producer.start();
        for (int i = 0; i < 3; i++) {
            try {
                Message msg =
                        new Message("transaction_topic", "your tag", "your key",
                                ("Hello RocketMQ " + i).getBytes(StandardCharsets.UTF_8));
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);
                System.out.printf("%s%n", sendResult);
                Thread.sleep(10);
            } catch (MQClientException e) {
                e.printStackTrace();
            }
        }
        System.in.read();
        producer.shutdown();
    }
}
