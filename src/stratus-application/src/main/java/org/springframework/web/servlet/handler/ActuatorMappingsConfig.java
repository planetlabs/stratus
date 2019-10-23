/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.springframework.web.servlet.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * GeoServer displays a list of all MVC endpoints in the /rest endpoint.  Spring Boot Actuator contains several useful
 * REST endpoints that should be exposed, however actuator runs in its own context and is not discoverable by the
 * GeoServer code.  The Spring objects that contain the mappings are not accessible by default.  This class uses
 * reflection to make them accessible and copy the mappings from actuator's context into GeoServer's context to make
 * them visible from the /rest endpoint.
 *
 * //TODO: Determine if this is still necessary
 *
 * @author joshfix
 * Created on 9/28/17
 */
public class ActuatorMappingsConfig {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping geoserverHandlerMapping;

    @Autowired
    private WebMvcEndpointHandlerMapping handlerMapping;

    @PostConstruct
    public void init() throws Exception {
        // get the mapping registry that contains the LinkedHashMap<RequestMappingInfo, HandlerMethod>
        Field mappingRegistryField = AbstractHandlerMethodMapping.class.getDeclaredField("mappingRegistry");
        mappingRegistryField.setAccessible(true);
        AbstractHandlerMethodMapping.MappingRegistry registry =
                (AbstractHandlerMethodMapping.MappingRegistry) mappingRegistryField.get(geoserverHandlerMapping);

        // get the actual Map
        Field mappingLookupField =
                AbstractHandlerMethodMapping.MappingRegistry.class.getDeclaredField("mappingLookup");
        mappingLookupField.setAccessible(true);
        Map<RequestMappingInfo, HandlerMethod> mappings = (Map) mappingLookupField.get(registry);

        // loop through actuator mappings.  we don't want to include all the actuator patterns that end in .json
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();

            PatternsRequestCondition newPatternsCondition =
                    new PatternsRequestCondition(patterns.toArray(new String[]{}));
            RequestMappingInfo newMappingInfo = new RequestMappingInfo(null, newPatternsCondition,
                    mappingInfo.getMethodsCondition(), mappingInfo.getParamsCondition(),
                    mappingInfo.getHeadersCondition(), mappingInfo.getConsumesCondition(),
                    mappingInfo.getProducesCondition(), mappingInfo.getCustomCondition());

            mappings.put(newMappingInfo, entry.getValue());
        }

        mappingRegistryField.setAccessible(false);
        mappingLookupField.setAccessible(false);
    }

}
