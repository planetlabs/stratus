/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.converter;

import org.geotools.jdbc.VirtualTable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.util.SerializationUtils;

/**
 * @author joshfix
 * Created on 11/2/17
 */
@ReadingConverter
public class BytesToVirtualTableConverter implements Converter<byte[], VirtualTable> {

    public VirtualTable convert(byte[] bytes) {
        Object obj = SerializationUtils.deserialize(bytes);
        if (obj instanceof VirtualTable) {
            return (VirtualTable)obj;
        }
        return null;
    }
}
