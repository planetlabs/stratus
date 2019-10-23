/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.geoserver.info;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

/**
 * @author joshfix
 * Created on 6/6/18
 *
 * @deprecated - Used by Stratus 1.2 catalogs only; preserved for backwards-compatibility
 */
@Deprecated
@Data
@RedisHash("ServiceInfo")
public class ServiceInfoWrapper implements Serializable {

    private static final long serialVersionUID = -5713627900623659500L;
    @Indexed
    private String id;

    @Indexed
    private String clazz;

    @Indexed
    private String name;

    @Indexed
    private String workspaceId;

    private String serializedObject;

}
