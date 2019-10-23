/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.config;

import lombok.RequiredArgsConstructor;
import org.geoserver.platform.GeoServerExtensions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CommunityRestConfiguration {

    private ContentNegotiationManager contentNegotiationManager;
    private final WebMvcConfigurationSupport support;
    /**
     * Return a {@link ContentNegotiationManager} instance to use to determine requested {@linkplain
     * MediaType media types} in a given request.
     */
    @Bean
    public ContentNegotiationManager mvcContentNegotiationManager() {
        if (this.contentNegotiationManager == null) {
            this.contentNegotiationManager = support.mvcContentNegotiationManager();
            this.contentNegotiationManager
                    .getStrategies()
                    .add(0, new DelegatingContentNegotiationStrategy());
        }
        return this.contentNegotiationManager;
    }

    /** Allows extension point configuration of {@link ContentNegotiationStrategy}s */
    private static class DelegatingContentNegotiationStrategy
            implements ContentNegotiationStrategy {
        @Override
        public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest)
                throws HttpMediaTypeNotAcceptableException {
            List<ContentNegotiationStrategy> strategies =
                    GeoServerExtensions.extensions(ContentNegotiationStrategy.class);
            List<MediaType> mediaTypes;
            for (ContentNegotiationStrategy strategy : strategies) {
                if (!(strategy instanceof ContentNegotiationManager
                        || strategy instanceof DelegatingContentNegotiationStrategy)) {
                    mediaTypes = strategy.resolveMediaTypes(webRequest);
                    if (mediaTypes.size() > 0) {
                        return mediaTypes;
                    }
                }
            }
            return MEDIA_TYPE_ALL_LIST;
        }
    }
}
