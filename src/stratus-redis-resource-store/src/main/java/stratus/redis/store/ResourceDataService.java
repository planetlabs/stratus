/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.store;

import lombok.extern.slf4j.Slf4j;
import org.geoserver.platform.resource.Resource;
import org.geoserver.platform.resource.Resource.Type;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import stratus.redis.repository.RedisRepository;
import stratus.redis.repository.RedisRepositoryImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author joshfix, Niels Charlier, tbarsballe
 */
@Slf4j
@Service("resourceDataService")
public class ResourceDataService {

    private final RedisRepository repository;
    private final boolean readOnlyRedis;

    public ResourceDataService(@Qualifier("redisRepositoryImpl") RedisRepositoryImpl repository) {
        this.repository = repository;
        readOnlyRedis = repository.isReadOnly();
    }

    protected static final String RESOURCE_PREFIX = Resource.class.getSimpleName() + ":";
    protected static final String ID_LAST_MODIFIED = "lastModified";
    protected static final String ID_TYPE = "type";
    protected static final String ID_CONTENT = "content";

    protected String hashKey(String path) {
        if (path.isEmpty()) {
            return RESOURCE_PREFIX;
        } else {
            return RESOURCE_PREFIX + "/" + path;
        }
    }

    @SuppressWarnings({ "unchecked" })
    protected boolean saveDirectory(String path, Date date) {
        if (readOnlyRedis) {
            return false;
        }
        //create parent directories if necessary
        if (!Paths.BASE.equals(path) && !saveDirectory(Paths.parent(path), date)) {
            return false;
        }
        String hashKey = hashKey(path);
        if (repository.getRedisHashRepository().getHashById(hashKey, ID_CONTENT) != null) {
            //already a resource in this place, cannot save as directory
            return false;
        }

        Map<String, Object> putMap = new HashMap<>();
        putMap.put(ID_LAST_MODIFIED, date);
        putMap.put(ID_TYPE, Type.DIRECTORY);
        repository.getRedisTemplate().opsForHash().putAll(hashKey, putMap);

        return true;
    }

    protected boolean saveResource(String path, byte[] content) {
        if (readOnlyRedis) {
            return false;
        }
        if (Paths.BASE.equals(path)) {
            return false;
        }

        String hashKey = hashKey(path);
        Date date = new Date();

        //update modified dates from parents and check if there aren't any resources already
        if (!saveDirectory(Paths.parent(path), date)) {
            return false;
        }

        Map<String, Object> putMap = new HashMap<>();
        putMap.put(ID_LAST_MODIFIED, date);
        putMap.put(ID_CONTENT, content);
        putMap.put(ID_TYPE, Type.RESOURCE);
        repository.getRedisTemplate().opsForHash().putAll(hashKey, putMap);
        log.debug("Saved content " + content.toString() + " to path " + path);

        return true;
    }

    /**
     * Creates a new resource
     *
     * @param path path to the new resource.
     * @param content content of the new resource
     * @return true if the resource was created successfully, false otherwise
     */
    public boolean createResource(String path, byte[] content) {
        if (readOnlyRedis) {
            return false;
        }
        String hashKey = hashKey(path);
        if (repository.keyExists(hashKey)) {
            // already exists
            return false;
        }
        return saveResource(path, content);
    }

    /**
     * Deletes a resource and all its children (if it is a directory)
     *
     * @param path path to the resource
     */
    public void delete(String path) {
        if (readOnlyRedis) {
            return;
        }
        String hashKey = hashKey(path);
        //delete all of its children in one go
        repository.deleteKeys(repository.scanKeys(hashKey + "/*"));
        //delete itself
        repository.deleteKey(hashKey);
    }

    /**
     * Renames a resource and all its children (if it is a directory)
     *
     * @param path old path to the resource
     * @param newPath new path to the resource
     */
    public void move(String path, String newPath) {
        if (readOnlyRedis) {
            return;
        }
        Date date = new Date();

        //rename itself and all of its children
        String hashKey = hashKey(path);
        String newHashKey = hashKey(newPath);

        //rename itself
        repository.renameKey(hashKey, newHashKey);
        //rename all of the children
        for (String child : repository.scanKeys(hashKey + "/*")) {
            String relativeChild = child.substring(hashKey.length());
            repository.renameKey(hashKey + relativeChild, newHashKey + relativeChild);
            //make sure parent directory exists
            saveDirectory(Paths.parent(newPath + relativeChild), date);
        }
    }

