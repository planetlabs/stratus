/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.repository.BlobStoreRepository;
import stratus.gwc.redis.repository.GeoServerTileLayerRepository;
import stratus.gwc.redis.repository.GridSetRepository;
import stratus.gwc.redis.repository.ServerRepository;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.repository.RedisRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utility class for Redis GWC tests
 *
 * @see stratus.redis.RedisFacadeTestSupport
 */
public class RedisGWCTestSupport {

    @Autowired
    public RedisConfigProps configProps;

    @Autowired
    public RedisRepositoryImpl repository;
    @Autowired
    public ServerRepository serverRepository;
    @Autowired
    public GeoServerTileLayerRepository tlRepository;
    @Autowired
    public GridSetRepository gsRepository;
    @Autowired
    public BlobStoreRepository bsRepository;
}
