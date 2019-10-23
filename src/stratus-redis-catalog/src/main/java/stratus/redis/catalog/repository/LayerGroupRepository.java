/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;


import stratus.redis.catalog.info.LayerGroupInfoRedisImpl;


/**
 * Created by joshfix on 9/20/16.
 */
public interface LayerGroupRepository 
	extends CatalogInfoCrudRepository<LayerGroupInfoRedisImpl>, CatalogInfoByWorkspace<LayerGroupInfoRedisImpl>
{}