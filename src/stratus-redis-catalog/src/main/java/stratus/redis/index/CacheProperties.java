/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("stratus.catalog.redis.caching")
public class CacheProperties {
    private boolean useParallelQueries;

    public boolean getUseParallelQueries() {
        return useParallelQueries;
    }

    /**
     * If true {@link RedisLayerIndexFacade} will make individual parallel queries to redis when preloading the request cache
     * from redis. Otherwise, it will use grouped MULTI queries.
     *
     * This should be configured in application.yml; this method should never be called in code.
     *
     * @param useParallelQueries
     */
    public void setUseParallelQueries(boolean useParallelQueries) {
        this.useParallelQueries = useParallelQueries;
    }
}
