/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.wps.executor;

import net.opengis.wps10.ExecuteResponseType;
import net.opengis.wps10.Wps10Factory;
import net.opengis.wps10.impl.Wps10FactoryImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

public class StratusWps10FactoryImpl extends Wps10FactoryImpl {

    public StratusWps10FactoryImpl(Wps10FactoryImpl theWps10Factory) {
        this.eAnnotations = theWps10Factory.getEAnnotations();
        this.ePackage = theWps10Factory.getEPackage();
    }

    public StratusWps10FactoryImpl(){

    }

    public static Wps10Factory init() {
        try {
            Wps10FactoryImpl theWps10Factory = (Wps10FactoryImpl) EPackage.Registry.INSTANCE.getEFactory("http://www.opengis.net/wps/1.0.0");
            if (theWps10Factory != null) {
                return new StratusWps10FactoryImpl(theWps10Factory);
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new StratusWps10FactoryImpl();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ExecuteResponseType createExecuteResponseType() {
        StratusExecuteResponseTypeImpl executeResponseType = new StratusExecuteResponseTypeImpl();
        return executeResponseType;
    }
}
