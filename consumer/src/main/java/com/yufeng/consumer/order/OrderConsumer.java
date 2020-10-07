package com.yufeng.consumer.order;

import java.util.Map;


import com.yufeng.common.entity.Goods;
import com.yufeng.common.entity.Order;

import com.yufeng.common.mapper.GoodsMapper;
import com.yufeng.common.mapper.OrderMapper;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.amqp.support.AmqpHeaders;

import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Component
public class OrderConsumer {

	@Resource
	private GoodsMapper goodsMapper;

	@Resource
	private OrderMapper orderMapper;

	@RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order-queue",durable = "true"),
			exchange = @Exchange(name="order-exchange",type = "topic"),
			key="order.*")
	        )
	@RabbitHandler
	@Transactional
	public void orderReceive(@Payload Order order,
			@Headers Map<String,Object> headers,
			Channel channel) throws Exception{
		//消费者操作
		System.out.println("收到消息，开始消费======================");
		Long deliverTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
		orderMapper.saveOrder(order);
		Goods goods = goodsMapper.getGoods(order.getGoodsId());
		goods.setStore(goods.getStore()-1);
		goodsMapper.updateGoods(goods);
		channel.basicAck(deliverTag, false);
		System.out.println("消费完成===========");
	}
	
	

}
