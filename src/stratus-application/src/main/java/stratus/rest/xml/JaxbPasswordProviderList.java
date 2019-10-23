/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.rest.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="masterPasswordProviders")
public class JaxbPasswordProviderList {

    @XmlElement(name="masterPasswordProvider")
    protected List<String> providers;

    public JaxbPasswordProviderList(Collection<String> providers) {
        this.providers = new ArrayList<>(providers);
    }

}