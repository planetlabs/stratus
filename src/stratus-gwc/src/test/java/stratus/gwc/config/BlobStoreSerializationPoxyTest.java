/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import stratus.gwc.redis.data.FileBlobStoreInfoRedisImpl;
import org.geowebcache.config.FileBlobStoreInfo;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class BlobStoreSerializationPoxyTest {
    
    @Test
    public void testFileBlobStoreInfo() throws Exception {
        FileBlobStoreInfo orig = new FileBlobStoreInfo("files");
        orig.setBaseDirectory("/tmp");
        orig.setDefault(true);
        orig.setEnabled(false);
        orig.setFileSystemBlockSize(42);
        
        FileBlobStoreInfoRedisImpl proxy = new FileBlobStoreInfoRedisImpl(orig);
        
        FileBlobStoreInfo copy = proxy.getInfo();
        
        assertThat(copy, hasProperty("name", equalTo("files")));
        assertThat(copy, hasProperty("baseDirectory", equalTo("/tmp")));
        assertThat(copy, hasProperty("default", equalTo(true)));
        assertThat(copy, hasProperty("enabled", equalTo(false)));
        assertThat(copy, hasProperty("fileSystemBlockSize", equalTo(42)));
    }
}
