/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.impl.CatalogImpl;
import org.geoserver.config.GeoServer;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.impl.GeoServerImpl;
import org.geoserver.config.util.XStreamServiceLoader;
import org.geoserver.data.test.StratusTestData;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.ows.util.KvpUtils;
import org.geoserver.ows.util.ResponseUtils;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.rest.RestConfiguration;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.SecureCatalogImpl;
import org.geoserver.security.filter.GeoServerAnonymousAuthenticationFilter;
import org.geoserver.security.filter.GeoServerSecurityInterceptorFilter;
import org.geoserver.test.GeoServerAbstractTestSupport;
import org.geoserver.test.GeoServerSystemTestSupport;
import org.geoserver.wps.WPSInfoImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import stratus.config.*;
import stratus.controller.GwcServiceController;
import stratus.controller.OwsController;
import stratus.gwc.config.*;
import stratus.gwc.redis.data.WMTSInfoClassRegisteringBean;
import stratus.health.StratusHealthIndicator;
import stratus.ows.OWSCachingCallback;
import stratus.ows.OWSVirtualServiceCallback;
import stratus.redis.RedisFacadeTestSupport;
import stratus.redis.cache.rest.RestCachingInterceptor;
import stratus.redis.cache.rest.preloaders.*;
import stratus.redis.config.RedisConfigProps;
import stratus.redis.index.CacheProperties;
import stratus.redis.index.RedisLayerIndexFacade;
import stratus.redis.repository.RedisRepositoryImpl;
import stratus.redis.store.*;
import stratus.wcs.WCSConfig;
import stratus.wcs.redis.geoserver.info.WCSInfoClassRegisteringBean;
import stratus.wfs.WFSConfig;
import stratus.wfs.redis.geoserver.info.WFSInfoClassRegisteringBean;
import stratus.wms.WMSConfig;
import stratus.wms.redis.geoserver.info.WMSInfoClassRegisteringBean;
import stratus.wps.WPSInitializerS3;
import stratus.wps.WPSStorageCleaner;
import stratus.wps.config.RedisWPSRepositoriesConfig;
import stratus.wps.config.S3Config;
import stratus.wps.config.WPSConfigurationProperties;
import stratus.wps.redis.geoserver.info.WPSInfoClassRegisteringBean;

import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 * Test framework for general Stratus Application tests
 * Instantiates a Stratus GeoServer instance, and loads the {@link SystemTestData} into it.
 *
 * Based on {@link GeoServerSystemTestSupport}, but without any configuration specific to community geoserver
 */
