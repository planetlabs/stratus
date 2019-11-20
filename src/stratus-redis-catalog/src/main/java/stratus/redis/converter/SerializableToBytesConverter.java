/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.converter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.data.convert.WritingConverter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generic converter to handle arbitrary serializable classes in {@link org.geoserver.catalog.Info} metadata.
 * Primarily intended for classes added by optional extensions, which can't be handled normally.
 *
 * @see BytesToSerializableConverter
 */
@Slf4j
@WritingConverter
public class SerializableToBytesConverter implements ConditionalGenericConverter {

    /**
     * List of types as strings, so we can handle optional extensions
     */
    protected static List<String> SUPPORTED_TYPES = Arrays.asList(
            "org.geoserver.web.netcdf.NetCDFSettingsContainer");

    private Set<ConvertiblePair> convertibleTypes = null;

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return SUPPORTED_TYPES.contains(sourceType.getName());
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        if (this.convertibleTypes == null) {
            Set<ConvertiblePair> convertibleTypes = new HashSet<>();
            for (String type : SUPPORTED_TYPES) {
                try {
                    Class<?> sourceClass = Class.forName(type);
                    convertibleTypes.add(new ConvertiblePair(sourceClass, byte[].class));
                } catch (ClassNotFoundException e) {
                    //This is expected if netcdfout is not loaded
                    log.debug("Class "+type+" not found in classpath when initilizing converters", e);
                }
            }
            this.convertibleTypes = convertibleTypes;
        }
        return this.convertibleTypes;
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source instanceof Serializable) {
            return SerializationUtils.serialize((Serializable) source);
        }
        return null;
    }
}
