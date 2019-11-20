/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import org.geoserver.platform.resource.AbstractResourceNotificationDispatcherTest;
import org.geoserver.platform.resource.ResourceNotificationDispatcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import stratus.redis.config.EmbeddedRedisConfig;
import stratus.redis.repository.RedisRepositoryImpl;

/**
 * Run the base GeoServer notification tests
 */
@RunWith(Theories.class)
@ContextConfiguration(classes = {EmbeddedRedisConfig.class, RedisMessageListenerContainer.class, RedisNotificationDispatcher.class,
        RedisRepositoryImpl.class, RedisResourceStore.class,  ResourceDataService.class, EmbeddedRedisConfig.class})
        //RedisLayerIndexFacade.class, CacheProperties.class,CatalogImpl.class, GeoServerImpl.class})
public class RedisResourceNotificationTest extends AbstractResourceNotificationDispatcherTest {

    @Autowired
    private RedisResourceStore store;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Override
    protected ResourceNotificationDispatcher initWatcher() throws Exception {
        return store.getResourceNotificationDispatcher();
    }
}

