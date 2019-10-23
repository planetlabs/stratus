/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest.xml;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.geoserver.security.GeoServerSecurityFilterChain;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.config.LogoutFilterConfig;
import org.geoserver.security.config.SSLFilterConfig;
import org.geoserver.security.config.SecurityManagerConfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;

@Data
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="securityConfig")
public class JaxbSecurityConfig {
	
	protected String roleServiceName;
	protected String configPasswordEncrypterName;
	protected Boolean encryptingUrlParams;
	protected String logoutRedirectURL;
	protected Integer sslPort;

	public JaxbSecurityConfig(GeoServerSecurityManager man) throws IOException {
		SecurityManagerConfig config = man.getSecurityConfig();
		
		this.roleServiceName = config.getRoleServiceName();
		this.configPasswordEncrypterName = config.getConfigPasswordEncrypterName();
		this.encryptingUrlParams = config.isEncryptingUrlParams();
		
		LogoutFilterConfig logoutFilterConfig = 
				(LogoutFilterConfig) man.loadFilterConfig(GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER);		
		logoutRedirectURL = logoutFilterConfig.getRedirectURL();
		
		SSLFilterConfig sslFilterConfig = (SSLFilterConfig) 
				man.loadFilterConfig(GeoServerSecurityFilterChain.SSL_FILTER);
		
		sslPort = sslFilterConfig.getSslPort();
	}

	public void updateConfig(GeoServerSecurityManager man) throws Exception {
		if (logoutRedirectURL != null) {
			LogoutFilterConfig logoutFilterConfig = 
					(LogoutFilterConfig) man.loadFilterConfig(GeoServerSecurityFilterChain.FORM_LOGOUT_FILTER);
			logoutFilterConfig.setRedirectURL(logoutRedirectURL);
			man.saveFilter(logoutFilterConfig);
		}
		
		if (sslPort != null) { //use 0 as default (to distinguish from do not update)
			SSLFilterConfig sslFilterConfig = (SSLFilterConfig) 
					man.loadFilterConfig(GeoServerSecurityFilterChain.SSL_FILTER);
			sslFilterConfig.setSslPort(sslPort == 0 ? null : sslPort);
			man.saveFilter(sslFilterConfig);
		}
		
		SecurityManagerConfig config = man.getSecurityConfig();
		if (roleServiceName != null) {
			config.setRoleServiceName(roleServiceName);
		}
		if (configPasswordEncrypterName != null) {
			config.setConfigPasswordEncrypterName(configPasswordEncrypterName);
		}
		if (encryptingUrlParams != null) {
			config.setEncryptingUrlParams(encryptingUrlParams);
		}
		
		man.saveSecurityConfig(config);
	}
	
}