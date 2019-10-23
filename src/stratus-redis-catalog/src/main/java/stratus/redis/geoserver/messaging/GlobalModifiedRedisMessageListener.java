/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.messaging;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerFacade;
import org.geoserver.platform.GeoServerExtensions;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class listens to the geoServerGlobalModified channel on redis and will reload the global geoserver info object
 * if it has been changed on another instance.
 *
 * @author joshfix
 * Created on 5/22/18
 */
@Slf4j
@Service
public class GlobalModifiedRedisMessageListener implements MessageListener {

    private GeoServer geoserver;
    private GeoServerFacade facade;
    private GeoServerGlobalConfigurationListener listener;
    private Instant lastMessageReceived = Instant.now();
    private static ReentrantLock lock = new ReentrantLock();


    public GlobalModifiedRedisMessageListener() {
        geoserver = GeoServerExtensions.bean(GeoServer.class);
        facade = GeoServerExtensions.bean(GeoServerFacade.class);
        listener = GeoServerExtensions.bean(GeoServerGlobalConfigurationListener.class);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // if this instance sent the message that was just received, ignore it
        if (message.toString().equals(GeoServerGlobalConfigurationListener.ID)) {
            return;
        }

        // if we have already received a message to reload the global object in the last second, ignore this message.
        Instant now = Instant.now();
        if (Duration.between(lastMessageReceived, now).getSeconds() < 1) {
            return;
        }

        lastMessageReceived = now;

        // multiple threads can be executing and can cause the listener to become enabled when it shouldn't be
        lock.lock();
        try {
            listener.disable();
            geoserver.setGlobal(facade.getGlobal());
            listener.enable();
        } finally {
            lock.unlock();
        }
    }

}