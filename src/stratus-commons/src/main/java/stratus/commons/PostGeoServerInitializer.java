package stratus.commons;

/**
 * Variant on {@link org.geoserver.config.GeoServerInitializer} which is loaded by BSE after the main
 * GeoServer context (usually in order deconflict circular dependencies.
 *
 * @author joshfix
 * Created on 11/13/19
 */
public interface PostGeoServerInitializer {
    void initialize() throws Exception;
}
