/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import net.opengis.wps10.Wps10Factory;

public interface StratusWps10Factory extends Wps10Factory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    Wps10Factory eINSTANCE = StratusWps10FactoryImpl.init();
}