//@EnableWebSecurity
@RunWith(SpringRunner.class)
@ComponentScan(
        basePackages = {"stratus", "org.geoserver.rest"},
        excludeFilters = {@ComponentScan.Filter(type=ASSIGNABLE_TYPE,value={SecureCatalogImpl.class}),
                @ComponentScan.Filter(type=ASSIGNABLE_TYPE,value={RestConfiguration.class})})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {
        /* Application Config */
        GWCWithEmbeddedRedisConfig.class,  WebXmlConfig.class, StratusRestSecurityConfiguration.class, StratusApplicationImportResourcesConfig.class,
        StratusConfigProps.class, CacheProperties.class, RedisConfigProps.class, CommunityRestMvcConfigurer.class, CommunityRestConfiguration.class,
        StratusInitializer.class, StratusGeoServerInitializedEventListener.class, StratusHealthIndicator.class,
        ObjectPostProcessorConfiguration.class, AuthenticationConfiguration.class, DispatcherServletAutoConfiguration.class,
        DispatcherServletConfig.class,
        /* Redis Config */
        ResourceDataService.class, RedisResourceStore.class, RedisRepositoryImpl.class, RedisLayerIndexFacade.class,
        RedisNotificationDispatcher.class, RedisMessageListenerContainer.class, RedisResourceInitializer.class,
        ResourceInitializationConfigProps.class, WMSInfoClassRegisteringBean.class, WFSInfoClassRegisteringBean.class,
        WCSInfoClassRegisteringBean.class, WMTSInfoClassRegisteringBean.class, WPSInfoClassRegisteringBean.class,
        /* GWC Config */
        RedisServerConfiguration.class, RedisGeoServerTileLayerConfiguration.class, RedisGridSetConfiguration.class,
        RedisBlobStoreConfiguration.class, StratusDefaultingConfiguration.class, GwcServiceController.class,
        /* Auth Config */
        SecureCatalogConfig.class, GeoServerAnonymousAuthenticationFilter.class, GeoServerSecurityInterceptorFilter.class,
        /* OWS Dispatcher */
        OwsController.class, OWSVirtualServiceCallback.class,
        /* Catalog Caching */
        OWSCachingCallback.class, WMSConfig.class, WFSConfig.class, WCSConfig.class,
        CoveragePreloader.class, CoveragesPreloader.class, CoverageStorePreloader.class, CoverageStoresPreloader.class,
        DataStorePreloader.class, DataStoresPreloader.class, FeatureTypePreloader.class, FeatureTypesPreloader.class,
        LayerGroupPreloader.class, LayerGroupsPreloader.class, LayerPreloader.class, LayersPreloader.class,
        NamespacePreloader.class, NamespacesPreloader.class, StylePreloader.class, StylesPreloader.class,
        WmsLayerPreloader.class, WmsLayersPreloader.class, WmsStorePreloader.class, WmsStoresPreloader.class,
        WorkspacePreloader.class, WorkspacesPreloader.class,
        RestCachingInterceptor.class,
        /* WPS Config */
        WPSInfoImpl.class, WPSConfigurationProperties.class, S3Config.class, WPSInitializerS3.class,
        WPSStorageCleaner.class, StratusWPSConfig.class, RedisWPSRepositoriesConfig.class, RestTemplate.class
}, properties = {"spring.main.allow-bean-definition-overriding=true"})
public abstract class StratusApplicationTestSupport  implements ApplicationContextAware {

    @Autowired
    protected RedisFacadeTestSupport redisTestSupport;
    @Autowired
    protected Catalog catalog;
    @Autowired
    protected CatalogImpl catalogImpl;
    @Autowired
    protected GeoServer geoServer;
    @Autowired
    protected GeoServerImpl geoServerImpl;
    @Autowired
    RedisResourceInitializer resourceInitializer;

    protected ApplicationContext applicationContext;

