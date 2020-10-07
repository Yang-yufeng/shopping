package com.yufeng.order.service;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301807
 */
public interface OrderService {

    /**
     * 创建秒杀
     * @param goodsId 商品id
     * @param number 商品数量
     * @return
     */
    String  createSecondKill(String goodsId,Integer number);

    /**
     * 秒杀
     * @param userId
     * @param goodsId
     * @return
     */
    String  secondskill(String userId,String goodsId);
}
