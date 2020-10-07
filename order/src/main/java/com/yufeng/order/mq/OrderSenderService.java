package com.yufeng.order.mq;



import com.yufeng.common.entity.Order;

import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class OrderSenderService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendOrder(Order order) throws Exception{
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setConfirmCallback(confirmCallback);
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.convertAndSend("order-exchange",
                "order.abcd",
                order,
                correlationData);;
    }

    //回调函数: confirm确认
    final ConfirmCallback confirmCallback = new ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.err.println("correlationData: " + correlationData);
            String messageId = correlationData.getId();
            if(ack){
                //如果confirm返回成功 则进行更新
                System.out.println("消息成功发送到队列===============messageId="+messageId);
            } else {
                //失败则进行具体的后续操作:重试 或者补偿等手段
                System.err.println("异常处理...");
            }
        }
    };

    final ReturnCallback returnCallback = new ReturnCallback() {

        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

        }
    };



}
