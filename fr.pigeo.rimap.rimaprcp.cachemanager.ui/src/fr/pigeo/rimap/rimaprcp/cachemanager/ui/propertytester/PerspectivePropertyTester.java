package fr.pigeo.rimap.rimaprcp.cachemanager.ui.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;

/**
 * Property tester that checks the <code>elementId</code> of the currently active perspective
 */
public class PerspectivePropertyTester extends PropertyTester {

    /**
     * @param receiver the currently active {@link MPerspective}
     * @param property the property to test, in this case 'elementId'
     * @param args additional arguments, in this case an empty array
     * @param expectedValue the expected value of {@link MPerspective#getElementId()}
     */
    @Override
    public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
        final MPerspective perspective = (MPerspective) receiver;
        System.out.println("####################Oy oy, ça tourne###################");
        return perspective.getElementId().equals(expectedValue);
    }
}