package com.yufeng.common.mapper;

import com.yufeng.common.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description
 * @Author yangwu
 * CreateTime        202010071101
 */
@Mapper
public interface OrderMapper {

    void saveOrder(Order order);

    Order getOrder(String userId,String goodsId);
}
