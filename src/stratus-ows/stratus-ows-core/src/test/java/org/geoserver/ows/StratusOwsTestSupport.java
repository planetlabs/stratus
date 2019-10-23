/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.ows;

import stratus.ows.OWSCachingCallback;
import stratus.ows.OWSCachingException;
import stratus.ows.OWSCachingHandler;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.test.GeoServerTestApplicationContext;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StratusOwsTestSupport {

    /**
     * Because the OWS tests load a custom context which ignores our autowired beans, we need to hack in some values
     *
     * @param applicationContext
     * @param owsCachingCallback
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void configureOwsCachingCallback(GeoServerTestApplicationContext applicationContext, OWSCachingCallback owsCachingCallback) throws NoSuchFieldException, IllegalAccessException {
        Dispatcher dispatcher = GeoServerExtensions.bean(Dispatcher.class);
        applicationContext.getBeanFactory().registerSingleton("owsCachingCallback", owsCachingCallback);
        dispatcher.initApplicationContext(applicationContext);

        Field callbacksField = Dispatcher.class.getDeclaredField("callbacks");
        callbacksField.setAccessible(true);
        List<DispatcherCallback> callbacks = (List<DispatcherCallback>) callbacksField.get(dispatcher);
        callbacks.add(owsCachingCallback);
    }

    public static void assertOwsCachingCallbackConfigured() throws NoSuchFieldException, IllegalAccessException {
        assertEquals(1, GeoServerExtensions.extensions(OWSCachingCallback.class).size());
        OWSCachingCallback owsCachingCallback = GeoServerExtensions.extensions(OWSCachingCallback.class).get(0);

        Dispatcher dispatcher = GeoServerExtensions.bean(Dispatcher.class);
        Field callbacksField = Dispatcher.class.getDeclaredField("callbacks");
        callbacksField.setAccessible(true);
        List<DispatcherCallback> callbacks = (List<DispatcherCallback>) callbacksField.get(dispatcher);

        assertTrue(callbacks.contains(owsCachingCallback));
    }

    public static class DummyCachingHandler extends OWSCachingHandler {
        public boolean wasHandled = false;

        @Override
        public void handle(String serviceName, String versionName, String requestName, String virtualWsName, String virtualLayerName, Request request) throws OWSCachingException {
            wasHandled = true;
        }
    }
}
