
package org.mule.module.google.calendar.processors;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.RegistrationException;
import org.mule.common.DefaultResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.common.metadata.ConnectorMetaDataEnabled;
import org.mule.common.metadata.DefaultListMetaDataModel;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultPojoMetaDataModel;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.OperationMetaDataEnabled;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.datatype.DataTypeFactory;
import org.mule.module.google.calendar.GoogleCalendarConnector;
import org.mule.module.google.calendar.model.Event;
import org.mule.module.google.calendar.oauth.GoogleCalendarConnectorOAuthManager;
import org.mule.modules.google.oauth.invalidation.OAuthTokenExpiredException;
import org.mule.security.oauth.callback.ProcessCallback;
import org.mule.streaming.PagingConfiguration;
import org.mule.streaming.PagingDelegate;


/**
 * GetInstancesMessageProcessor invokes the {@link org.mule.module.google.calendar.GoogleCalendarConnector#getInstances(java.lang.String, java.lang.String, java.lang.Integer, boolean, java.lang.String, java.lang.String, org.mule.streaming.PagingConfiguration)} method in {@link GoogleCalendarConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.5.0-M4", date = "2014-04-08T10:25:26-05:00", comments = "Build M4.1875.17b58a3")
public class GetInstancesMessageProcessor
    extends AbstractPagedConnectedProcessor
    implements MessageProcessor, OperationMetaDataEnabled
{

    protected Object calendarId;
    protected String _calendarIdType;
    protected Object eventId;
    protected String _eventIdType;
    protected Object maxAttendess;
    protected Integer _maxAttendessType;
    protected Object showDeleted;
    protected boolean _showDeletedType;
    protected Object timezone;
    protected String _timezoneType;
    protected Object originalStart;
    protected String _originalStartType;
    protected Object pagingConfiguration;
    protected PagingConfiguration _pagingConfigurationType;

    public GetInstancesMessageProcessor(String operationName) {
        super(operationName);
    }

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object  has not been set already it will search the Mule registry for a default one.
     * 
     * @throws InitialisationException
     */
    public void initialise()
        throws InitialisationException
    {
    }

    @Override
    public void start()
        throws MuleException
    {
        super.start();
    }

    @Override
    public void stop()
        throws MuleException
    {
        super.stop();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * Sets pagingConfiguration
     * 
     * @param value Value to set
     */
    public void setPagingConfiguration(Object value) {
        this.pagingConfiguration = value;
    }

    /**
     * Sets timezone
     * 
     * @param value Value to set
     */
    public void setTimezone(Object value) {
        this.timezone = value;
    }

    /**
     * Sets calendarId
     * 
     * @param value Value to set
     */
    public void setCalendarId(Object value) {
        this.calendarId = value;
    }

    /**
     * Sets eventId
     * 
     * @param value Value to set
     */
    public void setEventId(Object value) {
        this.eventId = value;
    }

    /**
     * Sets originalStart
     * 
     * @param value Value to set
     */
    public void setOriginalStart(Object value) {
        this.originalStart = value;
    }

    /**
     * Sets showDeleted
     * 
     * @param value Value to set
     */
    public void setShowDeleted(Object value) {
        this.showDeleted = value;
    }

    /**
     * Sets maxAttendess
     * 
     * @param value Value to set
     */
    public void setMaxAttendess(Object value) {
        this.maxAttendess = value;
    }

    /**
     * Invokes the MessageProcessor.
     * 
     * @param event MuleEvent to be processed
     * @throws Exception
     */
    public PagingDelegate getPagingDelegate(final MuleEvent event, final PagingConfiguration pagingConfiguration)
        throws Exception
    {
        Object moduleObject = null;
        try {
            moduleObject = findOrCreate(GoogleCalendarConnectorOAuthManager.class, false, event);
            final String _transformedCalendarId = ((String) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_calendarIdType").getGenericType(), null, calendarId));
            final String _transformedEventId = ((String) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_eventIdType").getGenericType(), null, eventId));
            final Integer _transformedMaxAttendess = ((Integer) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_maxAttendessType").getGenericType(), null, maxAttendess));
            final Boolean _transformedShowDeleted = ((Boolean) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_showDeletedType").getGenericType(), null, showDeleted));
            final String _transformedTimezone = ((String) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_timezoneType").getGenericType(), null, timezone));
            final String _transformedOriginalStart = ((String) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_originalStartType").getGenericType(), null, originalStart));
            final PagingConfiguration _transformedPagingConfiguration = ((PagingConfiguration) evaluateAndTransform(getMuleContext(), event, GetInstancesMessageProcessor.class.getDeclaredField("_pagingConfigurationType").getGenericType(), null, pagingConfiguration));
            Object resultPayload;
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
            resultPayload = processTemplate.execute(new ProcessCallback<Object,Object>() {


                public List<Class<? extends Exception>> getManagedExceptions() {
                    return Arrays.asList(((Class<? extends Exception> []) new Class[] {OAuthTokenExpiredException.class }));
                }

                public boolean isProtected() {
                    return true;
                }

                public Object process(Object object)
                    throws Exception
                {
                    return ((GoogleCalendarConnector) object).getInstances(_transformedCalendarId, _transformedEventId, _transformedMaxAttendess, _transformedShowDeleted, _transformedTimezone, _transformedOriginalStart, _transformedPagingConfiguration);
                }

            }
            , this, event);
            return ((PagingDelegate) resultPayload);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Result<MetaData> getInputMetaData() {
        return new DefaultResult<MetaData>(null, (Result.Status.SUCCESS));
    }

    @Override
    public Result<MetaData> getOutputMetaData(MetaData inputMetadata) {
        return new DefaultResult<MetaData>(new DefaultMetaData(new DefaultListMetaDataModel(getPojoOrSimpleModel(Event.class))));
    }

    private MetaDataModel getPojoOrSimpleModel(Class clazz) {
        DataType dataType = DataTypeFactory.getInstance().getDataType(clazz);
        if (DataType.POJO.equals(dataType)) {
            return new DefaultPojoMetaDataModel(clazz);
        } else {
            return new DefaultSimpleMetaDataModel(dataType);
        }
    }

    public Result<MetaData> getGenericMetaData(MetaDataKey metaDataKey) {
        ConnectorMetaDataEnabled connector;
        try {
            connector = ((ConnectorMetaDataEnabled) findOrCreate(GoogleCalendarConnector.class, true, null));
            try {
                Result<MetaData> metadata = connector.getMetaData(metaDataKey);
                if ((Result.Status.FAILURE).equals(metadata.getStatus())) {
                    return metadata;
                }
                if (metadata.get() == null) {
                    return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), "There was an error processing metadata at GoogleCalendarConnector at getInstances retrieving was successful but result is null");
                }
                return metadata;
            } catch (Exception e) {
                return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
            }
        } catch (ClassCastException cast) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), "There was an error getting metadata, there was no connection manager available. Maybe you're trying to use metadata from an Oauth connector");
        } catch (ConfigurationException e) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
        } catch (RegistrationException e) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
        } catch (IllegalAccessException e) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
        } catch (InstantiationException e) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
        } catch (Exception e) {
            return new DefaultResult<MetaData>(null, (Result.Status.FAILURE), e.getMessage(), FailureType.UNSPECIFIED, e);
        }
    }

}