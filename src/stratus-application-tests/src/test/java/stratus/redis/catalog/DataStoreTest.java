/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog;

import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * Basic GeoServer-Redis integration test for {@link DataStoreInfo}
 *
 * Created by joshfix on 8/26/16.
 */
public class DataStoreTest extends AbstractRedisCatalogTest {

    /**
     * Test verifies that that a data store that has been added to the catalog properly
     * saves all fields to redis and that the store retrieved from each store query method
     * is identical to the store originally saved to redis.
     */
    @Test
    public void testAddDataStore() {
        template.getConnectionFactory().getConnection().flushAll();

        String workspaceName = "TestWorkspace";
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName(workspaceName);
        workspace = catalogFacade.add(workspace);

        String storeName = "TestStore";
        StoreInfo store = new DataStoreInfoImpl(catalogFacade.getCatalog());
        store.setName(storeName);
        store.setWorkspace(workspace);
        store = catalogFacade.add(store);

        DataStoreInfo newStore = catalogFacade.getStore(store.getId(), DataStoreInfo.class);
        assertThat(newStore).isNotNull();
        assertThat(newStore).isEqualToIgnoringNullFields(store);

        DataStoreInfo storeByName = catalogFacade.getStoreByName(workspace, storeName, DataStoreInfo.class);
        assertThat(storeByName).isNotNull();
        assertThat(storeByName).isEqualToIgnoringNullFields(store);

        List<DataStoreInfo> storesByWorkspace = catalogFacade.getStoresByWorkspace(workspace, DataStoreInfo.class);
        assertThat(storesByWorkspace).isNotNull();
        assertThat(storesByWorkspace, hasSize(1));
        assertThat(storesByWorkspace.get(0)).isEqualToIgnoringNullFields(store);

        List<DataStoreInfo> stores = catalogFacade.getStores(DataStoreInfo.class);
        assertThat(stores).isNotNull();
        assertThat(stores, hasSize(1));
        assertThat(stores.get(0)).isEqualToIgnoringNullFields(store);
    }

    /***
     * Tests adding a data store, modifying values that are maintained in secondary
     * indexes (store name, workspace), saving the new data store, and ensuring all
     * of the old secondary indexes are cleaned up.
     */
    @Test
    public void testSaveDataStore() {
        template.getConnectionFactory().getConnection().flushAll();

        // create initial workspace and store
        String workspaceName = "TestWorkspace";
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName(workspaceName);
        workspace = catalogFacade.add(workspace);

        String storeName = "TestStore";
        StoreInfo store = new DataStoreInfoImpl(catalogFacade.getCatalog());
        store.setName(storeName);
        store.setWorkspace(workspace);
        store = catalogFacade.add(store);

        // save a reference to the secondary index keys
        String originalNameKey = RedisCatalogUtils.buildKey(DataStoreInfo.class, "name", store.getName());
        String originalWorkspaceKey = RedisCatalogUtils.buildKey(DataStoreInfo.class, "workspaceId", store.getWorkspace().getId());

        DataStoreInfo savedStore = catalogFacade.getStore(store.getId(), DataStoreInfo.class);
        assertThat(savedStore).isNotNull();

        // update the store name and workspace
        String newStoreName = "TestStore1";
        String newWorkspaceName = "TestWorkspace1";
        WorkspaceInfo newWorkspace = new WorkspaceInfoImpl();
        newWorkspace.setName(newWorkspaceName);
        newWorkspace = catalogFacade.add(newWorkspace);
        savedStore.setWorkspace(newWorkspace);
        savedStore.setName(newStoreName);

        catalogFacade.save(savedStore);

        DataStoreInfo updatedStore = catalogFacade.getStore(savedStore.getId(), DataStoreInfo.class);
        assertThat(updatedStore).isNotNull();

        // save a reference of the updated secondary index keys
        String updatedNameKey = RedisCatalogUtils.buildKey(DataStoreInfo.class, "name", updatedStore.getName());
        String updatedWorkspaceKey = RedisCatalogUtils.buildKey(DataStoreInfo.class, "workspaceId", updatedStore.getWorkspace().getId());

        // ensure the new secondary index keys are present in redis and the old ones are gone
        assertThat(updatedStore).isEqualToIgnoringNullFields(savedStore);
        assertThat(template.hasKey(originalNameKey)).isFalse();
        assertThat(template.hasKey(originalWorkspaceKey)).isFalse();
        assertThat(template.hasKey(updatedNameKey)).isTrue();
        assertThat(template.hasKey(updatedWorkspaceKey)).isTrue();
    }

    @Test
    public void deleteDataStore() {
        template.getConnectionFactory().getConnection().flushAll();

        // create initial workspace and store
        String workspaceName = "TestWorkspace";
        WorkspaceInfo workspace = new WorkspaceInfoImpl();
        workspace.setName(workspaceName);
        workspace = catalogFacade.add(workspace);

        String storeName = "TestStore";
        StoreInfo store = new DataStoreInfoImpl(catalogFacade.getCatalog());
        store.setName(storeName);
        store.setWorkspace(workspace);
        store = catalogFacade.add(store);

        // save a reference to the secondary index keys
        String originalNameKey = RedisCatalogUtils.buildKey(StoreInfo.class, "name", store.getName());
        String originalWorkspaceKey = RedisCatalogUtils.buildKey(StoreInfo.class, "workspace", store.getWorkspace().getId());

        DataStoreInfo savedStore = catalogFacade.getStore(store.getId(), DataStoreInfo.class);
        assertThat(savedStore).isNotNull();

        catalogFacade.remove(store);
        assertThat(template.hasKey(originalNameKey)).isFalse();
        assertThat(template.hasKey(originalWorkspaceKey)).isFalse();
        assertThat(template.opsForHash().hasKey(StoreInfo.class.getSimpleName(), store.getId())).isFalse();
    }
}
