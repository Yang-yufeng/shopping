package com.yufeng.order.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @Description
 * @Author yangwu
 * CreateTime        202009301808
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate redisTemplate;

    Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 读取缓存字符串
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return String.valueOf(result);
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public boolean receiveFlag(String redisKey) {
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        if (count == 1) {
            //设置有效期一分钟
            redisTemplate.expire(redisKey, 22, TimeUnit.SECONDS);
            return true;
        }
        if (count > 1) {
            return false;
        }

        return false;
    }

    public boolean receiveFlag2(String redisKey) {
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        if (count == 1) {
            //设置有效期一分钟
            redisTemplate.expire(redisKey, 60, TimeUnit.SECONDS);
            return true;
        }
        if (count > 1) {
            return false;
        }

        return false;
    }

    public boolean receiveFlagCustom(String redisKey, Long cacheTime) {
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        if (count == 1) {
            redisTemplate.expire(redisKey, cacheTime, TimeUnit.SECONDS);
            return true;
        }
        if (count > 1) {
            return false;
        }

        return false;
    }

    public void deleteByPrefix(String key) {
        Set<String> keyList = redisTemplate.keys(key + "*");
        redisTemplate.delete(keyList);
    }

    public long incrementByNum(String redisKey, Long num) {
        if (!this.exists(redisKey)) {
            this.set(redisKey, num);
        }
        long count = redisTemplate.opsForValue().increment(redisKey, 1);
        return count;
    }


    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object lGetOne(String key){
        try {
            return redisTemplate.opsForList().leftPop(key);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean lSet(String key, Object value, Long expireTime) {

        try {
            redisTemplate.opsForList().rightPush(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list数据清空
     *
     * @param time  时间(秒)
     * @return
     */
    public boolean lClear(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发布消息
     *
     * @param channel 频道
     * @param message 信息
     */
    public void convertAndSend(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }


    /**
     * @param key
     * @param x
     * @param y
     * @param distance m
     * @return
     * @MethodName：radiusGeo
     * @ReturnType：GeoResults<GeoLocation<String>>
     * @Description：通过给定的坐标和距离(km)获取范围类其它的坐标信息
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public GeoResults<GeoLocation<String>> radiusGeo(String key, double x, double y, double distance, Direction direction, long limit) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

            //设置geo查询参数  
            GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标  
            if (Direction.ASC.equals(direction)) {//按查询出的坐标距离中心坐标的距离进行排序  
                geoRadiusArgs.sortAscending();
            } else if (Direction.DESC.equals(direction)) {
                geoRadiusArgs.sortDescending();
            }
            geoRadiusArgs.limit(limit);//限制查询数量  
            GeoResults<GeoLocation<String>> radiusGeo = geoOps.radius(key, new Circle(new Point(x, y), new Distance(distance, DistanceUnit.METERS)), geoRadiusArgs);

            return radiusGeo;
        } catch (Throwable t) {
            logger.error("通过坐标[" + x + "," + y + "]获取范围[" + distance + "km的其它坐标失败]" + ", error[" + t + "]");
        }
        return null;
    }

    /**
     * @param key
     * @param x
     * @param y
     * @param member
     * @param time(单位秒) <=0 不过期
     * @return
     * @MethodName：cacheGeo
     * @ReturnType：boolean
     * @Description：缓存地理位置信息
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheGeo(String key, double x, double y, String member, long time) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.add(key, new Point(x, y), member);
            if (time > 0) redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Throwable t) {
            logger.error("缓存[" + key + "]" + "失败, point[" + x + "," +
                    y + "], member[" + member + "]" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * @param key
     * @param locations
     * @param time(单位秒) <=0 不过期
     * @return
     * @MethodName：cacheGeo
     * @ReturnType：boolean
     * @Description：
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public boolean cacheGeo(String k, Iterable<GeoLocation<String>> locations, long time) {
        try {
            for (GeoLocation<String> location : locations) {
                cacheGeo(k, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
            }
        } catch (Throwable t) {
            logger.error("缓存[" + k + "]" + "失败" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * @param key
     * @param members
     * @return
     * @MethodName：removeGeo
     * @ReturnType：boolean
     * @Description：移除地理位置信息
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public boolean removeGeo(String key, String... members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            geoOps.remove(key, members);
        } catch (Throwable t) {
            logger.error("移除[" + key + "]" + "失败" + ", error[" + t + "]");
        }
        return true;
    }

    /**
     * @param key
     * @param member1
     * @param member2
     * @return Distance
     * @MethodName：distanceGeo
     * @ReturnType：Distance
     * @Description：根据两个成员计算两个成员之间距离
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public Distance distanceGeo(String key, String member1, String member2) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.distance(key, member1, member2);
        } catch (Throwable t) {
            logger.error("计算距离[" + key + "]" + "失败, member[" + member1 + "," + member2 + "], error[" + t + "]");
        }
        return null;
    }

    /**
     * @param key
     * @param members
     * @return
     * @MethodName：getGeo
     * @ReturnType：List<Point>
     * @Description：根据key和member获取这些member的坐标信息
     * @Creator：mylies
     * @Modifier：
     * @ModifyTime：
     */
    public List<Point> getGeo(String key, String... members) {
        try {
            GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();
            return geoOps.position(key, members);
        } catch (Throwable t) {
            logger.error("获取坐标[" + key + "]" + "失败]" + ", error[" + t + "]");
        }
        return null;
    }

    public void lPush(String key,Object value){
  	  redisTemplate.opsForList().leftPush(key, value);
    }
    
    public String rPop(String key){
  	  return (String) redisTemplate.opsForList().rightPop(key);
    }
     
    public String bRPop(String key,Integer time){
  	  return (String)redisTemplate.opsForList().leftPop(key, time, TimeUnit.SECONDS);
    }





}