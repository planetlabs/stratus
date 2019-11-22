/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.RequiredArgsConstructor;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.SLDHandler;
import org.geoserver.catalog.StyleHandler;
import org.geoserver.catalog.Styles;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.rest.CallbackInterceptor;
import org.geoserver.rest.MediaTypeCallback;
import org.geoserver.rest.RestInterceptor;
import org.geoserver.rest.converters.*;
import org.geotools.util.Version;
import org.geowebcache.rest.converter.GWCConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;
import org.xml.sax.EntityResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Comparator;
import java.util.List;

/**
 * Alternative implementation of {@link org.geoserver.rest.RestConfiguration} that extends from
 * WebMvcConfigurerAdapater instead of WebMvcConfigurationSupport.  The latter prohibits Spring's
 * WebMvcAutoConfiguration from instantiating and configuring static resource handlers.
 *
 * @author joshfix
 * Created on 6/26/18
 */
@Component
@RequiredArgsConstructor
public class CommunityRestMvcConfigurer implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Catalog catalog = (Catalog) applicationContext.getBean("catalog");

        List<BaseMessageConverter> gsConverters = GeoServerExtensions.extensions(BaseMessageConverter.class);

        //Add default converters
        gsConverters.add(new FreemarkerHTMLMessageConverter("UTF-8"));
        gsConverters.add(new XStreamXMLMessageConverter());
        gsConverters.add(new XStreamJSONMessageConverter());
        gsConverters.add(new XStreamCatalogListConverter.XMLXStreamListConverter());
        gsConverters.add(new XStreamCatalogListConverter.JSONXStreamListConverter());
        gsConverters.add(new InputStreamConverter());

        //Deal with the various Style handler
        EntityResolver entityResolver = catalog.getResourcePool().getEntityResolver();
        for (StyleHandler sh : Styles.handlers()) {
            for (Version ver : sh.getVersions()) {
                gsConverters.add(new StyleReaderConverter(sh.mimeType(ver), ver, sh, entityResolver));
                gsConverters.add(new StyleWriterConverter(sh.mimeType(ver), ver, sh));
            }
        }

        //Sort the converters based on ExtensionPriority
        gsConverters.sort(Comparator.comparingInt(BaseMessageConverter::getPriority));
        //Remove any duplicates
        converters.removeIf(gsConverters::contains);
        for (int i = 0; i < gsConverters.size(); i++) {
            //Add to front of list
            converters.add(i, gsConverters.get(i));
        }

        //Add GWC REST converter (add it to the front, since it has stricter constraints than the defalt GS XML converters)
        if (applicationContext.containsBean("gwcConverter")) {
            GWCConverter gwcConverter = (GWCConverter) applicationContext.getBean("gwcConverter");
            converters.remove(gwcConverter);
            converters.add(0, gwcConverter);
        }

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // add all configured Spring Converter classes to allow extension/pluggability
        for (Converter converter : GeoServerExtensions.extensions(Converter.class)) {
            registry.addConverter(converter);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RestInterceptor());
        registry.addInterceptor(new CallbackInterceptor());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // scan and register media types for style handlers
        List<StyleHandler> styleHandlers = GeoServerExtensions.extensions(StyleHandler.class);
        for (StyleHandler handler : styleHandlers) {
            if(handler.getVersions() != null && handler.getVersions().size() > 0) {
                // Spring configuration allows associating a single mime to extensions, pick the latest
                List<Version> versions = handler.getVersions();
                final Version firstVersion = versions.get(versions.size() - 1);
                configurer.mediaType(handler.getFormat(), MediaType.valueOf(handler.mimeType(firstVersion)));
            }
        }
        // manually force SLD to v10 for backwards compatibility
        configurer.mediaType("sld", MediaType.valueOf(SLDHandler.MIMETYPE_10));

        // other common media types
        configurer.mediaType("html", MediaType.TEXT_HTML);
        configurer.mediaType("xml", MediaType.APPLICATION_XML);
        configurer.mediaType("json", MediaType.APPLICATION_JSON);
        configurer.mediaType("xslt", MediaType.valueOf("application/xslt+xml"));
        configurer.mediaType("ftl", MediaType.TEXT_PLAIN);
        configurer.mediaType("xml", MediaType.APPLICATION_XML);

        // allow extension point configuration of media types
        List<MediaTypeCallback> callbacks = GeoServerExtensions.extensions(MediaTypeCallback.class);
        for (MediaTypeCallback callback : callbacks) {
            callback.configure(configurer);
        }
        //todo properties files are only supported for test cases. should try to find a way to
        //support them without polluting prod code with handling
        //configurer.mediaType("properties", MediaType.valueOf("application/prs.gs.psl"));

        configurer.favorPathExtension(true);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //Force MVC to use /restng endpoint. If we need something more advanced, we should make a custom PathHelper
        configurer.setUrlPathHelper(new GeoServerUrlPathHelper());
        configurer.getUrlPathHelper().setAlwaysUseFullPath(true);
        configurer.setUseRegisteredSuffixPatternMatch(true);
    }

    static class GeoServerUrlPathHelper extends UrlPathHelper {

        public GeoServerUrlPathHelper() {
            setAlwaysUseFullPath(true);
            setDefaultEncoding("UTF-8");
        }

        @Override
        public String decodeRequestString(HttpServletRequest request, String source) {
            // compatibility with old Restlet based config, it also decodes "+" into space
            try {
                return URLDecoder.decode(source, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    }

}
