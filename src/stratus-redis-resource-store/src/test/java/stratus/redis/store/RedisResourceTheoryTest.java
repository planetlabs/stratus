/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import org.apache.commons.io.IOUtils;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.ResourceMatchers;
import org.geoserver.platform.resource.ResourceTheoryTest;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import stratus.redis.config.EmbeddedRedisConfig;
import stratus.redis.repository.RedisRepositoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RunWith(Theories.class)
@ContextConfiguration(classes = {EmbeddedRedisConfig.class, RedisMessageListenerContainer.class,
        RedisNotificationDispatcher.class, RedisRepositoryImpl.class, RedisResourceStore.class,
        ResourceDataService.class, RedisResourceInitializer.class, ResourceInitializationConfigProps.class})
public class RedisResourceTheoryTest extends ResourceTheoryTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Autowired
	private RedisResourceStore store;
		
	@DataPoints
    public static String[] testPaths() {
        return new String[]{"myres", "mydir/myres","mydir", "mydir2/myres2", "mydir2", "undir/undef", "undef"};
    }
	
	@Before
    public void setUp() throws Exception {
		TestContextManager tcm = new TestContextManager(getClass());
		tcm.prepareTestInstance(this);
		
		addResource("/myres", "someData");
		addResource("/mydir/myres", "someMoreData");
		addResource("/mydir2/myres2", "someOtherData");	
	}
	
	@After
	public void destroy() throws Exception {
		store.get(Paths.BASE).delete();
	}
	
	private void addResource(String path, String data) throws IOException {
		Resource res = store.get(path);
		try (OutputStream os = res.out()) {
			os.write(data.getBytes());
		}
	}

	@Override
	protected Resource getDirectory() {
		return store.get("/mydir");
	}

	@Override
	protected Resource getResource() {
		return store.get("/mydir/myres");
	}

	@Override
	protected Resource getResource(String path) throws Exception {
		return store.get(path);
	}

	@Override
	protected Resource getUndefined() {
		return store.get("un/de/fined");
	}
	
	@Override
    public void theoryAlteringFileAltersResource(String path) throws Exception {
        //disabled
    }
    
    @Override
    public void theoryAddingFileToDirectoryAddsResource(String path) throws Exception {
        //disabled
    }
    
    @Theory
    public void theoryRenameOverwrite(String path) throws Exception {
    	final Resource res = getResource(path);
        Assume.assumeThat(res, ResourceMatchers.resource());
        final Resource target = getResource();
        Assume.assumeThat(res, Matchers.not(Matchers.equalTo(target)));

        final byte[] expectedContent;
        try(InputStream in = res.in()) {
            expectedContent = IOUtils.toByteArray(in);
        }

        Assert.assertThat(res.renameTo(target), Matchers.is(true));
        Assert.assertThat(res, ResourceMatchers.undefined());
        Assert.assertThat(target, ResourceMatchers.resource());

        final byte[] resultContent;
        try(InputStream in = target.in()) {
            resultContent = IOUtils.toByteArray(in);
        }

        Assert.assertThat(resultContent, Matchers.equalTo(expectedContent));
    }

    @Theory
    public void theoryRenameDirectory(String path) throws Exception {
    	final Resource res = getResource(path);
        Assume.assumeThat(res, ResourceMatchers.directory());
        final Resource target = getResource();
        Assume.assumeThat(res, Matchers.not(Matchers.equalTo(target)));
        
        List<Resource> children = res.list();
        
        Assert.assertThat(res.renameTo(target), Matchers.is(true));
        //assertThat(res, undefined());
        Assert.assertThat(target, ResourceMatchers.directory());
        
        List<Resource> targetChildren = target.list();        
        Assert.assertThat(targetChildren.size(), Matchers.equalTo(children.size()));
        for (int i = 0; i < targetChildren.size(); i++) {
        	//assertThat(children.get(i), undefined());
        	Assert.assertThat(targetChildren.get(i).name(), Matchers.equalTo(children.get(i).name()));
        }
        
    }

}
