/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade.stratus_1_2;

import org.geoserver.catalog.impl.WrappingProxy;
import org.geoserver.ows.util.ClassProperties;
import org.geoserver.ows.util.OwsUtils;
import stratus.redis.upgrade.Upgradable;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Minimal copy of ModificationProxy for handling deserialization upgrades
 */
public class ModificationProxy implements WrappingProxy, Serializable, Upgradable< org.geoserver.catalog.impl.ModificationProxy> {
   static final long serialVersionUID = -8186570459980422193L;

    /**
     * the proxy object
     */
    Object proxyObject;

    /**
     * reflection helper
     */
    transient ClassProperties cp;

    /**
     * "dirty" properties
     */
    HashMap<String,Object> properties;

    /**
     * The old values of the live collections (we have to clone them because once
     * the proxy commits the original map will contain the same values as the new one,
     * breaking getOldValues()
     */
    HashMap<String,Object> oldCollectionValues;

    public ModificationProxy(Object proxyObject) {
        this.proxyObject = proxyObject;
    }

    private ClassProperties cp(){
        if(cp == null){
            this.cp = OwsUtils.getClassProperties(proxyObject.getClass());
        }
        return cp;
    }

    public Object getProxyObject() {
        return proxyObject;
    }

    public HashMap<String,Object> getProperties() {
        return properties();
    }
    HashMap<String,Object> properties() {
        if ( properties != null ) {
            return properties;
        }

        synchronized (this) {
            if ( properties != null ) {
                return properties;
            }

            properties = new HashMap<String,Object>();
        }

        return properties;
    }

    @Override
    public org.geoserver.catalog.impl.ModificationProxy upgrade() {
        org.geoserver.catalog.impl.ModificationProxy upgraded = new org.geoserver.catalog.impl.ModificationProxy(this.proxyObject);
        upgraded.getProperties().putAll(properties());

        return upgraded;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException("Stratus 1.2 compatibility class for \"org.geoserver.catalog.impl.ModificationProxy\" should never be invoked");
    }
}
