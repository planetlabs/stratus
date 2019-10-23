/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.connection;

import stratus.redis.config.RedisConfigProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePool;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * All business logic to construct models used by the connection view
 *
 * @author joshfix
 * Created on 6/12/18
 */
@Slf4j
@Service
public class DefaultConnectionService implements ConnectionService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private RedisConfigProps configProps;

    public static final String STATUS_TITLE = "Local Connection Pool Stats";
    public static final String CONFIG_TITLE = "Connection Pool Config";

    @Override
    public List<ClientModel> getClients() {
        List<ClientModel> clients = new ArrayList<>();
        List<RedisClientInfo> clientInfos = redisTemplate.getClientList();
        for (RedisClientInfo client : clientInfos) {
            ClientModel model = new ClientModel();
            model.setId(client.get("id"));
            model.setAddressAndPort(client.getAddressPort());
            model.setDatabase(client.getDatabaseId());
            model.setAge(client.getAge());
            model.setChannelSubscriptions(client.getChannelSubscribtions());
            model.setFlags(client.getFlags());
            model.setBufferLength(client.getBufferLength());
            model.setBufferFreeSpace(client.getBufferFreeSpace());
            model.setOutputBufferLength(client.getOutputBufferLength());
            model.setOutputBufferMemUsage(client.getOutputBufferMemoryUsage());
            model.setEvents(client.getEvents());
            model.setIdle(client.getIdle());
            model.setLastCommand(client.getLastCommand());
            clients.add(model);
        }
        return clients;
    }


    @Override
    public ConnectionPoolStatusModel connectionPoolStatus() throws Exception {
        ConnectionPoolStatusModel model = new ConnectionPoolStatusModel();
        model.setTitle(STATUS_TITLE);

        Field poolField = connectionFactory.getClass().getDeclaredField("pool");
        poolField.setAccessible(true);

        DirectFieldAccessor accessor = null;
        if (connectionFactory instanceof JedisConnectionFactory) {
            Pool<Jedis> pool = (Pool<Jedis>) poolField.get(connectionFactory);
            accessor = new DirectFieldAccessor(pool);
        } else if (connectionFactory instanceof LettuceConnectionFactory) {
            LettucePool pool = (LettucePool) poolField.get(connectionFactory);
            accessor = new DirectFieldAccessor(pool);
        }

        if (null != accessor) {
            GenericObjectPool p = (GenericObjectPool) accessor.getPropertyValue("internalPool");
            model.setActiveConnections(p.getNumActive());
            model.setMaxTotalConnections(p.getMaxTotal());
            model.setIdleConnections(p.getNumIdle());
            model.setMinIdleConnections(p.getMinIdle());
            model.setMaxIdleConnections(p.getMaxIdle());
        }
        return model;
    }

    @Override
    public ConnectionPoolConfigModel connectionPoolConfig() {
        ConnectionPoolConfigModel model = new ConnectionPoolConfigModel();
        BeanUtils.copyProperties(configProps.getPool(), model);
        model.setTitle(CONFIG_TITLE);
        return model;
    }
}
