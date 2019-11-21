/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.jndi;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.geotools.data.DataAccess;
import org.geotools.jdbc.JDBCDataStore;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.vfny.geoserver.util.DataStoreUtils;
import stratus.config.StratusConfigProps;
import stratus.config.TomcatConfig;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * @author joshfix
 * Created on 6/21/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {TomcatConfig.class, GeoToolsJndiTest.TestConfig.class})
public class GeoToolsJndiTest {

    public static final String JNDI_SOURCE_NAME = "jdbc/test";
    public static final String JNDI_SOURCE_URL_KEY = "url";
    public static final String JNDI_SOURCE_USERNAME_KEY = "username";
    public static final String JNDI_SOURCE_PASSWORD_KEY = "password";
    public static final String JNDI_SOURCE_PORT = "5535";
    public static final String JNDI_SOURCE_DB_NAME = "test";
    public static final String JNDI_SOURCE_URL = "jdbc:postgresql://localhost:" + JNDI_SOURCE_PORT + "/" + JNDI_SOURCE_DB_NAME;
    public static final String JNDI_SOURCE_USERNAME = "sa";
    public static final String JNDI_SOURCE_PASSWORD = "sa";

    public static final String JNDI_REFERENCE_NAME_PREFIX = "java:comp/env/";
    public static final String JNDI_REFERENCE_NAME_KEY = "jndiReferenceName";
    public static final String JNDI_REFERENCE_NAME = JNDI_REFERENCE_NAME_PREFIX + JNDI_SOURCE_NAME;
    public static final String DB_TYPE_KEY = "dbtype";
    public static final String DB_TYPE = "postgis";
    public static final String FAKE_POSTGIS_VERSION_COMMAND =
            "CREATE ALIAS POSTGIS_LIB_VERSION FOR \"stratus.jndi.GeoToolsJndiTest.postgis_lib_version()\";";
    public Server server;

    @Autowired
    private StratusConfigProps configProps;

    public static String postgis_lib_version() {
        return "2.2";
    }

    @Before
    public void init() throws Exception {
        //Do a hard reset of the DB - shutdown, delete all files, startup again
        server = Server.createPgServer("-pgPort", JNDI_SOURCE_PORT, "-pgDaemon", "-baseDir", "./");
        server.stop();
        DeleteDbFiles.execute("./", "test", false);
        server = Server.createPgServer("-pgPort", JNDI_SOURCE_PORT, "-pgDaemon", "-baseDir", "./");
        server.start();
        // h2 does not support postgis, so need to fake out gt with an aliased function to return a postgis version
        DriverManager
                .getConnection(JNDI_SOURCE_URL, JNDI_SOURCE_USERNAME, JNDI_SOURCE_PASSWORD)
                .createStatement()
                .execute(FAKE_POSTGIS_VERSION_COMMAND);
    }

    @Test
    public void testJndi() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(JNDI_REFERENCE_NAME_KEY, JNDI_REFERENCE_NAME);
        params.put(DB_TYPE_KEY, DB_TYPE);

        DataAccess dataAccess = DataStoreUtils.getDataAccess(params);
        assertThat(dataAccess, is(instanceOf(JDBCDataStore.class)));
        JDBCDataStore jdbcDataStore = (JDBCDataStore)dataAccess;
        assertThat(jdbcDataStore.getDataSource(), is(instanceOf(BasicDataSource.class)));
        BasicDataSource basicDataSource = (BasicDataSource) jdbcDataStore.getDataSource();

        assertThat(basicDataSource.getUrl(), is(notNullValue()));
        assertThat(basicDataSource.getUsername(), is(notNullValue()));
        assertThat(basicDataSource.getPassword(), is(notNullValue()));

        JndiSource jndiSource = configProps.getJndi().getSources().get(0);

        assertThat(basicDataSource.getUrl(), is(jndiSource.getProperties().get(JNDI_SOURCE_URL_KEY)));
        assertThat(basicDataSource.getUsername(), is(jndiSource.getProperties().get(JNDI_SOURCE_USERNAME_KEY)));
        assertThat(basicDataSource.getPassword(), is(jndiSource.getProperties().get(JNDI_SOURCE_PASSWORD_KEY)));
    }

    @After
    public void after() {
        server.stop();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public StratusConfigProps stratusConfigProps() {
            StratusConfigProps configProps = new StratusConfigProps();
            JndiSource jndiSource = new JndiSource();
            jndiSource.setName(JNDI_SOURCE_NAME);
            jndiSource.getProperties().put(JNDI_SOURCE_URL_KEY, JNDI_SOURCE_URL);
            jndiSource.getProperties().put(JNDI_SOURCE_USERNAME_KEY, JNDI_SOURCE_USERNAME);
            jndiSource.getProperties().put(JNDI_SOURCE_PASSWORD_KEY, JNDI_SOURCE_PASSWORD);
            configProps.getJndi().getSources().add(jndiSource);
            return configProps;
        }
    }

}