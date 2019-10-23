/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.commons.beanfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is an extension of spring's default bean definition reader but provides an addition list of strings
 * containing bean id's that should not be instantiated.  This provides the capability of individually excluding any
 * given bean that is defined in any context.xml file being imported by Stratus.
 *
 * @author joshfix
 * Created on 9/27/17
 */
@Slf4j
public class FilteringBeanDefinitionDocumentReader extends DefaultBeanDefinitionDocumentReader {

    @Getter
    private List<String> exclusions = new ArrayList<>();

    /**
     * Exclusions based on class name. This will exclude a bean with the given class name. Primary motivation
     * is to ensure consistent overriding of beans via ID in some cases where the load order isn't consistent or
     * controllable. This is slightly more granular than filtering by name.
     */
    private List<String> classExclusions = new ArrayList<>();

    public FilteringBeanDefinitionDocumentReader() {
        // The GeoServer handler mappings do not work with spring boot.  There is no reason to ever load them.
        exclusions.addAll(Arrays.asList("wcsURLMapping", "gwcServiceDispatcherMapping", "wpsURLMapping",
                "cswURLMapping", "dispatcherMapping", "wmsURLMapping", "wfsURLMapping", "animateURLMapping",
                "kmlURLMapping", "wpsTempDirDispatcherMapping", "wcs111DispatcherMapping"));
    }

    @Override
    protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
        String id = ele.getAttribute("id");
        if (null != id && exclusions.contains(id)) {
            log.info("Bean id matched exclusion pattern.  Skipping bean creation for " + id);
            return;
        }

        BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
        if (bdHolder != null) {
            if (!this.classExclusions.contains(bdHolder.getBeanDefinition().getBeanClassName())) {
                bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);

                try {
                    BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, this.getReaderContext().getRegistry());
                } catch (BeanDefinitionStoreException var5) {
                    this.getReaderContext().error("Failed to register bean definition with name '" + bdHolder.getBeanName() + "'", ele, var5);
                }

                this.getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
            } else {
                log.warn("Excluding bean: " + bdHolder.toString());
            }
        }
    }

    public void addExclusion(String... exclusionsArray) {
        Collections.addAll(exclusions, exclusionsArray);
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public List<String> getClassExclusions() {
        return classExclusions;
    }

    public void addClassExclusion(String bean) {
        classExclusions.add(bean);
    }
}
