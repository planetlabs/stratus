/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest;

import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.security.GeoServerSecurityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author joshfix
 * Created on 6/14/18
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {CatalogRestInterceptorTest.Config.class, CatalogRestInterceptor.class,
        GeoServerExtensions.class, GeoServerSecurityManager.class,  GeoServerResourceLoader.class},
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CatalogRestInterceptorTest {

    @Autowired
    private CatalogRestInterceptor interceptor;

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "geoserver";
    public static final String ADMIN_ROLES = "ADMINISTRATOR";
    public static final String USER_USERNAME = "user";
    public static final String USER_PASSWORD = "pass";
    public static final String USER_ROLES = "USER";
    public static final String TEST_URL = RedisCatalogRestConstants.BASE_PATH + "/any_url";

    @Test
    @WithMockUser(username = ADMIN_USERNAME, password = ADMIN_PASSWORD, roles = ADMIN_ROLES)
    public void testInterceptor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.toString(), TEST_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean grantAccess = interceptor.preHandle(request, response, null);
        assertTrue(grantAccess);
    }

    @Test
    @WithMockUser(username = USER_USERNAME, password = USER_PASSWORD, roles = USER_ROLES)
    public void testUnauthorizedInterceptor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(RequestMethod.GET.toString(), TEST_URL);
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean grantAccess = interceptor.preHandle(request, response, null);
        assertFalse(grantAccess);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public GeoServerDataDirectory geoServerDataDirectory(GeoServerResourceLoader loader) {
            return new GeoServerDataDirectory(loader);
        }
    }

}
