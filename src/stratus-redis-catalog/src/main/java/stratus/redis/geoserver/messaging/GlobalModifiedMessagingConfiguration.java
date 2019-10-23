/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Configures the necessary beans for creating a topic and publishing and subscribing via redis.
 *
 * @author joshfix
 * Created on 5/22/18
 */
@Slf4j
@Configuration
public class GlobalModifiedMessagingConfiguration {

    public final static String TOPIC_NAME = "geoServerGlobalModified";

    @Bean(TOPIC_NAME)
    public ChannelTopic topic() {
        return new ChannelTopic(TOPIC_NAME);
    }

    @Bean
    MessageListenerAdapter messageListener(RedisMessageListenerContainer container) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new GlobalModifiedRedisMessageListener());
        container.addMessageListener(messageListenerAdapter, topic());
        return new MessageListenerAdapter(new GlobalModifiedRedisMessageListener());
    }

}