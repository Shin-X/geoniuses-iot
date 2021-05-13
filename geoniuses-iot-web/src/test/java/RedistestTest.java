import com.geoniuses.core.config.RedisClusterConfig;
import com.geoniuses.web.Application;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author liuxin
 * @Date: 2021/5/10 18:56
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
class RedistestTest {

    @Autowired
    RedisClusterConfig redisClusterConfig;
    @Test
    void setRedisClusterConfig() {
        Boolean exists = redisClusterConfig.getLettuceConnectionFactory().getClusterConnection().keyCommands().exists("00000002493400ff0000000d00230001".getBytes());
        System.err.println(exists);
    }
}