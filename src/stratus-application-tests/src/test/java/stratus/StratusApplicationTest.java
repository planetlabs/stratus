/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus;

import org.geoserver.data.test.CiteTestData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StratusApplicationTest extends StratusApplicationTestSupport {

    @Test
    public void testWorkspaces() {
        assertEquals(catalog.getWorkspaces().size(), catalogImpl.getWorkspaces().size());
        assertNotNull(catalog.getWorkspaceByName(CiteTestData.CITE_PREFIX));
    }
}
