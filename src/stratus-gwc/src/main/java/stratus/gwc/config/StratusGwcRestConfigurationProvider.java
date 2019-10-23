/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.GeoServerTileLayerRedisInfoImpl;
import stratus.gwc.redis.data.RegexParameterFilterRedis;
import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.gwc.layer.GeoServerTileLayerInfo;
import org.geowebcache.config.ContextualConfigurationProvider;
import org.geowebcache.config.Info;
import org.springframework.stereotype.Component;

/**
 * Tells XStream to process the annotations found in the GeoServerTileLayer classes.
 * See: {@link stratus.gwc.redis.data.GeoServerTileLayerRedisInfoImpl},
 * {@link stratus.gwc.redis.data.RegexParameterFilterRedis},
 *
 * @author joshfix
 * Created on 5/21/18
 */
@Slf4j
@Component
public class StratusGwcRestConfigurationProvider implements ContextualConfigurationProvider {

    @Override
    public boolean appliesTo(Context ctxt) {
        return Context.REST==ctxt;
    }

    @Override
    public XStream getConfiguredXStream(XStream xs) {
        xs.processAnnotations(GeoServerTileLayerRedisInfoImpl.class);
        xs.processAnnotations(RegexParameterFilterRedis.class);
        xs.addDefaultImplementation(GeoServerTileLayerRedisInfoImpl.class, GeoServerTileLayerInfo.class);
        return xs;
    }

    @Override
    public boolean canSave(Info i) {
        return false;
    }
}
