/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest;

import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.validation.SecurityConfigException;
import org.geoserver.test.GeoServerTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import stratus.config.StratusConfigProps;
import stratus.controller.GwcServiceController;
import stratus.gwc.config.*;
import stratus.redis.catalog.RedisCatalogFacade;
import stratus.redis.catalog.RedisCatalogImportResourcesConfig;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.geoserver.RedisGeoServerFacade;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.rest.xml.*;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        /* Catalog */
        RedisCatalogFacade.class, RedisGeoServerFacade.class, RedisLayerIndexFacade.class,
        CacheProperties.class, CatalogImpl.class, GWCWithEmbeddedRedisConfig.class, StratusConfigProps.class,
        RedisCatalogImportResourcesConfig.class, RedisRepositoryImpl.class, RedisConfigProps.class,
        /* OWS */
        WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class, WCSInfoClassRegisteringBean.class,
        /* GWC */
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, GwcServiceController.class,
        RedisGridSetConfiguration.class, RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class SecurityConfigRestControllerTest extends GeoServerTestSupport {
    
    GeoServerSecurityManager securityManager;
    SecurityConfigRestController securityConfigRestController;

    @Autowired
    private RedisCatalogImportResourcesConfig importResourcesConfig;

    @Override
    protected String[] getSpringContextLocations() {
        List<String> springContextLocations = new ArrayList<>();
        importResourcesConfig.buildFilteredApplicationContextXmlResourceList(springContextLocations);
        return springContextLocations.toArray(new String[0]);
    }

    @Before
    public void oneTimeSetUp() throws Exception {
        setValidating(true);
        super.oneTimeSetUp();
        securityManager = applicationContext.getBean(GeoServerSecurityManager.class);
        securityConfigRestController = new SecurityConfigRestController(securityManager);
    }
    
    @Test
    public void testConfig() throws Exception {
        JaxbSecurityConfig config = securityConfigRestController.get();
        assertNotNull(config);
        assertEquals("default", config.getRoleServiceName());
        assertEquals("pbePasswordEncoder", config.getConfigPasswordEncrypterName());
        assertEquals("/web/", config.getLogoutRedirectURL());
        assertEquals(new Integer(443), config.getSslPort());
        
        //make a change
        config.setSslPort(8443);
        securityConfigRestController.put(config);
        config = securityConfigRestController.get();
        assertEquals(new Integer(8443), config.getSslPort());
    }

    @Test
    public void testAuthFilters() throws Exception {
        JaxbAuthFilterList authFilters = securityConfigRestController.getAuthFilters();
        assertEquals(12, authFilters.getFilters().size());
        assertEquals("anonymous", authFilters.getFilters().get(0));
        String filter = new String(securityConfigRestController.getAuthFilter("anonymous"));
        assertTrue(filter.contains("<className>org.geoserver.security.filter.GeoServerAnonymousAuthenticationFilter</className>"));
        
        //404
        boolean ok = false;
        try {
            securityConfigRestController.getAuthFilter("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);
        
        //cannot create with id
        ok = false;
        try {
            securityConfigRestController.postAuthFilter(filter.getBytes());
            //} catch (IllegalStateException e) {
        } catch (Exception e) {
            ok = true;
        }
        assertTrue(ok);
                
        filter = "<org.geoserver.security.config.BasicAuthenticationFilterConfig>" +
           "<name>newAuthFilter</name>" +
           "<className>org.geoserver.security.filter.GeoServerBasicAuthenticationFilter</className>" +
           "<useRememberMe>true</useRememberMe>" +
           "</org.geoserver.security.config.BasicAuthenticationFilterConfig>";
        
        //make new
        securityConfigRestController.postAuthFilter(filter.getBytes());
        authFilters = securityConfigRestController.getAuthFilters();
        assertEquals(13, authFilters.getFilters().size());
        filter = new String(securityConfigRestController.getAuthFilter("newAuthFilter"));
        assertNotNull(filter);        

        assertEquals(true, ((org.geoserver.security.config.BasicAuthenticationFilterConfig) 
            securityManager.loadFilterConfig("newAuthFilter")).isUseRememberMe());
                
        //cannot change name
        filter = filter.replace("newAuthFilter", "otherAuthFilter");
        ok = false;
        try {
            securityConfigRestController.putAuthFilter("newAuthFilter", filter.getBytes());            
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        //make a change
        filter = "<org.geoserver.security.config.BasicAuthenticationFilterConfig>" 
                + "<className>org.geoserver.security.filter.GeoServerBasicAuthenticationFilter</className>"
                + "<useRememberMe>false</useRememberMe>" 
                + "</org.geoserver.security.config.BasicAuthenticationFilterConfig>";
        securityConfigRestController.putAuthFilter("newAuthFilter", filter.getBytes());
        
        assertEquals(false, ((org.geoserver.security.config.BasicAuthenticationFilterConfig) 
            securityManager.loadFilterConfig("newAuthFilter")).isUseRememberMe());

        //delete
        securityConfigRestController.deleteAuthFilter("newAuthFilter");
        authFilters = securityConfigRestController.getAuthFilters();
        assertEquals(12, authFilters.getFilters().size());
    }
    
    @Test
    public void testAuthProviders() throws Exception {
        JaxbAuthProviderList authProviders = securityConfigRestController.getAuthProviders();
        assertEquals(1, authProviders.getProviders().size());
        assertEquals("default", authProviders.getProviders().get(0));
        String provider = new String(securityConfigRestController.getAuthProvider("default"));
        assertTrue(provider.contains("<className>org.geoserver.security.auth.UsernamePasswordAuthenticationProvider</className>"));
        
        //404
        boolean ok = false;
        try {
            securityConfigRestController.getAuthProvider("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);

        // cannot create with id
        ok = false;
        try {
            securityConfigRestController.postAuthProvider(provider.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        provider = "<org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig>"
                + "<name>newAuthProvider</name>"
                + "<className>org.geoserver.security.auth.UsernamePasswordAuthenticationProvider</className>"
                + "<userGroupServiceName>default</userGroupServiceName>"
                + "</org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig>";

        // make new
        securityConfigRestController.postAuthProvider(provider.getBytes());
        authProviders = securityConfigRestController.getAuthProviders();
        assertEquals(2, authProviders.getProviders().size());
        provider = new String(securityConfigRestController.getAuthProvider("newAuthProvider"));
        assertNotNull(provider);

        // cannot change name
        provider = provider.replace("newAuthProvider", "otherAuthProvider");
        ok = false;
        try {
            securityConfigRestController.putAuthProvider("newAuthProvider", provider.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        // make a change
        provider = "<org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig>"
                + "<className>org.geoserver.security.auth.UsernamePasswordAuthenticationProvider</className>"
                + "<userGroupServiceName>not_the_default</userGroupServiceName>"
                + "</org.geoserver.security.config.UsernamePasswordAuthenticationProviderConfig>";
        
        ok = false;
        try {
            securityConfigRestController.putAuthProvider("newAuthProvider", provider.getBytes());
        } catch (SecurityConfigException e) { //user group service doesn't exist
            ok = true;
        }
        assertTrue(ok);
        

        // delete
        securityConfigRestController.deleteAuthProvider("newAuthProvider");
        authProviders = securityConfigRestController.getAuthProviders();
        assertEquals(1, authProviders.getProviders().size());
    }
    
    @Test
    public void testPasswordProviders() throws Exception {
        JaxbPasswordProviderList pwProviders = securityConfigRestController.getPasswordProviders();
        assertEquals(1, pwProviders.getProviders().size());
        assertEquals("default", pwProviders.getProviders().get(0));
        String provider = new String(securityConfigRestController.getPasswordProvider("default"));
        assertTrue(provider.contains("<className>org.geoserver.security.password.URLMasterPasswordProvider</className>"));
        
        //404
        boolean ok = false;
        try {
            securityConfigRestController.getPasswordProvider("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);

        // cannot create with id
        ok = false;
        try {
            securityConfigRestController.postPasswordProvider(provider.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        provider = "<org.geoserver.security.password.URLMasterPasswordProviderConfig>"
                 + "<name>newPwProvider</name>"
                 + "<className>org.geoserver.security.password.URLMasterPasswordProvider</className>"
                 + "<readOnly>false</readOnly>"
                 + "<url>file:passwd</url>"
                 + "<encrypting>true</encrypting>"
                 + "</org.geoserver.security.password.URLMasterPasswordProviderConfig>";

        // make new
        securityConfigRestController.postPasswordProvider(provider.getBytes());
        pwProviders = securityConfigRestController.getPasswordProviders();
        assertEquals(2, pwProviders.getProviders().size());
        provider = new String(securityConfigRestController.getPasswordProvider("newPwProvider"));
        assertNotNull(provider);

        assertEquals("file:passwd", ((org.geoserver.security.password.URLMasterPasswordProviderConfig) 
                securityManager.loadMasterPassswordProviderConfig("newPwProvider")).getURL().toString());

        // cannot change name
        provider = provider.replace("newPwProvider", "otherPwProvider");
        ok = false;
        try {
            securityConfigRestController.putPasswordProvider("newPwProvider", provider.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        // make a change
        provider = "<org.geoserver.security.password.URLMasterPasswordProviderConfig>"
                 + "<className>org.geoserver.security.password.URLMasterPasswordProvider</className>"
                 + "<readOnly>false</readOnly>"
                 + "<url>file:otherpasswd</url>"
                 + "<encrypting>true</encrypting>"
                 + "</org.geoserver.security.password.URLMasterPasswordProviderConfig>";

        securityConfigRestController.putPasswordProvider("newPwProvider", provider.getBytes());
        assertEquals("file:otherpasswd", ((org.geoserver.security.password.URLMasterPasswordProviderConfig) 
                securityManager.loadMasterPassswordProviderConfig("newPwProvider")).getURL().toString());

        // delete
        securityConfigRestController.deletePasswordProvider("newPwProvider");
        pwProviders = securityConfigRestController.getPasswordProviders();
        assertEquals(1, pwProviders.getProviders().size());
    }
    
    @Test
    public void testPasswordPolicies() throws Exception {
        JaxbPasswordPolicyList pwPolicies = securityConfigRestController.getPasswordPolicies();
        assertEquals(2, pwPolicies.getPolicies().size());
        assertEquals("default", pwPolicies.getPolicies().get(0));
        String policy = new String(securityConfigRestController.getPasswordPolicy("default"));
        assertTrue(policy.contains(" <className>org.geoserver.security.validation.PasswordValidatorImpl</className>"));
        
        //404
        boolean ok = false;
        try {
            securityConfigRestController.getPasswordPolicy("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);

        // cannot create with id
        ok = false;
        try {
            securityConfigRestController.postPasswordPolicy(policy.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        policy = "<org.geoserver.security.config.PasswordPolicyConfig>"
          + "<name>newPwPolicy</name>"
          + "<className>org.geoserver.security.validation.PasswordValidatorImpl</className>"
          + "<uppercaseRequired>false</uppercaseRequired>"
          + "<lowercaseRequired>false</lowercaseRequired>"
          + "<digitRequired>false</digitRequired>"
          + "<minLength>0</minLength>"
          + "<maxLength>-1</maxLength>"
          + "</org.geoserver.security.config.PasswordPolicyConfig>";

        // make new
        securityConfigRestController.postPasswordPolicy(policy.getBytes());
        pwPolicies = securityConfigRestController.getPasswordPolicies();
        assertEquals(3, pwPolicies.getPolicies().size());
        policy = new String(securityConfigRestController.getPasswordPolicy("newPwPolicy"));
        assertNotNull(policy);

        assertEquals(-1, securityManager
                .loadPasswordPolicyConfig("newPwPolicy").getMaxLength());

        // cannot change name
        policy = policy.replace("newPwPolicy", "otherPwPolicy");
        ok = false;
        try {
            securityConfigRestController.putPasswordPolicy("newPwPolicy", policy.getBytes());
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);

        // make a change
        policy = "<org.geoserver.security.config.PasswordPolicyConfig>"
                  + "<className>org.geoserver.security.validation.PasswordValidatorImpl</className>"
                  + "<uppercaseRequired>false</uppercaseRequired>"
                  + "<lowercaseRequired>false</lowercaseRequired>"
                  + "<digitRequired>false</digitRequired>"
                  + "<minLength>0</minLength>"
                  + "<maxLength>30</maxLength>"
                  + "</org.geoserver.security.config.PasswordPolicyConfig>";

        securityConfigRestController.putPasswordPolicy("newPwPolicy", policy.getBytes());
        assertEquals(30, securityManager
                .loadPasswordPolicyConfig("newPwPolicy").getMaxLength());

        // delete
        securityConfigRestController.deletePasswordPolicy("newPwPolicy");
        pwPolicies = securityConfigRestController.getPasswordPolicies();
        assertEquals(2, pwPolicies.getPolicies().size());
    }
    
    @Test
    public void testFilterChains() throws Exception {
        JaxbRequestFilterChainList filterChains = securityConfigRestController.getFilterChains();        
        assertEquals(6, filterChains.getList().size());
        assertEquals("web", filterChains.getList().get(0).getName());
        JaxbRequestFilterChain chain = securityConfigRestController.getFilterChain("web");
        assertEquals(3, chain.getFilterNames().getFilterNames().size());
        assertEquals("rememberme", chain.getFilterNames().getFilterNames().get(0));
        
        //post a copy on position 4
        chain.setName("testChain");
        chain.setPosition(4);
        securityConfigRestController.postFilterChain(chain);
        filterChains = securityConfigRestController.getFilterChains();        
        assertEquals(7, filterChains.getList().size());
        assertEquals("testChain", filterChains.getList().get(4).getName());
        chain = securityConfigRestController.getFilterChain("testChain");
        assertNotNull(chain);
        
        //let's change the order
        chain.setPosition(2);
        securityConfigRestController.putFilterChain("testChain", chain);    
        filterChains = securityConfigRestController.getFilterChains();        
        assertEquals("testChain", filterChains.getList().get(2).getName());
        
        //test 404
        boolean ok = false;
        try {
            securityConfigRestController.getFilterChain("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);
                
        ok = false;
        try {
            securityConfigRestController.putFilterChain("doesn't exist", chain);
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);
        
        //test impossible to change type
        chain.setType(JaxbRequestFilterChain.Type.UNKNOWN);
        ok = false;
        try {
            securityConfigRestController.putFilterChain("testChain", chain);
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);
        
        //test impossible to create chain of type unknown
        ok = false;
        chain.setName("otherTestChain");
        try {
            securityConfigRestController.postFilterChain(chain);
        } catch (IllegalStateException e) {
            ok = true;
        }
        assertTrue(ok);
        
        //delete it
        securityConfigRestController.deleteFilterChain("testChain");
        filterChains = securityConfigRestController.getFilterChains();        
        assertEquals(6, filterChains.getList().size());
        
        //test 404
        ok = false;
        try {
            securityConfigRestController.deleteFilterChain("doesn't exist");
        } catch (IllegalArgumentException e) {
            ok = true;
        }
        assertTrue(ok);
    }

}
