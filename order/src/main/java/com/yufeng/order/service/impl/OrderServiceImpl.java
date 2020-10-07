package com.yufeng.order.service.impl;

import com.yufeng.common.entity.Goods;
import com.yufeng.common.entity.Order;
import com.yufeng.common.mapper.GoodsMapper;
import com.yufeng.common.result.ResultEntity;
import com.yufeng.common.result.ResultEnum;

import com.yufeng.order.mq.OrderSenderService;
import com.yufeng.order.redis.RedisUtils;
import com.yufeng.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301808
 */
@Service
public class OrderServiceImpl implements OrderService {


    @Resource
    private RedisUtils redisUtils;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private OrderSenderService orderSenderService;

    @Override
    @Transactional
    public String createSecondKill(String goodsId, Integer number) {
        ResultEntity result;
        String skillGoodsKey = "skillGoods:"+goodsId;
        //缓存中放入商品计数
        try {
            for(int i=0;i<number;i++){
                redisUtils.lPush(skillGoodsKey,i+1);
            }
            //数据库插入库存
            Goods goods = goodsMapper.getGoods(goodsId);
            if(goods!=null){
                goods.setStore(number);
                goodsMapper.updateGoods(goods);
            }else{
                goods = new Goods("skillGoods",number);
                goods.setId(goodsId);
                goodsMapper.saveGoods(goods);
            }
            result = new ResultEntity(ResultEnum.SUCCESS);
        }catch (Exception e){
            System.out.println(e.getMessage());
            result = new ResultEntity(ResultEnum.FAIL);
            redisUtils.lClear(skillGoodsKey);
        }
        return result.toString();
    }

    @Override
    @Transactional
    public String secondskill(String userId, String goodsId) {
        ResultEntity result;
        Integer goodsInfo = (Integer) redisUtils.lGetOne("skillGoods:"+goodsId);
        if(goodsInfo==null){
            System.out.println("用户"+userId+"没有抢到商品");
            result = new ResultEntity(ResultEnum.SALE_OUT);
        }else{
            try {
                System.out.println("用户"+userId+"抢到了"+goodsInfo+"号商品");
                redisUtils.lSet(goodsId+"skillUserIds",userId);
//              //更新数据库库存
//              Goods goods = goodsMapper.getGoods(goodsId);
//              goods.setStore(goods.getStore()-1);
//              goodsMapper.updateGoods(goods);
                //使用rabbitmq异步更新数据库
                Order order = new Order(userId,goodsId,1,goodsInfo);
                orderSenderService.sendOrder(order);
                result = new ResultEntity(ResultEnum.SUCCESS);
            }catch (Exception e){
                System.out.println(e.getMessage());
                redisUtils.lClear(goodsId+"skillUserIds");
                result = new ResultEntity(ResultEnum.FAIL);
            }
        }
        return  result.toString();
    }
}
