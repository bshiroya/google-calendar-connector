
package org.mule.module.google.calendar.config;

import javax.annotation.Generated;
import org.mule.api.processor.MessageProcessor;
import org.mule.config.spring.factories.MessageProcessorChainFactoryBean;
import org.mule.security.oauth.callback.DefaultRestoreAccessTokenCallback;

@Generated(value = "Mule DevKit Version 3.5.0-M4", date = "2014-03-26T12:34:07-05:00", comments = "Build M4.1875.17b58a3")
public class RestoreAccessTokenCallbackFactoryBean
    extends MessageProcessorChainFactoryBean
{


    public Class getObjectType() {
        return DefaultRestoreAccessTokenCallback.class;
    }

    public Object getObject()
        throws Exception
    {
        DefaultRestoreAccessTokenCallback callback = new DefaultRestoreAccessTokenCallback();
        callback.setMessageProcessor(((MessageProcessor) super.getObject()));
        return callback;
    }

}
