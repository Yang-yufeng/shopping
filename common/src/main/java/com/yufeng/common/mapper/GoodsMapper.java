package com.yufeng.common.mapper;

import com.yufeng.common.entity.Goods;
import org.apache.ibatis.annotations.Mapper;


/**
 * @Description
 * @Author yangwu
 * CreateTime        202010011204
 */
@Mapper
public interface GoodsMapper {

    Goods getGoods(String id);

    void updateGoods(Goods goods);

    void saveGoods(Goods goods);


}
