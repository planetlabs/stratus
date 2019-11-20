/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.upgrade.stratus_1_2;

import org.geoserver.wms.WMSInfoImpl;
import org.junit.Test;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import stratus.redis.upgrade.StratusUpgradingDeserializingConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;

/**
 * Tests deserialization of Stratus 1.2.0 WMSInfoImpl objects to {@link stratus.wms.redis.upgrade.stratus_1_2.WMSInfoImpl}
 * and upgrade to regular {@link WMSInfoImpl} objects, using serialized data from an actual Stratus 1.2.0 instance.
 */
public class WMSInfoImplUpgradeTest {

    private final JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer(
            new SerializingConverter(), new StratusUpgradingDeserializingConverter(new DeserializingConverter()));

    @Test
    public void testUpgradeGlobal() throws IOException {
        String serializedInfo = resourceAsString("wmsInfoGlobal.txt");
        WMSInfoImpl wmsInfo = (WMSInfoImpl) serializer.deserialize(Base64.getDecoder().decode(serializedInfo));
        assertNotNull(wmsInfo);
        assertNotNull(wmsInfo.getId());
        assertNotNull(wmsInfo.getTitle());
    }

    @Test
    public void testUpgradeWorkspace() {
        String serializedInfo = resourceAsString("wmsInfoWs.txt");
        WMSInfoImpl wmsInfo = (WMSInfoImpl) serializer.deserialize(Base64.getDecoder().decode(serializedInfo));
        assertNotNull(wmsInfo);
        assertNotNull(wmsInfo.getId());
        assertNotNull(wmsInfo.getTitle());
        assertNotNull(wmsInfo.getWorkspace());
    }

    private String resourceAsString(String resourceName) {
        InputStream is = getClass().getResourceAsStream(resourceName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

}
