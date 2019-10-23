/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver;

import org.geoserver.ows.ProxifyingURLMangler;
import org.geoserver.platform.ExtensionFilter;
import org.springframework.stereotype.Component;

@Component
public class ProxifyingURLManglerExclusionFilter implements ExtensionFilter {

	@Override
	public boolean exclude(String beanId, Object bean) {
		return (bean instanceof ProxifyingURLMangler);
	}
}
