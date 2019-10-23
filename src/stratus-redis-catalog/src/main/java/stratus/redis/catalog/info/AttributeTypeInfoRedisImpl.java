/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.catalog.info;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.geoserver.catalog.AttributeTypeInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.MetadataMap;
import org.opengis.feature.type.AttributeDescriptor;
import org.springframework.data.annotation.Transient;

@Slf4j
@Data
public class AttributeTypeInfoRedisImpl implements AttributeTypeInfo {

    private static final long serialVersionUID = 4875989364280835933L;

    protected String id;
    protected String name;
    protected int minOccurs;
    protected int maxOccurs;
    protected boolean nillable;
    protected MetadataMap metadata = new MetadataMap();
    protected Integer length;

    protected String bindingString;
    @Transient protected Class binding;

    @Transient protected transient AttributeDescriptor attribute;
    @Transient protected transient FeatureTypeInfo featureType;

    @Override
    public Class getBinding() {
        if (binding == null && bindingString != null) {
            try {
                binding = Class.forName(bindingString);
            } catch (ClassNotFoundException e) {
                log.warn("Could not convert attribute binding", e);
            }
        }
        return binding;
    }

    @Override
    public void setBinding(Class binding) {
        this.binding = binding;
        this.bindingString = binding == null ? null : binding.getName();
    }

    @Override
    public boolean equalsIngnoreFeatureType(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeTypeInfoRedisImpl other = (AttributeTypeInfoRedisImpl) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (binding == null) {
            if (other.binding != null)
                return false;
        } else if (!binding.equals(other.binding))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (length == null) {
            if (other.length != null)
                return false;
        } else if (!length.equals(other.length))
            return false;
        if (maxOccurs != other.maxOccurs)
            return false;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
            return false;
        if (minOccurs != other.minOccurs)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (nillable != other.nillable)
            return false;
        return true;
    }
}