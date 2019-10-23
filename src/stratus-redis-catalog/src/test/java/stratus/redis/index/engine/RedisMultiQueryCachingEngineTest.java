/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.index.engine;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.Info;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.catalog.impl.DataStoreInfoImpl;
import org.geoserver.catalog.impl.FeatureTypeInfoImpl;
import org.geoserver.catalog.impl.WorkspaceInfoImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.core.convert.RedisData;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedisMultiQueryCachingEngineTest {

    private RedisMultiQueryCachingEngine engine;
    private RedisTemplate redisTemplate;
    private RedisConverter converter;
    private RedisOperations redisOperations;
    private RedisConnection redisConnection;

    @Before
    public void setUp() {
        //Reset all mock objects
        redisTemplate = EasyMock.createMock("redisTemplate", RedisTemplate.class);
        converter = EasyMock.createMock("converter", RedisConverter.class);
        redisOperations = EasyMock.createMock("redisOperations", RedisOperations.class);
        redisConnection = EasyMock.createMock("redisConnection", RedisConnection.class);


        engine = new RedisMultiQueryCachingEngine(redisTemplate, converter);
    }

    //Test one query, using multi execution
    @Test
    public void testSimpleQueryMulti() {
        //Setup mock
        Capture<SessionCallback> capturedSessionCallback = new Capture<>();
        EasyMock.expect(redisTemplate.execute(EasyMock.capture(capturedSessionCallback))).andAnswer(
                () -> capturedSessionCallback.getValue().execute(redisOperations));

        redisOperations.multi(); EasyMock.expectLastCall();

        RedisQueryKey key = new DefaultRedisQueryKey(TestInfo.class, "foo", "bar");
        Map<byte[], byte[]> result = new HashMap<>();
        result.put(key.getQueryKey().getBytes(), "foobar".getBytes());
        EasyMock.expect(redisOperations.execute(EasyMock.anyObject(RedisCallback.class))).andReturn(Collections.singletonList(result));

        RedisData resultData = new RedisData(result);
        resultData.setId(key.getId());
        resultData.setKeyspace(key.getKeyspace());

        EasyMock.expect(converter.read(EasyMock.eq(key.getQueryClass()), EasyMock.anyObject(RedisData.class))).andReturn(new TestInfo("foo", "bar"));

        EasyMock.replay(redisTemplate, converter, redisOperations);

        //Run test
        RedisValueQuery query = engine.get(key, null);
        engine.execute(false);

        assertTrue(query.isDone);
        assertTrue(query.responseObject instanceof TestInfo);
        assertEquals("foo", ((TestInfo)query.responseObject).getId());
        assertEquals("bar", ((TestInfo)query.responseObject).getValue());

        EasyMock.verify(redisTemplate, converter, redisOperations);
    }

    //Test one query, with a null result
    @Test
    public void testSimpleQueryMultiNull() {
        //Setup mock
        Capture<SessionCallback> capturedSessionCallback = new Capture<>();
        EasyMock.expect(redisTemplate.execute(EasyMock.capture(capturedSessionCallback))).andAnswer(
                () -> capturedSessionCallback.getValue().execute(redisOperations));

        redisOperations.multi(); EasyMock.expectLastCall();

        RedisQueryKey key = new DefaultRedisQueryKey(TestInfo.class, "foo", "bar");
        Map<byte[], byte[]> result = new HashMap<>();
        result.put(key.getQueryKey().getBytes(), null);
        EasyMock.expect(redisOperations.execute(EasyMock.anyObject(RedisCallback.class))).andReturn(Collections.singletonList(result));

        RedisData resultData = new RedisData(result);
        resultData.setId(key.getId());
        resultData.setKeyspace(key.getKeyspace());

        EasyMock.expect(converter.read(EasyMock.eq(key.getQueryClass()), EasyMock.anyObject(RedisData.class))).andReturn(null);

        EasyMock.replay(redisTemplate, converter, redisOperations);

        //Run test
        RedisValueQuery query = engine.get(key, null);
        engine.execute(false);

        assertTrue(query.isDone);
        assertEquals(null, query.responseObject);

        EasyMock.verify(redisTemplate, converter, redisOperations);
    }

    //Test one query, using parallel execution
    @Test
    public void testSimpleQueryParallel() {
        //Setup mock
        RedisQueryKey key = new DefaultRedisQueryKey(TestInfo.class, "foo", "bar");
        Map<byte[], byte[]> result = new HashMap<>();
        result.put(key.getQueryKey().getBytes(), "foobar".getBytes());
        EasyMock.expect(redisTemplate.execute(EasyMock.anyObject(RedisCallback.class))).andReturn(result);

        RedisData resultData = new RedisData(result);
        resultData.setId(key.getId());
        resultData.setKeyspace(key.getKeyspace());

        EasyMock.expect(converter.read(EasyMock.eq(key.getQueryClass()), EasyMock.anyObject(RedisData.class))).andReturn(new TestInfo("foo", "bar"));

        EasyMock.replay(redisTemplate, converter);

        //Run test
        RedisValueQuery query = engine.get(key, null);
        engine.execute(true);

        assertTrue(query.isDone);
        assertTrue(query.responseObject instanceof TestInfo);
        assertEquals("foo", ((TestInfo)query.responseObject).getId());
        assertEquals("bar", ((TestInfo)query.responseObject).getValue());

        EasyMock.verify(redisTemplate, converter);
    }

    /*
     * Test basic chaining x3
     *
     * WorkspaceByName -> StoreByName -> ResourceByStore
     * StoreId            ResourceId
     */
    @Test
    public void testChainingQueryMulti() {
        //Construct objects
        WorkspaceInfoImpl ws = new WorkspaceInfoImpl();
        ws.setId("ws");
        ws.setName("foo");

        DataStoreInfoImpl st = new DataStoreInfoImpl(null, "st");
        st.setWorkspace(ws);
        st.setName("bar");

        FeatureTypeInfoImpl ft = new FeatureTypeInfoImpl(null, "ft");
        ft.setStore(st);
        ft.setName("baz");

        //Construct queries
        RedisValueQuery wsQuery = engine.get(new DefaultRedisQueryKey(WorkspaceInfo.class, "ws"), null);
        RedisValueQuery stQuery = engine.getStoreByName(wsQuery, "bar", DataStoreInfo.class);
        RedisValueQuery ftQuery = engine.getResourceByStore(stQuery, "baz", FeatureTypeInfo.class);

        RedisQueryKey wsQueryKey = wsQuery.getKey();
        RedisKeyQuery stKeyQuery = (RedisKeyQuery) stQuery.getKey();
        RedisKeyQuery ftKeyQuery = (RedisKeyQuery) ftQuery.getKey();

        //Keys
        byte[] wsKey = "WorkspaceInfo:ws".getBytes();
        byte[] stKey = "DataStoreInfo:st".getBytes();
        byte[] ftKey = "FeatureTypeInfo:ft".getBytes();

        byte[] wsIdKey = "WorkspaceInfo:name:foo".getBytes();
        byte[] stIdKey1 = "DataStoreInfo:name:bar".getBytes();
        byte[] stIdKey2 = "DataStoreInfo:workspaceId:ws".getBytes();
        byte[] ftIdKey1 = "FeatureTypeInfo:name:baz".getBytes();
        byte[] ftIdKey2 = "FeatureTypeInfo:storeId:st".getBytes();

        Map<byte[], byte[]> wsResult = new HashMap<>();
        wsResult.put(wsKey, ws.getId().getBytes());
        Map<byte[], byte[]> stResult = new HashMap<>();
        stResult.put(stKey, st.getId().getBytes());
        Map<byte[], byte[]> ftResult = new HashMap<>();
        ftResult.put(ftKey, ft.getId().getBytes());

        Set<byte[]> stKeyResult = new HashSet<>();
        stKeyResult.add("st".getBytes());

        Set<byte[]> ftKeyResult = new HashSet<>();
        ftKeyResult.add("ft".getBytes());

        //Setup mock
        Capture<SessionCallback> capturedSessionCallback = new Capture<>();
        EasyMock.expect(redisTemplate.execute(EasyMock.capture(capturedSessionCallback))).andAnswer(
                () -> capturedSessionCallback.getValue().execute(redisOperations)).anyTimes();

        redisOperations.multi(); EasyMock.expectLastCall().anyTimes();

        Capture<RedisCallback> capturedRedisCallback = new Capture<>();
        EasyMock.expect(redisOperations.execute(EasyMock.capture(capturedRedisCallback))).andAnswer(
                () -> capturedRedisCallback.getValue().doInRedis(redisConnection)).anyTimes();

        redisConnection.multi();
        EasyMock.expectLastCall().anyTimes();

        redisConnection.close();
        EasyMock.expectLastCall().anyTimes();


        List<Object> group1Results = new ArrayList<>();
        //group 1
        EasyMock.expect(redisConnection.hGetAll(EasyMock.aryEq(wsKey))).andAnswer(() -> {
            group1Results.add(wsResult);
            return wsResult;
        });
        EasyMock.expect(redisConnection.sInter(EasyMock.aryEq(stIdKey2), EasyMock.aryEq(stIdKey1))).andAnswer(() -> {
            group1Results.add(stKeyResult);
            return stKeyResult;
        });

        redisConnection.exec();
        EasyMock.expectLastCall().andReturn(group1Results);

        List<Object> group2Results = new ArrayList<>();
        //group 2
        EasyMock.expect(redisConnection.hGetAll(EasyMock.aryEq(stKey))).andAnswer(() -> {
            group2Results.add(stResult);
            return stResult;
        });
        EasyMock.expect(redisConnection.sInter(EasyMock.aryEq(ftIdKey2), EasyMock.aryEq(ftIdKey1))).andAnswer(() -> {
            group2Results.add(ftKeyResult);
            return ftKeyResult;
        });

        redisConnection.exec();
        EasyMock.expectLastCall().andReturn(group2Results);

        //group 3
        EasyMock.expect(redisConnection.hGetAll(EasyMock.aryEq(ftKey))).andReturn(ftResult);
        redisConnection.exec();
        EasyMock.expectLastCall().andReturn(Arrays.asList(ftResult));


        EasyMock.expect(converter.read(EasyMock.eq(WorkspaceInfo.class), redisDataMatcher(ws.getId()))).andReturn(ws);
        EasyMock.expect(converter.read(EasyMock.eq(DataStoreInfo.class), redisDataMatcher(st.getId()))).andReturn(st);
        EasyMock.expect(converter.read(EasyMock.eq(FeatureTypeInfo.class), redisDataMatcher(ft.getId()))).andReturn(ft);

        EasyMock.replay(redisTemplate, converter, redisOperations, redisConnection);

        //Run test
        engine.execute(false);

        assertTrue(ftQuery.isDone);
        assertTrue(ftQuery.responseObject instanceof FeatureTypeInfo);
        assertEquals("ft", ((FeatureTypeInfo)ftQuery.responseObject).getId());
        assertEquals("baz", ((FeatureTypeInfo)ftQuery.responseObject).getName());

        EasyMock.verify(redisTemplate, converter, redisOperations);
    }

    /*
     * Test that null results don't cause chaining errors, and that groups with invalid keys are not executed
     *
     * WorkspaceByName        -> StoreByName [not executed] -> ResourceByStore [not executed]
     * StoreId [returns null]    ResourceId  [returns null]
     */
    @Test
    public void testChainingQueryMultiNull() {
        //Construct objects
        WorkspaceInfoImpl ws = new WorkspaceInfoImpl();
        ws.setId("ws");
        ws.setName("foo");

        DataStoreInfoImpl st = new DataStoreInfoImpl(null, "st");
        st.setWorkspace(ws);
        st.setName("bar");

        FeatureTypeInfoImpl ft = new FeatureTypeInfoImpl(null, "ft");
        ft.setStore(st);
        ft.setName("baz");

        //Construct queries
        RedisValueQuery wsQuery = engine.get(new DefaultRedisQueryKey(WorkspaceInfo.class, "ws"), null);
        RedisValueQuery stQuery = engine.getStoreByName(wsQuery, "bar", DataStoreInfo.class);
        RedisValueQuery ftQuery = engine.getResourceByStore(stQuery, "baz", FeatureTypeInfo.class);

        RedisQueryKey wsQueryKey = wsQuery.getKey();
        RedisKeyQuery stKeyQuery = (RedisKeyQuery) stQuery.getKey();
        RedisKeyQuery ftKeyQuery = (RedisKeyQuery) ftQuery.getKey();

        //Keys
        byte[] wsKey = "WorkspaceInfo:ws".getBytes();
        byte[] stKey = "DataStoreInfo".getBytes();
        byte[] ftKey = "FeatureTypeInfo".getBytes();

        byte[] wsIdKey = "WorkspaceInfo:name:foo".getBytes();
        byte[] stIdKey1 = "DataStoreInfo:name:bar".getBytes();
        byte[] stIdKey2 = "DataStoreInfo:workspaceId:ws".getBytes();
        byte[] ftIdKey1 = "FeatureTypeInfo:name:baz".getBytes();
        byte[] ftIdKey2 = "FeatureTypeInfo:storeId".getBytes();

        Map<byte[], byte[]> wsResult = new HashMap<>();
        wsResult.put(wsKey, ws.getId().getBytes());
        Map<byte[], byte[]> stResult = new HashMap<>();
        stResult.put(stKey, null);
        Map<byte[], byte[]> ftResult = new HashMap<>();
        ftResult.put(ftKey, ft.getId().getBytes());

        Set<byte[]> stKeyResult = new HashSet<>();

        Set<byte[]> ftKeyResult = new HashSet<>();

        //Setup mock
        Capture<SessionCallback> capturedSessionCallback = new Capture<>();
        EasyMock.expect(redisTemplate.execute(EasyMock.capture(capturedSessionCallback))).andAnswer(
                () -> capturedSessionCallback.getValue().execute(redisOperations)).anyTimes();

        redisOperations.multi(); EasyMock.expectLastCall().anyTimes();

        Capture<RedisCallback> capturedRedisCallback = new Capture<>();
        EasyMock.expect(redisOperations.execute(EasyMock.capture(capturedRedisCallback))).andAnswer(
                () -> capturedRedisCallback.getValue().doInRedis(redisConnection)).anyTimes();

        redisConnection.multi();
        EasyMock.expectLastCall().anyTimes();

        redisConnection.close();
        EasyMock.expectLastCall().anyTimes();

        List<Object> group1Results = new ArrayList<>();
        //group 1
        EasyMock.expect(redisConnection.hGetAll(EasyMock.aryEq(wsKey))).andAnswer(() -> {
            group1Results.add(wsResult);
            return wsResult;
        });
        EasyMock.expect(redisConnection.sInter(EasyMock.aryEq(stIdKey2), EasyMock.aryEq(stIdKey1))).andAnswer(() -> {
            group1Results.add(stKeyResult);
            return stKeyResult;
        });

        redisConnection.exec();
        EasyMock.expectLastCall().andReturn(group1Results);

        //group 2
        EasyMock.expect(redisConnection.hGetAll(EasyMock.aryEq(stKey))).andReturn(stResult);
        EasyMock.expect(redisConnection.sInter(EasyMock.aryEq(ftIdKey2), EasyMock.aryEq(ftIdKey1))).andReturn(ftKeyResult);

        redisConnection.exec();
        EasyMock.expectLastCall().andReturn(Arrays.asList(ftKeyResult));

        EasyMock.expect(converter.read(EasyMock.eq(WorkspaceInfo.class), redisDataMatcher(ws.getId()))).andReturn(ws);

        //Group three should never get executed

        EasyMock.replay(redisTemplate, converter, redisOperations, redisConnection);

        //Run test
        engine.execute(false);

        assertTrue(ftQuery.isDone);
        assertEquals(null, ftQuery.responseObject);

        EasyMock.verify(redisTemplate, converter, redisOperations);
    }

    /**
     * Minimal implementation of {@link Info}, for testing the caching engine
     */
    private static class TestInfo implements Info {

        String id;

        String value;

        public TestInfo(String id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Tests if a RedisData argument passed to a mock has a certain id
     * @param id the id to compare to
     * @return null
     */
    private static RedisData redisDataMatcher(String id) {
        EasyMock.reportMatcher(new RedisDataMatcher(id));
        return null;
    }

    /**
     * {@link IArgumentMatcher} used for {@link #redisDataMatcher(String)}
     */
    private static class RedisDataMatcher implements IArgumentMatcher {
        String id;

        public RedisDataMatcher(String id) {
            this.id = id;
        }

        @Override
        public boolean matches(Object argument) {
            return argument instanceof RedisData && Objects.equals(id, ((RedisData)argument).getId());
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append("RedisDataMatcher("+id+")");
        }
    }
}
