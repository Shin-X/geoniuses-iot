package com.geoniuses.core.utils;



import com.geoniuses.core.config.RedisClusterConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author ：zyf
 * @date ：2020/5/19 20:52
 */
@Component
public class RedisUtil {

    private static RedisClusterConfig redisClusterConfig;

    public RedisUtil(RedisClusterConfig redisClusterConfig){
        this.redisClusterConfig = redisClusterConfig;
    }
    private static final Logger logger = LogManager.getLogger(RedisUtil.class);
    /**
     * 获取分布式锁
     *
     * @param key        string 缓存key
     * @param expireTime int 过期时间，单位秒
     * @return boolean true-抢到锁，false-没有抢到锁
     */
    public static boolean getDistributedLockSetTime(String key, Integer expireTime) {
        try {
            // 移除已经失效的锁
            byte[] temp = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().get(key.getBytes());
            Long currentTime = System.currentTimeMillis();
            if (null != temp && Long.valueOf(new String(temp)) < currentTime) {
                redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().del(key.getBytes());
            }

            // 锁竞争
            Long nextTime = currentTime + Long.valueOf(expireTime) * 1000;
            Boolean result = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().stringCommands().setNX(key.getBytes(), String.valueOf(nextTime).getBytes());
            if (result) {
                redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().expire(key.getBytes(), expireTime);
                return true;
            }
        } catch (Exception ignored) {
            logger.log(Level.ERROR,"===============获取redis分布式锁失败===============",ignored);
        }
        return false;
    }
}
