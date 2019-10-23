/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.info;

import org.springframework.data.redis.core.RedisHash;

/**
 * Non-abstract extension of {@link AbstractServiceInfoRedisImpl}
 *
 * Used for generic ServiceInfo objects, while avoiding conflicting with other classes/repositories that
 * extend ServiceInfoRedisImpl
 *
 * Uses "ServiceInfoImpl" as a hash key so as to not conflict with {@link stratus.redis.geoserver.repository.ServiceInfoWrapperRepository}
 */
@RedisHash("ServiceInfoImpl")
public class ServiceInfoRedisImpl extends AbstractServiceInfoRedisImpl {
}
