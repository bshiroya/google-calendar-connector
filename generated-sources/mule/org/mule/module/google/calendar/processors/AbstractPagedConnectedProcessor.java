
package org.mule.module.google.calendar.processors;

import java.lang.reflect.Type;
import javax.annotation.Generated;
import org.mule.streaming.processor.AbstractDevkitBasedPageableMessageProcessor;

@Generated(value = "Mule DevKit Version 3.5.0-SNAPSHOT", date = "2014-04-16T09:46:10-05:00", comments = "Build master.1915.dd1962d")
public abstract class AbstractPagedConnectedProcessor
    extends AbstractDevkitBasedPageableMessageProcessor
    implements ConnectivityProcessor
{


    public AbstractPagedConnectedProcessor(String operationName) {
        super(operationName);
    }

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public Type typeFor(String fieldName)
        throws NoSuchFieldException
    {
        return AbstractPagedConnectedProcessor.class.getDeclaredField(fieldName).getGenericType();
    }

}
