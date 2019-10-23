/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.repository;

import stratus.redis.catalog.info.LayerInfoRedisImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * Created by joshfix on 9/20/16.
 */
public interface LayerRepository extends CatalogInfoCrudRepository<LayerInfoRedisImpl> {
	
	 LayerInfoRedisImpl findByResourceId(String id);
	 
	 List<LayerInfoRedisImpl> findListByResourceId(String id);

    /**
     * This repo method is meant to replace find findLayerByStyle catalog method. DefaultCatalogFacade in community
     * geoserver takes into account both the list of associated styles AND the default style when finding layers by
     * style. In order to take into account both then we need to do an OR style JPA repo method and USE THE SAME ID FOR
     * BOTH ARGUMENTS.<br/>
     *<br/>
     * <em>IF YOU AREN'T USING THE SAME ID FOR BOTH ARGS YOU ARE PROBABLY USING THIS METHOD WRONG!</em>
     *
     * @param id the style id to do reverse layer look up
     * @param defaultId THE SAME ID AS THE FIRST
     */
	 List<LayerInfoRedisImpl> findListByStyleIdsOrDefaultStyleId(String id, String defaultId);

	 Page<LayerInfoRedisImpl> findListByResourceId(String id, Pageable page);

	 Page<LayerInfoRedisImpl> findListByStyleIds(String id, Pageable page);
	
}