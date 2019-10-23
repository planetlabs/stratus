/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade.stratus_1_2;

import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.DefaultDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Variant of {@link DefaultDeserializer} that delegates to {@link Stratus_1_2_ObjectInputStream} instead of
 * {@link org.springframework.core.ConfigurableObjectInputStream}.
 */
public class Stratus_1_2_Deserializer extends DefaultDeserializer {

    ClassLoader classLoader;

    public Stratus_1_2_Deserializer() {
        this.classLoader = null;
    }

    @Override
    @SuppressWarnings("resource")
    public Object deserialize(InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new Stratus_1_2_ObjectInputStream(inputStream, classLoader);
        try {
            return objectInputStream.readObject();
        } catch (ClassNotFoundException ex) {
            throw new NestedIOException("Failed to deserialize object type", ex);
        }
    }
}
