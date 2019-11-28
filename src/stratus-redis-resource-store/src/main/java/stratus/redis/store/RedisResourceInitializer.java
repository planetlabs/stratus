/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.platform.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;
import stratus.redis.repository.RedisRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by joshfix on 11/9/16.
 */
@Slf4j
@Service
public class RedisResourceInitializer {

    private RedisResourceStore store;
    private ConfigurableEnvironment env;
    private final static String RESOURCE_PREFIX = "stratus.store.resource";

    @Getter
    @Autowired
    private ResourceInitializationConfigProps configProps;

	public void setRedisResourceStore(RedisResourceStore store) {
		this.store = store;
	}

	public RedisResourceStore getRedisResourceStore() { return this.store; }
    
    @Autowired
    @Lazy        
	public void setConfigurableEnvironment(ConfigurableEnvironment env) {
		this.env = env;
	}

    public void init() {
        RedisRepository repository = store.getDataService().getRepository();
        // initialize empty security directory -- needs to be present for AbstractAccessRulesDAO to initialize security/layers.properties.
        String hashKey = store.getDataService().hashKey("security");
        if (!repository.keyExists(hashKey)) {
            store.getDataService().saveDirectory("security", new Date());
        }

        MutablePropertySources sources = env.getPropertySources();
        for (PropertySource<?> propertySource : sources) {

            if (!(propertySource instanceof MapPropertySource)) {
                continue;
            }

            MapPropertySource mapPropertySource = (MapPropertySource) propertySource;
            String[] propertyNames = mapPropertySource.getPropertyNames();
            for (String key : propertyNames) {

                if (!key.startsWith(RESOURCE_PREFIX)) {
                    continue;
                }

                Object valueObject = mapPropertySource.getProperty(key);
                if (null == valueObject || !(valueObject instanceof String)) {
                    continue;
                }

                String value = (String) valueObject;
                String path = key.substring(RESOURCE_PREFIX.length() + 1);

                Resource resource = store.get(path);
                if (resource.getType() == Resource.Type.UNDEFINED || configProps.isOverwriteResources()) {
                    try (OutputStream out = resource.out()) {
                        log.debug("Initializing GeoServer resource.  Path: " + path + " - Value: " + value);
                        out.write(value.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException ioe) {
                        log.error("Error writing resource " + path + " with value " + value + " to redis.");
                    }
                }
            }
        }
    }
}
