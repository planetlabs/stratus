/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import org.geoserver.catalog.Catalog;
import org.geoserver.config.DefaultGeoServerLoader;
import org.geoserver.config.util.XStreamPersister;
import org.geoserver.platform.GeoServerResourceLoader;
import stratus.redis.catalog.RedisCatalogUtils;

public class DataDirectoryGeoServerLoader extends DefaultGeoServerLoader {

	public DataDirectoryGeoServerLoader() {
		super(new GeoServerResourceLoader(RedisCatalogUtils.lookupGeoServerDataDirectory()));
	}
	
	protected void readCatalog(Catalog catalog, XStreamPersister xp) throws Exception {
		//this is just a bit of a hack to get this method accessible from RedisGeoServerLoader
		super.readCatalog(catalog, xp);
	}
}