    protected DispatcherServlet dispatcher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        new GeoServerExtensions().setApplicationContext(applicationContext);
    }

    @Before
    public final void setUp() throws Exception {
        redisTestSupport.repository.flush();
        catalogImpl.setFacade(redisTestSupport.createCatalogFacade(catalogImpl));
        geoServerImpl.setFacade(redisTestSupport.createGeoServerFacade());

        GeoServerInfo geoServerInfo = geoServerImpl.getFactory().createGlobal();
        geoServerImpl.setGlobal(geoServerInfo);

        LoggingInfo loggingInfo = geoServerImpl.getFactory().createLogging();
        geoServerImpl.setLogging(loggingInfo);

        //also ensure we have a service configuration for every service we know about
        final List<XStreamServiceLoader> loaders = GeoServerExtensions.extensions(XStreamServiceLoader.class);
        for (XStreamServiceLoader loader : loaders) {
            ServiceInfo serviceInfo = geoServerImpl.getService(loader.getServiceClass());
            if (serviceInfo == null) {
                geoServerImpl.add(loader.create(geoServerImpl));
            }
        }
        ResourceDataService resourceDataService = GeoServerExtensions.bean(ResourceDataService.class);
        RedisResourceStore resourceStore = GeoServerExtensions.bean(RedisResourceStore.class);
        resourceStore.setDataService(resourceDataService);
        GeoServerSecurityManager securityManager = GeoServerExtensions.bean(GeoServerSecurityManager.class);

        resourceInitializer.init();

        dispatcher = buildDispatcher();

        SystemTestData testData = createTestData();

        login("admin", "geoserver", "ROLE_ADMINISTRATOR");

        setUpTestData(testData);
        onSetUp(testData);
    }



    protected SystemTestData createTestData() throws Exception {
        return new StratusTestData(catalogImpl);
    }

    /**
     * Sets up the {@link SystemTestData} used for this test.
     * <p>
     * This method is used to add any additional data or configuration to the test setup and may
     * be overridden or extended. The default implementation calls
     * {@link SystemTestData#setUpDefaultLayers()} to add the default layers for the test.
     * </p>
     */
    protected void setUpTestData(SystemTestData testData) throws Exception {
        testData.setUp();
        testData.setUpDefault();
    }

    /**
     * Subclass hook called after the system (ie spring context) has been fully initialized.
     * <p>
     * Subclasses should override for post setup that is needed. The default implementation does
     * nothing.
     * </p>
     */
    protected void onSetUp(SystemTestData testData) throws Exception {
    }
    /**
     * Subclasses needed to do integration tests with servlet filters can override this method
     * and return the list of filters to be used during mocked requests
     *
     */
    protected List<Filter> getFilters() {
        return null;
    }

    /**
     * Sets up the authentication context for the test.
     * <p>
     * This context lasts only for a single test case, it is cleared after every test has completed.
     * </p>
     * @param username The username.
     * @param password The password.
     * @param roles Roles to assign.
     */
    protected void login(String username, String password, String... roles) {
        SecurityContextHolder.setContext(new SecurityContextImpl());
        List<GrantedAuthority> l= new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            l.add(new SimpleGrantedAuthority(role));
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username,password,l));
    }

    /**
     * Clears the authentication context.
     * <p>
     * This method is called after each test case
     * </p>
     */
    protected void logout() {
        SecurityContextHolder.clearContext();
    }

    protected DispatcherServlet getDispatcher() throws Exception {
        return dispatcher;
    }

    protected DispatcherServlet buildDispatcher() throws ServletException, NoSuchFieldException, IllegalAccessException {
        // create an instance of the spring dispatcher
        ServletContext context = ((WebApplicationContext) applicationContext).getServletContext();

        MockServletConfig config = new MockServletConfig(context, "dispatcher");

        DispatcherServlet dispatcher = new DispatcherServlet((WebApplicationContext) applicationContext);

        dispatcher.setContextConfigLocation(GeoServerAbstractTestSupport.class.getResource(
                "dispatcher-servlet.xml").toString());
        dispatcher.init(config);

        //Hack any extra interceptors into the main RequestMapping
        Collection<HandlerInterceptor> interceptors = GeoServerExtensions.extensions(HandlerInterceptor.class, applicationContext);
        RequestMappingHandlerMapping hm = GeoServerExtensions.extensions(RequestMappingHandlerMapping.class).get(0);

        Field adaptedInterceptorsField = AbstractHandlerMapping.class.getDeclaredField("adaptedInterceptors");
        adaptedInterceptorsField.setAccessible(true);
        List<HandlerInterceptor> adaptedInterceptors = (List<HandlerInterceptor>) adaptedInterceptorsField.get(hm);
        //skip any duplicates
        interceptors.removeAll(adaptedInterceptors);
        adaptedInterceptors.addAll(interceptors);

        return dispatcher;
    }

    protected MockHttpServletResponse dispatch(HttpServletRequest request ) throws Exception {
        return dispatch(request, (String) null);
    }

    protected MockHttpServletResponse dispatch( HttpServletRequest request, String charset )
            throws Exception {
        MockHttpServletResponse response = null;
        if (charset == null) {
            charset = Charset.defaultCharset().name();
        }
        response = new MockHttpServletResponse();
        response.setCharacterEncoding(charset);

        dispatch(request, response);
        return response;
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final DispatcherServlet dispatcher = getDispatcher();

        // build a filter chain so that we can test with filters as well
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {
                try {
                    dispatcher.service(request, response);
                } catch(RuntimeException e) {
                    throw e;
                } catch(IOException e) {
                    throw e;
                } catch(ServletException e) {
                    throw e;
                } catch(Exception e) {
                    throw (IOException) new IOException("Failed to handle the request").initCause(e);
                }
            }
        };
        List<Filter> filterList = getFilters();
        MockFilterChain chain;
        if(filterList != null) {
            chain = new MockFilterChain(servlet, (Filter[]) filterList.toArray(new Filter[filterList.size()]));
        } else {
            chain = new MockFilterChain(servlet);
        }


        chain.doFilter(request, response);

    }

    protected MockHttpServletRequest createRequest(String path) {
        return createRequest(path, false);
    }

    //
    // request/response helpers
    //
    /**
     * Convenience method for subclasses to create mock http servlet requests.
     * <p>
     * Examples of using this method are:
     * <pre>
     * <code>
     *   createRequest( "wfs?request=GetCapabilities" );  //get
     *   createRequest( "wfs" ); //post
     * </code>
     * </pre>
     * </p>
     * @param path The path for the request and optional the query string.
     *
     */
    protected MockHttpServletRequest createRequest(String path, boolean createSession) {
        MockHttpServletRequest request = new GeoServerSystemTestSupport.GeoServerMockHttpServletRequest();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("/geoserver");
        request.setRequestURI(ResponseUtils.stripQueryString(ResponseUtils.appendPath(
                "/geoserver/", path)));
        // request.setRequestURL(ResponseUtils.appendPath("http://localhost:8080/geoserver", path ) );
        request.setQueryString(ResponseUtils.getQueryString(path));
        request.setRemoteAddr("127.0.0.1");
        request.setServletPath(ResponseUtils.makePathAbsolute( ResponseUtils.stripRemainingPath(path)) );
        request.setPathInfo(ResponseUtils.makePathAbsolute( ResponseUtils.stripBeginningPath( ResponseUtils.stripQueryString(path))));
        request.addHeader("Host", "localhost:8080");

        /*
        // deal with authentication
        if(username != null) {
            String token = username + ":";
            if(password != null) {
                token += password;
            }
            request.addHeader("Authorization",  "Basic " + new String(Base64.encodeBase64(token.getBytes())));
        }*/


        kvp(request, path);

        if(createSession) {
            MockHttpSession session = new MockHttpSession(new MockServletContext());
            request.setSession(session);
        }

        request.setUserPrincipal(null);

        return request;
    }

    /**
     * Convenience method for subclasses to create mock http servlet requests.
     * <p>
     * Examples of using this method are:
     * <pre>
     * <code>
     *   Map kvp = new HashMap();
     *   kvp.put( "service", "wfs" );
     *   kvp.put( "request", "GetCapabilities" );
     *
     *   createRequest( "wfs", kvp );
     * </code>
     * </pre>
     * </p>
     * @param path The path for the request, minus any query string parameters.
     * @param kvp The key value pairs to be put in teh query string.
     *
     */
    protected MockHttpServletRequest createRequest( String path, Map kvp ) {
        StringBuffer q = new StringBuffer();
        for ( Iterator e = kvp.entrySet().iterator(); e.hasNext(); ) {
            Map.Entry entry = (Map.Entry) e.next();
            q.append( entry.getKey() ).append("=").append( entry.getValue() );
            q.append( "&" );
        }
        q.setLength(q.length()-1);

        return createRequest( ResponseUtils.appendQueryString(path, q.toString() ) );
    }

    /*
     * Helper method to create the kvp params from the query string.
     */
    private void kvp(MockHttpServletRequest request, String path) {
        Map<String, Object> params = KvpUtils.parseQueryString(path);
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if(value instanceof String) {
                request.addParameter(key, (String) value);
            } else {
                String[] values = (String[]) value;
                request.addParameter(key, values);
            }
        }

    }

    /**
     * Executes an ows request using the GET method.
     *
     * @param path The porition of the request after hte context,
     *      example: 'wms?request=GetMap&version=1.1.1&..."
     *
     * @return the mock servlet response
     *
     */
    protected MockHttpServletResponse getAsServletResponse( String path ) throws Exception {
        return getAsServletResponse(path, null);
    }

    /**
     * Executes a request using the GET method.
     *
     * @param path The porition of the request after hte context,
     *      example: 'wms?request=GetMap&version=1.1.1&..."
     * @param charset The character set of the response.
     *
     * @return the mock servlet response
     */
    protected MockHttpServletResponse getAsServletResponse( String path, String charset ) throws Exception {
        MockHttpServletRequest request = createRequest( path );
        request.setMethod( "GET" );
        request.setContent(new byte[]{});

        return dispatch( request, charset );
    }

    /**
     * Executes a request using the POST method.
     *
     * @param path The portion of the request after the context ( no query string ), example: 'wms'.
     * @param body The body content, often xml for OGC services
     * @param contentType
     * @return the servlet response
     */
    protected MockHttpServletResponse postAsServletResponse(String path, String body, String contentType) throws Exception {
        return postAsServletResponse(path, body.getBytes("UTF-8"), contentType);
    }

    protected MockHttpServletResponse postAsServletResponse(String path, byte[] body, String contentType )
            throws Exception {

        MockHttpServletRequest request = createRequest(path);
        request.setMethod("POST");
        request.setContentType(contentType);
        request.setContent(body);

        return dispatch(request);
    }

    /**
     * Executes a request using the PUT method.
     *
     * @param path The portion of the request after the context ( no query string ), example: 'wms'.
     * @param body The body content, often xml for OGC services
     * @param contentType
     * @return the servlet response
     */
    protected MockHttpServletResponse putAsServletResponse(String path, String body, String contentType )
            throws Exception {
        return putAsServletResponse(path, body != null ? body.getBytes() : (byte[]) null, contentType);
    }

    protected MockHttpServletResponse putAsServletResponse(String path, byte[] body, String contentType )
            throws Exception {

        MockHttpServletRequest request = createRequest(path);
        request.setMethod("PUT");
        request.setContentType(contentType);
        request.setContent(body);

        return dispatch(request);
    }

    /**
     * Executes a request using the DELETE method.
     *
     * @param path The path of the request.
     *
     * @return the servlet response
     */
    protected MockHttpServletResponse deleteAsServletResponse(String path) throws Exception {
        MockHttpServletRequest request = createRequest(path);
        request.setMethod("DELETE");

        return dispatch(request);
    }

    /**
     * Extracts the true binary stream out of the response. The usual way (going
     * thru {@link MockHttpServletResponse#getOutputStreamContent()}) mangles
     * bytes if the content is not made of chars.
     *
     * @param response
     *
     */
    protected byte[] getBinary(MockHttpServletResponse response) {
        return response.getContentAsByteArray();
    }

    /**
     * Extracts the true binary stream out of the response. The usual way (going
     * thru {@link MockHttpServletResponse#getOutputStreamContent()}) mangles
     * bytes if the content is not made of chars.
     *
     * @param response
     *
     */
    protected ByteArrayInputStream getBinaryInputStream(MockHttpServletResponse response) {
        return new ByteArrayInputStream(getBinary(response));
    }

    /**
     * Performs some checks on an image response such as the mime type and attempts to read the
     * actual image into a buffered image.
     *
     */
    protected void checkImage(MockHttpServletResponse response, String mimeType, int width, int height) {
        assertEquals(mimeType, response.getContentType());
        try {
            BufferedImage image = ImageIO.read(getBinaryInputStream(response));
            assertNotNull(image);
            if(width > 0) {
                assertEquals(width, image.getWidth());
            }
            if(height > 0) {
                assertEquals(height, image.getHeight());
            }
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Could not read image returned from GetMap:" + t.getLocalizedMessage());
        }
    }


}
