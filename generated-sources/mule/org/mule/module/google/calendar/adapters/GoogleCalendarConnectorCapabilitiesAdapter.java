
package org.mule.module.google.calendar.adapters;

import javax.annotation.Generated;
import org.mule.module.google.calendar.GoogleCalendarConnector;
import org.mule.module.google.calendar.basic.Capabilities;
import org.mule.module.google.calendar.basic.Capability;


/**
 * A <code>GoogleCalendarConnectorCapabilitiesAdapter</code> is a wrapper around {@link GoogleCalendarConnector } that implements {@link org.mule.api.Capabilities} interface.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.3", date = "2014-03-26T12:32:33-05:00", comments = "Build 3.4.3.1620.30ea288")
public class GoogleCalendarConnectorCapabilitiesAdapter
    extends GoogleCalendarConnector
    implements Capabilities
{


    /**
     * Returns true if this module implements such capability
     * 
     */
    public boolean isCapableOf(Capability capability) {
        if (capability == Capability.LIFECYCLE_CAPABLE) {
            return true;
        }
        if (capability == Capability.OAUTH2_CAPABLE) {
            return true;
        }
        return false;
    }

}
