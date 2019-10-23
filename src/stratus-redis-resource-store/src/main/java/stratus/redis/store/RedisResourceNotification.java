/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.geoserver.platform.resource.ResourceNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simpler POJO to facilitate serializing ResourceNotification for pubsub
 */
@JsonIgnoreProperties(value = {"resourceNotification"})
@Data
public class RedisResourceNotification {
    private ResourceNotification.Kind kind;
    private String path;
    private long timestamp;
    private List<RedisResourceEvent> delta = new ArrayList<>();

    @JsonCreator
    public RedisResourceNotification(
            @JsonProperty("kind") ResourceNotification.Kind kind,
            @JsonProperty("path") String path,
            @JsonProperty("timestamp") long timestamp,
            @JsonProperty("delta") List<RedisResourceEvent> delta) {

        this.kind = kind;
        this.path = path;
        this.timestamp = timestamp;
        this.delta.addAll(delta);
    }

    public RedisResourceNotification(ResourceNotification notification) {
        kind = notification.getKind();
        path = notification.getPath();
        timestamp = notification.getTimestamp();
        delta.addAll(notification.events().stream().map(RedisResourceEvent::new).collect(Collectors.toList()));
    }

    public ResourceNotification getResourceNotification() {
        return new ResourceNotification(path, kind, timestamp, delta.stream().map(
                e -> new ResourceNotification.Event(e.getPath(), e.getKind())).collect(Collectors.toList()));
    }

    public static class RedisResourceEvent {
        String path;
        ResourceNotification.Kind kind;

        public RedisResourceEvent() {

        }

        public RedisResourceEvent(ResourceNotification.Event event) {
            this.path = event.getPath();
            this.kind = event.getKind();
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public ResourceNotification.Kind getKind() {
            return kind;
        }

        public void setKind(ResourceNotification.Kind kind) {
            this.kind = kind;
        }
    }
}
