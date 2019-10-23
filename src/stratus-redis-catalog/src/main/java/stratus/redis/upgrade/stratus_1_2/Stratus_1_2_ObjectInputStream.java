/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade.stratus_1_2;

import org.springframework.core.ConfigurableObjectInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

/**
 * A variant of {@link ConfigurableObjectInputStream} which overrides {@link #resolveClass(ObjectStreamClass)} to
 * return specific Stratus 1.2 compatibility classes where applicable
 */
public class Stratus_1_2_ObjectInputStream extends ConfigurableObjectInputStream {

    public Stratus_1_2_ObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in, classLoader);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        Class<?> clazz = loadClassInternal(classDesc.getName());

        if (clazz == null) {
          clazz = super.resolveClass(classDesc);
        }
        return clazz;
    }

    private synchronized Class<?> loadClassInternal(String name) throws ClassNotFoundException {
        switch (name) {
            case "org.geoserver.wms.WMSInfoImpl":
                return Class.forName("stratus.wms.redis.upgrade.stratus_1_2.WMSInfoImpl");
            case "org.geoserver.catalog.impl.ModificationProxy":
                return Class.forName("stratus.redis.upgrade.stratus_1_2.ModificationProxy");
            default:
                return null;
        }
    }
}
