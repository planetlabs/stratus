/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.repository;

import stratus.redis.geoserver.info.GeoServerInfoRedisImpl;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author joshfix
 * Created on 6/5/18
 */
public interface GeoServerInfoRepository extends PagingAndSortingRepository<GeoServerInfoRedisImpl, String> {}