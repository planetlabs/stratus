/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.converter;

import org.apache.commons.lang.SerializationUtils;
import org.geotools.jdbc.VirtualTable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * @author joshfix
 * Created on 11/2/17
 */
@WritingConverter
public class VirtualTableToBytesConverter implements Converter<VirtualTable, byte[]> {

    public byte[] convert(VirtualTable virtualTableRedis) {
        return SerializationUtils.serialize(virtualTableRedis);
    }
}
