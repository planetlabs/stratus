/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic converter to handle arbitrary serializable classes in {@link org.geoserver.catalog.Info} metadata.
 * Primarily intended for classes added by optional extensions, which can't be handled normally.
 *
 * @see SerializableToBytesConverter
 * @see SerializableToBytesConverter#SUPPORTED_TYPES
 */
@Slf4j
@ReadingConverter
public class BytesToSerializableConverter implements ConditionalGenericConverter {

    private Set<ConvertiblePair> convertibleTypes = null;

    public Serializable convert(byte[] bytes) {
        Object obj = SerializationUtils.deserialize(bytes);
        if (obj instanceof Serializable) {
            return (Serializable) obj;
        }
        return null;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return SerializableToBytesConverter.SUPPORTED_TYPES.contains(targetType.getName());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        if (this.convertibleTypes == null) {
            Set<ConvertiblePair> convertibleTypes = new HashSet<>();
            for (String type : SerializableToBytesConverter.SUPPORTED_TYPES) {
                try {
                    Class<?> targetClass = Class.forName(type);
                    convertibleTypes.add(new ConvertiblePair(byte[].class, targetClass));
                } catch (ClassNotFoundException e) {
                    //This is expected if netcdfout is not loaded
                    log.debug("Class "+type+" not found in classpath when initializing converters", e);
                }
            }
            this.convertibleTypes = convertibleTypes;
        }
        return this.convertibleTypes;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source instanceof byte[]) {
            Object obj = SerializationUtils.deserialize((byte[]) source);
            if (obj instanceof Serializable) {
                return obj;
            }
        }
        return null;
    }
}
