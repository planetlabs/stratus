/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.geoserver.platform.resource.ResourceListener;
import org.geoserver.platform.resource.ResourceNotification;
import org.geoserver.platform.resource.ResourceNotificationDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Notification dispatcher for Redis resources
 */
@Slf4j
@Service("redisNotificationDispatcher")
public class RedisNotificationDispatcher implements MessageListener, ResourceNotificationDispatcher {

    private Map<String, List<ResourceListener>> handlers = new HashMap<>();

    @Autowired
    @Setter
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    @Setter
    @Qualifier("redisMessageListenerContainer")
    private RedisMessageListenerContainer messageContainer;

    @Override
    public void addListener(String resource, ResourceListener listener) {
        List<ResourceListener> listeners = this.handlers.getOrDefault(resource, new ArrayList<>());
        listeners.add(listener);
        handlers.put(resource, listeners);
        messageContainer.addMessageListener(new MessageListenerProxy(resource, this), new ChannelTopic(resource));
    }

    @Override
    public boolean removeListener(String resource, ResourceListener listener) {
        List<ResourceListener> listeners = this.handlers.getOrDefault(resource, new ArrayList<>());
        boolean removed = listeners.remove(listener);
        if (listeners.isEmpty()) {
            messageContainer.removeMessageListener(new MessageListenerProxy(resource, this), new ChannelTopic(resource));
        }
        return removed;
    }

    @Override
    public void changed(ResourceNotification notification) {
        if (StringUtils.isNotEmpty(notification.getPath())) {
            this.handleNotification(notification);
            redisTemplate.convertAndSend(notification.getPath(), this.serialize(
                    new RedisResourceNotification(notification)));
        }
    }

    private Object serialize(RedisResourceNotification notification) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            log.error("Error serializing resource notification", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            RedisResourceNotification redisResourceNotification = mapper.readValue(message.getBody(),
                    RedisResourceNotification.class);

            this.handleNotification(redisResourceNotification.getResourceNotification());
        } catch (IOException e) {
            log.error("Error deserializing resource notification", e);
            throw new RuntimeException(e);
        }
    }

    private void handleNotification(ResourceNotification notification) {
        handleNotificationInternal(notification);

        //if delete, propagate delete notifications to children, which can be found in the events (see {@link createEvents})
        if (notification.getKind() == ResourceNotification.Kind.ENTRY_DELETE) {
            for (ResourceNotification.Event event : notification.events()) {
                if (!notification.getPath().equals(event.getPath())) {
                    this.handleNotificationInternal(new ResourceNotification(event.getPath(), ResourceNotification.Kind.ENTRY_DELETE,
                            notification.getTimestamp(), Collections.emptyList()));
                }
            }
        }

        //if create, propagate CREATE events to its created parents, which can be found in the events (see {@link createEvents})
        Set<String> createdParents = new HashSet<>();
        if (notification.getKind() == ResourceNotification.Kind.ENTRY_CREATE) {
            for (ResourceNotification.Event event : notification.events()) {
                if (!notification.getPath().equals(event.getPath())) {
                    createdParents.add(event.getPath());
                }
            }
        }

        //propagate any event to its direct parent (as MODIFY if not a created parent)
        String path = Paths.parent(notification.getPath());
        while (StringUtils.isNotEmpty(path)) {
            boolean isCreate = createdParents.contains(path);
            ResourceNotification newNotification = new ResourceNotification(path,
                    isCreate ? ResourceNotification.Kind.ENTRY_CREATE : ResourceNotification.Kind.ENTRY_MODIFY,
                    notification.getTimestamp(), notification.events());
            this.handleNotificationInternal(newNotification);
            redisTemplate.convertAndSend(newNotification.getPath(), this.serialize(
                    new RedisResourceNotification(newNotification)));

            //stop propagating after first modify
            path = isCreate ? Paths.parent(path) : null;
        }
    }

    private synchronized void handleNotificationInternal(ResourceNotification notification) {
        List<ResourceListener> originalListeners = handlers.get(notification.getPath());
        //Copy list, since some handlers try to remove themselves on notifications
        if (originalListeners != null) {
            List<ResourceListener> listeners = new ArrayList<>(originalListeners);

            for (ResourceListener listener : listeners) {
                listener.changed(notification);
            }
        }
    }

    /**
     * Just a basic proxy for the message listener. RedisNotificationDispatcher can't easily implement hashCode/equals
     * and thus can't easily be removed from the list of redis pubsub scubscribers. This is an equal-able proxy.
     */
    static class MessageListenerProxy implements MessageListener {

        private String path;
        private RedisNotificationDispatcher delegate;

        public MessageListenerProxy(String path, RedisNotificationDispatcher delegate) {
            this.path = path;
            this.delegate = delegate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MessageListenerProxy that = (MessageListenerProxy) o;

            return path.equals(that.path);

        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }

        @Override
        public void onMessage(Message message, byte[] pattern) {
            delegate.onMessage(message, pattern);
        }
    }
}