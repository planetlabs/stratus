/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.Info;
import org.geoserver.ows.util.OwsUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.rmi.server.UID;
import java.util.*;

/**
 *
 * @author joshfix
 */
public class RedisCatalogUtils {

    private static String REDIS_IMPORT_PROP = "redis.import";
    private static String REDIS_IMPORT_CATALOG = "catalog";
    private static String REDIS_IMPORT_STORE = "store";
    private static String REDIS_IMPORT_BOTH = "both";
	
    public static <T extends Info> String buildKey(Class<T> clazz, String key, String value) {
        // TODO check if we can break things with keys or values containing colons.
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(clazz.getSimpleName());
        
        if(Objects.nonNull(key)) {
            stringBuilder
                .append(":")
                .append(key);
        }

        // certain redis collections, like the list of ids, won't have the value appended at the end
        if (value != null) {
            stringBuilder
                    .append(":")
                    .append(value);
        }
        return stringBuilder.toString();
    }

    public static String getIdFromSet(Set<Object> set) {
        Assert.notNull(set);
        if (set.size() > 1) {
            // TODO: what exception to throw?
            throw new RuntimeException("repository returned more than one result");
        } else if (set.isEmpty()) {
            return null;
        }

        return (String) set.iterator().next();
    }

    public static <T extends Info> List<T> parseResultMap(Map<Object, Object> results, Class<T> clazz) {
        Collection<Object> values = results.values();
        List<T> resultList = new ArrayList<>();
        values.stream().forEach((value) -> {
        	if (clazz.isInstance(value)) {
        		resultList.add(clazz.cast(value));
        	}
        });
        return resultList;
    }

    public static void setId(Info info, Class<? extends Info> type) {
        final String curId = info.getId();
        if (null == curId) {
            final String uid = new UID().toString();
            final String id = type.getSimpleName() + "." + uid;
            OwsUtils.set(info, "id", id);
        }
    }
        
    public static boolean isImportCatalog() {
    	String importProp = System.getProperty(REDIS_IMPORT_PROP);
    	return REDIS_IMPORT_CATALOG.equalsIgnoreCase(importProp) || REDIS_IMPORT_BOTH.equalsIgnoreCase(importProp);
    }
    
    public static boolean isImportStore() {
    	String importProp = System.getProperty(REDIS_IMPORT_PROP);
    	return REDIS_IMPORT_STORE.equalsIgnoreCase(importProp) || REDIS_IMPORT_BOTH.equalsIgnoreCase(importProp);
    }
    
    public static File lookupGeoServerDataDirectory() {        
        final String[] varStrs = { "GEOSERVER_DATA_DIR", "GEOSERVER_DATA_ROOT" };
        
        // Loop over variable names
        for (String varStr : varStrs) {

            // Loop over variable access methods
            for (int j = 0; j < 2; j++) {
                String value = null;

                // Lookup section
                switch (j) {
                    case 0:
                        value = System.getProperty(varStr);
                        break;
                    case 1:
                        value = System.getenv(varStr);
                        break;
                }

                // Verify section
                if (value != null && !"".equals(value)) {
                    File fh = new File(value);

                    if (fh.exists() && fh.isDirectory()) {
                        // Sweet, we can work with this
                        return fh;
                    }
                }
            }
        }
        
        //fall back to current directory
        return new File(".").getAbsoluteFile();
    }

}
