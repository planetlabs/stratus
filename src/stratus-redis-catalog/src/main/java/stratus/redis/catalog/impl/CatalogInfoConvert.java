/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.impl;

import org.geoserver.catalog.*;
import org.geoserver.catalog.impl.*;
import org.springframework.beans.BeanUtils;
import stratus.redis.catalog.info.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class CatalogInfoConvert {
    
    private CatalogInfoConvert() {}

    private static final Class<?>[] ROOT_CLASSES = new Class<?>[] {
            MapInfo.class,
            NamespaceInfo.class,
            LayerGroupInfo.class,
            LayerInfo.class,
            CoverageInfo.class,
            FeatureTypeInfo.class,
            WMSLayerInfo.class,
            DataStoreInfo.class,
            CoverageStoreInfo.class,
            WMSStoreInfo.class,
            StyleInfo.class,
            WorkspaceInfo.class,
            WMTSStoreInfo.class,
            WMTSLayerInfo.class
    };

    private static Map<Class<? extends CatalogInfo>, Class<?>> INFO_REDIS_CLASS_MAPPINGS = new HashMap<>();
    
    private static Map<Class<? extends CatalogInfo>, Class<?>> INFO_TRAD_CLASS_MAPPINGS = new HashMap<>();

    static {
        INFO_REDIS_CLASS_MAPPINGS.put(WorkspaceInfo.class, WorkspaceInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(DataStoreInfo.class, DataStoreInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(CoverageStoreInfo.class, CoverageStoreInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(WMSStoreInfo.class, WMSStoreInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(NamespaceInfo.class, NamespaceInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(FeatureTypeInfo.class, FeatureTypeInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(CoverageInfo.class, CoverageInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(WMSLayerInfo.class, WMSLayerInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(LayerInfo.class, LayerInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(StyleInfo.class, StyleInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(LayerGroupInfo.class, LayerGroupInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(WMTSStoreInfo.class, WMTSStoreInfoRedisImpl.class);
        INFO_REDIS_CLASS_MAPPINGS.put(WMTSLayerInfo.class, WMTSLayerInfoRedisImpl.class);
        
        INFO_TRAD_CLASS_MAPPINGS.put(WorkspaceInfo.class, WorkspaceInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(DataStoreInfo.class, DataStoreInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(CoverageStoreInfo.class, CoverageStoreInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(WMSStoreInfo.class, WMSStoreInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(NamespaceInfo.class, NamespaceInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(FeatureTypeInfo.class, FeatureTypeInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(CoverageInfo.class, CoverageInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(WMSLayerInfo.class, WMSLayerInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(LayerInfo.class, LayerInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(StyleInfo.class, StyleInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(LayerGroupInfo.class, LayerGroupInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(WMTSLayerInfo.class, WMTSLayerInfoImpl.class);
        INFO_TRAD_CLASS_MAPPINGS.put(WMTSStoreInfo.class, WMTSStoreInfoImpl.class);
    }

    @SuppressWarnings("unchecked")
    public static final Class<? extends CatalogInfo> root(Class<? extends CatalogInfo> clazz) {
        for (Class<?> rootClass : ROOT_CLASSES) {
            if (rootClass.isAssignableFrom(clazz)) {
                return (Class<? extends CatalogInfo>) rootClass;
            }
        }
        return null;
    }
    
    public static final <T extends CatalogInfo> T toRedis(CatalogInfo info) {
        return convert(info, redisClass(info.getClass()));
    }
    
    public static final <T extends CatalogInfo> T toTraditional(CatalogInfo info) {
        return convert(info, traditionalClass(info.getClass()));
    }

    @SuppressWarnings("unchecked")
    private static final <T extends CatalogInfo> Class<T> redisClass(Class<? extends CatalogInfo> clazz) {
        return (Class<T>) INFO_REDIS_CLASS_MAPPINGS.get(root(clazz));
    }
    
    @SuppressWarnings("unchecked")
    private static final <T extends CatalogInfo> Class<T> traditionalClass(Class<? extends CatalogInfo> clazz) {
        return (Class<T>) INFO_TRAD_CLASS_MAPPINGS.get(root(clazz));
    }
    
    private static final <T extends CatalogInfo> T convert(CatalogInfo info, Class<T> clazz) {
        T newInfo;
        try {
            if (INFO_TRAD_CLASS_MAPPINGS.values().contains(clazz) &&
                    (StoreInfoImpl.class.isAssignableFrom(clazz) || ResourceInfoImpl.class.isAssignableFrom(clazz) || StyleInfoImpl.class.isAssignableFrom(clazz))) {
                //Default constructor is protected, pass in null catalog
                newInfo = clazz.getDeclaredConstructor(Catalog.class).newInstance((Catalog)null);
            } else {
                newInfo = clazz.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(e); //shouldn't happen
        }

        //hackadeedodoo - for LayerInfoImpl, resource must be set before certain other properties
        if (clazz == LayerInfoImpl.class) {
            ((LayerInfoImpl) newInfo).setResource(((LayerInfo) info).getResource());
        }
        
        BeanUtils.copyProperties(info, newInfo);

        return newInfo;
    }
}
