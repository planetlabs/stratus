/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade;

import stratus.redis.upgrade.stratus_1_2.Stratus_1_2_Deserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.io.ByteArrayInputStream;
import java.io.InvalidClassException;

/**
 * A {@link DeserializingConverter} that checks for {@link InvalidClassException}s during deserialization, and tries
 * to deserialize against older version of certain Stratus catalog classes to work around these failures.
 */
@Slf4j
public class StratusUpgradingDeserializingConverter extends DeserializingConverter {

    private DeserializingConverter delegate;
    private Stratus_1_2_Deserializer stratus_1_2_deserializer = new Stratus_1_2_Deserializer();

    public  StratusUpgradingDeserializingConverter(DeserializingConverter delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object convert(byte[] source) {
        try {
            return delegate.convert(source);
        } catch ( SerializationFailedException sfe ) {
            //Deserialization failed due to an incompatible class
            if (InvalidClassException.class.isAssignableFrom(sfe.getCause().getClass())) {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(source);
                try {
                    Object legacyObject = this.stratus_1_2_deserializer.deserialize(byteStream);
                    if (log.isDebugEnabled()) {
                        log.debug("Upgrading Stratus 1.2 serialized \"" + legacyObject.getClass() +
                                "\" instance for compatibility with current Stratus version.");
                    }
                    if (legacyObject instanceof Upgradable) {
                        return ((Upgradable) legacyObject).upgrade();
                    }
                    return legacyObject;
                } catch (Throwable ex) {
                    ex.addSuppressed(sfe);
                    throw new SerializationFailedException("Failed to deserialize payload in Stratus 1.2 compatibility mode." +
                            "Is the byte array a result of corresponding serialization for " +
                            this.stratus_1_2_deserializer.getClass().getSimpleName() + "?", ex);

                    //Add more version-specific deserializers as needed...
                }
            } else {
                throw sfe;
            }

        }
    }

}
