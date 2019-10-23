/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import org.geoserver.wps.ProcessDismissedException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.util.ProgressListener;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Helper class that builds a intercepting proxy around feature collections, the proxy will start
 * throwing exceptions as soon as the ProgressListener is cancelled
 *
 * Ported to Stratus because the GeoServer version is protected
 *
 * @author Andrea Aime - GeoSolutions
 */
public class CancellingFeatureCollectionBuilder {

    public static SimpleFeatureCollection wrap(final FeatureCollection delegate,
                                               final ProgressListener listener) {
        InvocationHandler cancellingInvocationHandler = new CancellingFeatureCollectionBuilder.CancellingInvocationHandler(listener,
                delegate);

        Class[] interfaces;
        if (delegate instanceof SimpleFeatureCollection) {
            interfaces = new Class[] { SimpleFeatureCollection.class };
        } else {
            interfaces = new Class[] { FeatureCollection.class };
        }
        SimpleFeatureCollection proxy = (SimpleFeatureCollection) Proxy.newProxyInstance(
                CancellingFeatureCollectionBuilder.class.getClassLoader(), interfaces,
                cancellingInvocationHandler);

        return proxy;
    }

    private static class CancellingInvocationHandler implements InvocationHandler {

        ProgressListener listener;

        Object delegate;

        public CancellingInvocationHandler(ProgressListener listener, Object delegate) {
            this.listener = listener;
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (listener.isCanceled()) {
                throw new ProcessDismissedException(listener);
            }

            Object result = method.invoke(delegate, args);

            // wrap feature iterators into cancelling bits too
            if (result instanceof FeatureIterator<?>) {
                Class[] interfaces;
                if (result instanceof SimpleFeatureIterator) {
                    interfaces = new Class[] { SimpleFeatureIterator.class };
                } else {
                    interfaces = new Class[] { FeatureIterator.class };
                }
                result = Proxy.newProxyInstance(CancellingFeatureCollectionBuilder.class.getClassLoader(),
                        interfaces, new CancellingFeatureCollectionBuilder.CancellingInvocationHandler(listener, result));
            }

            return result;
        }

    }

}
