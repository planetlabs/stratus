/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.messaging;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.config.ConfigurationListenerAdapter;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.platform.GeoServerExtensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Listens to postGlobalChange events and publishes a message to redis.
 *
 * @author joshfix
 * Created on 5/22/18
 */
@Data
@Slf4j
@Service
@SuppressWarnings("SpringJavaAutowiringInspection")
public class GeoServerGlobalConfigurationListener extends ConfigurationListenerAdapter {

    @Autowired
    private GeoServer geoserver;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Qualifier(GlobalModifiedMessagingConfiguration.TOPIC_NAME)
    private ChannelTopic topic;

    private boolean disabled;
    private Instant lastMessagePublished = Instant.now();
    public final static String ID = UUID.randomUUID().toString();

    public GeoServerGlobalConfigurationListener() {
        GeoServerExtensions.bean(GeoServer.class).addListener(this);
    }

    @Override
    public void handlePostGlobalChange(GeoServerInfo global) {
        // if this instance just updated the global object due to a published message, do not publish again
        if (disabled) {
            return;
        }

        // geoserver seems to call things multiple times.  instead of repeatedly publishing messages, don't publish more
        // than 1 per second, as the back-to-back modification events stem from the same user action/settings update
        Instant now = Instant.now();
        if (Duration.between(lastMessagePublished, now).getSeconds() < 1) {
            return;
        }

        lastMessagePublished = now;
        redisTemplate.convertAndSend(topic.getTopic(), ID);
    }

    public void disable() {
        disabled = true;
    }

    public void enable() {
        disabled = false;
    }
}