    /**
     * Obtains the content of the resource from the {@link #ID_CONTENT} key.
     *
     * @param path path to the resource
     * @return content of the resource
     */
    public byte[] getContent(String path) {
        String hashKey = hashKey(path);

        // check to see if it's an existing resource
        //if (repository.keyExists(hashKey)) {
        Object content = repository.getRedisHashRepository().getHashById(hashKey, ID_CONTENT);
        if (null != content) {
            byte[] contentBytes = (byte[]) content;
            log.debug("Got content " + new String(contentBytes) + " for path " + path);
            return contentBytes;
        }

        return null;
    }

    /**
     * Obtains the last modified date of the resource from the {@link #ID_LAST_MODIFIED} key.
     *
     * @param path path to the resource
     * @return last modified date of the resource
     */
    public Date getLastModified(String path) {
        String hashKey = hashKey(path);

        Object date = repository.getRedisHashRepository().getHashById(hashKey, ID_LAST_MODIFIED);
        if (null != date) {
            return (Date) date;
        }

        return null;
    }

    /**
     * Obtains the type of the resource from the {@link #ID_TYPE} key.
     *
     * @param path path to the resource
     * @return type of the resource
     */
    public Type getType(String path) {
        String hashKey = hashKey(path);

        Object type = repository.getRedisHashRepository().getHashById(hashKey, ID_TYPE);
        if (type != null && type instanceof Type) {
            return (Type) type;
        }
        return Type.UNDEFINED;
    }

    /**
     * Obtains the type and content of a resource from the {@link #ID_CONTENT} key.
     *
     * Type is calculated according to the following:
     * If the resource key exists, and the value of the hash for the {@link #ID_CONTENT} is null, type is Type.DIRECTORY
     * If the resource key exists, and the value of the hash for the {@link #ID_CONTENT} is not null, type is Type.RESOURCE
     * If the resource key does not exist, the type is Type.UNDEFINED
     *
     * This should be equivalent to the value of the {@link #ID_TYPE} key (used by {@link #getType(String)}.
     * Both methods are used in order to reduce calls to redis, since sometimes you just need the Type, and other times
     * you also need the content.
     *
     * If the type is Type.DIRECTORY or Type.UNDEFINED, then the content will be null.
     * If the type is Type.RESOURCE, then the content will not be null.
     *
     * @param path path to the resource
     * @return A {@link TypeAndContent} containing the type and content of the resource
     */
    public TypeAndContent getTypeAndContent(String path) {
        String hashKey = hashKey(path);
        // check to see if it's an existing resource
        if (repository.keyExists(hashKey)) {
            byte[] content = (byte[]) repository.getRedisHashRepository().getHashById(hashKey, ID_CONTENT);
            if (content != null) {
                log.debug("Got content " + new String(content) + " for path " + path);
            }
            //Note: content == null and getType() == Type.DIRECTORY should be equivalent.
            if (content == null) {
                return new TypeAndContent(Type.DIRECTORY, content);
            } else {
                return new TypeAndContent(Type.RESOURCE, content);
            }
        }
        return new TypeAndContent(Type.UNDEFINED, null);
    }

    /**
     * Returns the child resources of the resource at the provided path.
     *
     * @param path path to the resource
     * @return children of the resource
     */
    public List<String> getChildren(String path) {
        String hashKey = hashKey(path);
        Pattern p = Pattern.compile(hashKey + "/([^/]*)");

        List<String> children = new ArrayList<>();
        for (String child : repository.scanKeys(hashKey + "/*")) {
            Matcher m = p.matcher(child);
            if (m.matches()) {
                children.add(m.group(1));
            }
        }
        return children;
    }

    public RedisRepository getRepository() {
        return repository;
    }

    /**
     * A utility class containing a {@link Type} representing the type of a resource, and a {@link byte[]}
     * representing the content of a resource.
     */
    public static class TypeAndContent {
        private Type type;
        private byte[] content;

        public TypeAndContent(Type type, byte[] content) {
            this.type = type;
            this.content = content;
        }

        public Type getType() {
            return type;
        }

        public byte[] getContent() {
            return content;
        }
    }

}
