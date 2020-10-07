package com.yufeng.order.controller;

import com.yufeng.common.entity.User;
import com.yufeng.order.redis.RedisUtils;
import com.yufeng.order.service.OrderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301807
 */
@RestController
@RequestMapping("/order")
public class OrderController {


    @Resource
    private OrderService orderService;


    @Resource
    private RedisUtils redisUtils;

    /**
     * 秒杀商品
     * @param userId
     * @param goodsId
     * @return
     */
    @RequestMapping("/secondskill")
    public String buy(String userId, String goodsId){
        userId = userId==null? UUID.randomUUID().toString().replace("-",""):userId;
        String result = orderService.secondskill(userId,goodsId);
        return result;
    }



    /**
     * 创建秒杀
     * @param goodsId 商品id
     * @param number 商品数量
     * @return
     */
    @RequestMapping("/create_skill")
    public String createSecondKill(String goodsId,Integer number){
        String result = orderService.createSecondKill(goodsId,number);
        return  result;
    }


}
