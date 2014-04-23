/**
 * Mule Google Calendars Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.mule.module.google.calendar;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.services.calendar.Calendar.Calendars;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import org.mule.api.annotations.*;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.oauth.*;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.module.google.calendar.model.*;
import org.mule.module.google.calendar.model.batch.CalendarBatchCallback;
import org.mule.module.google.calendar.model.batch.EventBatchCallback;
import org.mule.module.google.calendar.transformer.BatchResponseToBulkOperationTransformer;
import org.mule.modules.google.AbstractGoogleOAuthConnector;
import org.mule.modules.google.AccessType;
import org.mule.modules.google.ForcePrompt;
import org.mule.modules.google.GoogleUserIdExtractor;
import org.mule.modules.google.api.client.batch.BatchResponse;
import org.mule.modules.google.api.client.batch.VoidBatchCallback;
import org.mule.modules.google.api.datetime.DateTimeConstants;
import org.mule.modules.google.api.datetime.DateTimeUtils;
import org.mule.modules.google.api.pagination.TokenBasedPagingDelegate;
import org.mule.modules.google.oauth.invalidation.OAuthTokenExpiredException;
import org.mule.streaming.PagingConfiguration;
import org.mule.streaming.ProviderAwarePagingDelegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Google Calendars Cloud connector.
 * This connector covers almost all the Google Calendar API v3 using OAuth2 for authentication.
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="google-calendars", schemaVersion="1.0", friendlyName="Google Calendars", minMuleVersion="3.5", configElementName="config-with-oauth")
@OAuth2(
		authorizationUrl = "https://accounts.google.com/o/oauth2/auth",
		accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
		accessTokenRegex="\"access_token\"[ ]*:[ ]*\"([^\\\"]*)\"",
		expirationRegex="\"expires_in\"[ ]*:[ ]*([\\d]*)",
		refreshTokenRegex="\"refresh_token\"[ ]*:[ ]*\"([^\\\"]*)\"",
		authorizationParameters={
				@OAuthAuthorizationParameter(name="access_type", defaultValue="online", type=AccessType.class, description="Indicates if your application needs to access a Google API when the user is not present at the browser. " + 
											" Use offline to get a refresh token and use that when the user is not at the browser. Default is online", optional=true),
				@OAuthAuthorizationParameter(name="force_prompt", defaultValue="auto", type=ForcePrompt.class, description="Indicates if google should remember that an app has been authorized or if each should ask authorization every time. " + 
											" Use force to request authorization every time or auto to only do it the first time. Default is auto", optional=true)
		}
)
@ReconnectOn(exceptions = OAuthTokenExpiredException.class)
public class GoogleCalendarConnector extends AbstractGoogleOAuthConnector {
	
	/**
     * The OAuth2 consumer key 
     */
    @Configurable
    @OAuthConsumerKey
    private String consumerKey;

    /**
     * The OAuth2 consumer secret 
     */
    @Configurable
    @OAuthConsumerSecret
    private String consumerSecret;
    
    /**
     * Application name registered on Google API console
     */
    @Configurable
    @Default("Mule-GoogleCalendarConnector/1.0")
    private String applicationName;
    
    /**
     * The OAuth scopes you want to request
     */
    @OAuthScope
    @Configurable
    @Default(USER_PROFILE_SCOPE + " " + CalendarScopes.CALENDAR)
    private String scope;
    
    /**
     * Factory to instantiate the underlying google client.
     * Usually you don't need to override this. Most common
     * use case of a custom value here is testing.
     */
    @Configurable
    @Optional
    private GoogleCalendarClientFactory clientFactory;
    
    @OAuthAccessToken
    private String accessToken;
    
	/**
	 * The google api client
	 */
	private com.google.api.services.calendar.Calendar client;
	
	/**
	 * Initializes the connector. if no clientFactory was provided, then a default
     * {@link org.mule.module.google.calendar.DefaultGoogleCalendarClientFactory}
     * wil be used instead
     */
	@Start
	public void init() {
		if (this.clientFactory == null) {
			this.clientFactory = new DefaultGoogleCalendarClientFactory();
		}
		this.registerTransformer(new BatchResponseToBulkOperationTransformer());
	}
	
	@OAuthPostAuthorization
	public void postAuth() {
		this.client = this.clientFactory.newClient(this.getAccessToken(), this.getApplicationName());
		GoogleUserIdExtractor.fetchAndPublishAsFlowVar(this);
	}
	
    /**
     * Inserts a new calendar associated with the user that owns the current OAuth token
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:create-calendar}
     * 
     * @param calendar an instance of {@link org.mule.module.google.calendar.model.Calendar} with the information
     *        of the calendar you want to insert 
     * @return another instance of {@link org.mule.module.google.calendar.model.Calendar} representing the calendar
     *         that was created
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Calendar createCalendar(@Default("#[payload]") Calendar calendar) throws IOException {
  	   return new Calendar(this.client.calendars().insert(calendar.wrapped()).execute());
    }
    
    /**
     * Returns a paginated iterator with instances of {@link org.mule.module.google.calendar.model.CalendarList} listing the
     * calendars of the user that owns the OAuth access token.
     * 
     * Optional parameters are not considered in the search if not specified.
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-calendar-list}
     * 
     * @param showHidden if true, hidden calendars will be returned
     * @param pagingConfiguration the paging configuration object
     * @return a paginated iterator with instances of {@link org.mule.module.google.calendar.model.CalendarList}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    @Paged
    public ProviderAwarePagingDelegate<CalendarList, AbstractGoogleOAuthConnector> getCalendarList(
            final @Default("false") boolean showHidden,
    		final PagingConfiguration pagingConfiguration) throws IOException {
    	
    	return new TokenBasedPagingDelegate<CalendarList>() {
    		
    		@Override
            public List<CalendarList> doGetPage(AbstractGoogleOAuthConnector connector) throws IOException {
                com.google.api.services.calendar.Calendar.CalendarList.List calendars = client.calendarList().list();
				com.google.api.services.calendar.model.CalendarList list = calendars.setMaxResults(pagingConfiguration.getFetchSize())
						.setPageToken(this.getPageToken())
						.setShowHidden(showHidden)
						.execute();
				
				setPageToken(list.getNextPageToken());
				return CalendarList.valueOf(list.getItems(), CalendarList.class);
    		}
		};
    }
    
    /**
     * Returns a particular calendar list identified by id.
     * The user owning the OAuth token needs to have permissions for reading that list
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-calendar-list-by-id}
     * 
     * @param id the id of the calendar list you want to get
     * @return an instance of {@link org.mule.module.google.calendar.model.CalendarList}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public CalendarList getCalendarListById(String id) throws IOException {
    	return new CalendarList(this.client.calendarList().get(id).execute());
    }
    
    /**
     * Deletes a calendar list referenced by id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:delete-calendar-list}
     * 
     * @param id the id of the calendar list you want to delete
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void deleteCalendarList(String id) throws IOException {
    	this.client.calendarList().delete(id).execute();
    }
    
    /**
     * Updates a calendar list referenced by id with the content of one specified or taken from the message payload.
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:update-calendar-list}
     * 
     * @param id the id of the calendar list you want to update
     * @param calendarList an instance of {@link org.mule.module.google.calendar.model.CalendarList} with the content you want to have reflected
     * @return an instance of {@link org.mule.module.google.calendar.model.CalendarList} with reflecting the calendar's updated state
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public CalendarList updateCalendarList(String id, @Default("#[payload]") CalendarList calendarList) throws IOException {
    	return new CalendarList(this.client.calendarList().update(id, calendarList.wrapped()).execute());
    }
    
    
    /**
     * Returns a particular calendar specified by id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-calendar-by-id}
     * 
     * @param id the id of the calendar you want to get
     * @return an instance of {@link org.mule.module.google.calendar.model.Calendar}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Calendar getCalendarById(String id) throws IOException {
    	return new Calendar(this.client.calendars().get(id).execute());
    }
    
    /**
     * Updates a calendar referenced by id with the content of one specified or taken from the message payload.
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:update-calendar}
     * 
     * @param id the id of the calendar you want to get
     * @param calendar an instance of {@link org.mule.module.google.calendar.model.Calendar} with the content you want to have reflected
     * @return an instance of {@link org.mule.module.google.calendar.model.Calendar} representing the updated calendar's state
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Calendar updateCalendar(String id, @Default("#[payload]") Calendar calendar) throws IOException {
    	return new Calendar(this.client.calendars().update(id, calendar.wrapped()).execute());
    }
    
    /**
     * Deletes a particular calendar referenced by id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:delete-calendar}
     * 
     * @param id the id of the calendar you want to delete
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void deleteCalendar(String id) throws IOException {
    	this.client.calendars().delete(id).execute();
    }
    
    /**
     * Clears all the events on the calendar referenced by id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:clear-calendar}
     * 
     * @param id the id of the calendar you want to clear
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void clearCalendar(String id) throws IOException {
    	this.client.calendars().clear(id).execute();
    }
    
    
    /**
     * Searchs and returns events matching the criteria parameters. If a criteria is not specified, then it is not applied
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-events}
     * 
     * @param calendarId the id of the colendar that contains the events
     * @param icalUID Specifies iCalendar UID (iCalUID) of events to be included in the response
     * @param maxAttendees The maximum number of attendees to include in the response. If there are more than the
     *                     specified number of attendees, only the participant is returned.
     * @param orderBy The order of the events returned in the result
     * @param query Free text search terms to find events that match these terms in any field, except for
     *               extended properties
     * @param showDeleted Whether to include deleted events (with 'eventStatus' equals 'cancelled') in the result.
     * @param showHiddenInvitations Whether to include hidden invitations in the result
     * @param singleEvents whether to expand recurring events into instances and only return single one-off events and
     * 			instances of recurring events, but not the underlying recurring events themselves.
     * @param timeMin Lower bound timestamp (inclusive) for an event's end time to filter by 
     * @param timeMax Upper bound timestamp (exclusive) for an event's start time to filter by
     * @param timezone Timezone in which timeMin, timeMax and lastUpdated is to be considered on
     * @param lastUpdated Lower bound timestamp for an event's last modification time to filter by
     * @param datetimeFormat The timestamp format for timeMin, timeMax and lastUpdated. It defaults to RFC 3369 (yyyy-MM-dd'T'HH:mm:ssZ)
     * @param pagingConfiguration the paging configuration object
     * @return a paginated iterator of {@link org.mule.module.google.calendar.model.Event}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    @Paged
    public ProviderAwarePagingDelegate<Event, AbstractGoogleOAuthConnector> getEvents(
            final String calendarId,
            final @Optional String icalUID,
            final @Optional Integer maxAttendees,
    		final @Optional String orderBy,
    		final @Optional String query,
    		final @Default("false") boolean showDeleted,
    		final @Default("false") boolean showHiddenInvitations,
    		final @Default("true") boolean singleEvents,
    		final @Optional String timeMin,
    		final @Optional String timeMax,
    		final @Default(DateTimeConstants.RFC3339) String datetimeFormat,
    		final @Default("UTC") String timezone,
    		final @Optional String lastUpdated,
    		final PagingConfiguration pagingConfiguration) throws IOException {
    	
    	return new TokenBasedPagingDelegate<Event>() {
    		
    		@Override
            protected List<Event> doGetPage(AbstractGoogleOAuthConnector connector) throws IOException {
                com.google.api.services.calendar.Calendar.Events.List events = client.events().list(calendarId);

                com.google.api.services.calendar.model.Events result = events.setICalUID(icalUID)
                        .setMaxAttendees(maxAttendees)
    					.setMaxResults(pagingConfiguration.getFetchSize())
    					.setOrderBy(orderBy)
    					.setPageToken(this.getPageToken())
    					.setQ(query)
    					.setShowDeleted(showDeleted)
    					.setShowHiddenInvitations(showHiddenInvitations)
    					.setSingleEvents(singleEvents)
    					.setTimeMax(DateTimeUtils.parseDateTime(timeMax, datetimeFormat, timezone))
    					.setTimeMin(DateTimeUtils.parseDateTime(timeMin, datetimeFormat, timezone))
    					.setTimeZone(timezone)
    					.setUpdatedMin(DateTimeUtils.parseDateTime(lastUpdated, datetimeFormat, timezone))
    					.execute();
    			
    			this.setPageToken(result.getNextPageToken());
    			return Event.valueOf(result.getItems(), Event.class);
    		}
		};
    	
    }
    
    /**
     * Imports the given event into a calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:import-event}
     * 
     * @param calendarId the id of the target calendar
     * @param calendarEvent an instance of {@link org.mule.module.google.calendar.model.Event} representing the event to be imported
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the imported event on google's servers
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event importEvent(String calendarId, @Default("#[payload]") Event calendarEvent) throws IOException {
    	return new Event(this.client.events().calendarImport(calendarId, calendarEvent.wrapped()).execute());
    }
    
    /**
     * Deletes an event from a specified calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:delete-event}
     * 
     * @param calendarId the id of the calendar containing the event
     * @param eventId the event's id
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void deleteEvent(String calendarId, String eventId) throws IOException {
    	this.client.events().delete(calendarId, eventId).execute();
    }
    
    /**
     * Retrieves an event by id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-event-by-id}
     *  
     * @param calendarId the id of the calendar containing the event
     * @param eventId the event's id
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the event on google's servers
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event getEventById(String calendarId, String eventId) throws IOException {
    	return new Event(this.client.events().get(calendarId, eventId).execute());
    }
    
    /**
     * Inserts the given event into an specified calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:insert-event}
     * 
     * @param calendarId the id of the calendar that's to contain the event
     * @param calendarEvent an instance of {@link org.mule.module.google.calendar.model.Event} representing the event to be inserted 
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the event inserted on google's servers
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event insertEvent(String calendarId, @Default("#[payload]") Event calendarEvent) throws IOException {
    	return new Event(this.client.events().insert(calendarId, calendarEvent.wrapped()).execute());
    }
    
    
    /**
     * Inserts many events as a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-insert-event}
     * 
     * @param calendarId the id of the calendar that's receiving the calendars
     * @param calendarEvents a collection with instances of {@link org.mule.module.google.calendar.model.Event} that are to be inserted
     * @return an instance of {@link org.mule.modules.google.api.client.batch.BatchResponse<Event>}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public BatchResponse<Event> batchInsertEvent(
    			String calendarId,
    		    @Default("#[payload]") Collection<Event> calendarEvents) throws IOException {
    	
    	EventBatchCallback callback = new EventBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Events eventsClient = client.events();
    	
    	for (Event event : calendarEvents) {
    		eventsClient.insert(calendarId, event.wrapped()).queue(batch, callback);
    	}
    	
    	batch.execute();
    	
    	return callback.getResponse();
    }
    	
    /**
     * Updates many events in a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-update-event}
     * 
     * @param calendarId the id of the calendar containing the events to be updated
     * @param calendarEvents a collection with instances of {@link org.mule.module.google.calendar.model.Event} that are to be updated
     * @return an instance of {@link org.mule.modules.google.api.client.batch.BatchResponse<Event>}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public BatchResponse<Event> batchUpdateEvent(String calendarId, @Default("#[payload]") Collection<Event> calendarEvents) throws IOException {
    	
    	EventBatchCallback callback = new EventBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Events eventsClient = client.events();
    	
    	for (Event event : calendarEvents) {
    		eventsClient.update(calendarId, event.getId(), event.wrapped()).queue(batch, callback);
    	}
    	
    	batch.execute();
    	
    	return callback.getResponse();
    }
    
    /**
     * Deletes many events in a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-delete-event}
     * 
     * @param calendarId the id of the calendar containing the events to be deleted
     * @param calendarEvents a collection with instances of {@link org.mule.module.google.calendar.model.Event} that are to be deleted
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void batchDeleteEvent(String calendarId, @Default("#[payload]") Collection<Event> calendarEvents) throws IOException {
    	
    	VoidBatchCallback callback = new VoidBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Events eventsClient = client.events();
    	
    	for (Event event : calendarEvents) {
    		eventsClient.delete(calendarId, event.getId()).queue(batch, callback);
    	}
    	
    	batch.execute();
    }
    
    /**
     * Inserts many calendars in a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-insert-calendar}
     * 
     * @param calendars a collection with instances of @{link org.mule.module.google.calendar.model.Calendar} that are to be inserted
     * @return an instance of {@link org.mule.modules.google.api.client.batch.BatchResponse<Calendar>}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public BatchResponse<Calendar> batchInsertCalendar(@Default("#[payload]") Collection<Calendar> calendars) throws IOException {
    	
    	CalendarBatchCallback callback = new CalendarBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Calendars calendarsClient = client.calendars();
    	
    	for (Calendar calendar : calendars) {
    		calendarsClient.insert(calendar.wrapped()).queue(batch, callback);
    	}
    	
    	batch.execute();
    	
    	return callback.getResponse();
    }
    
    /**
     * Updates many calendars in a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-update-calendar}
     * 
     * @param calendars a collection with instances of @{link org.mule.module.google.calendar.model.Calendar} that are to be updated
     * @return an instance of {@link org.mule.modules.google.api.client.batch.BatchResponse<Calendar>}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public BatchResponse<Calendar> batchUpdateCalendar(@Default("#[payload]") Collection<Calendar> calendars) throws IOException {
    	
    	CalendarBatchCallback callback = new CalendarBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Calendars calendarsClient = client.calendars();
    	
    	for (Calendar calendar : calendars) {
    		calendarsClient.update(calendar.getId(), calendar.wrapped()).queue(batch, callback);
    	}
    	
    	batch.execute();
    	
    	return callback.getResponse();
    }
    
    /**
     * Deletes many events in a batch
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:batch-delete-calendar}
     * 
     * @param calendars a collection with instances of @{link org.mule.module.google.calendar.model.Calendar} that are to be deleted
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void batchDeleteCalendar(@Default("#[payload]") Collection<Calendar> calendars) throws IOException {
    	
    	VoidBatchCallback callback = new VoidBatchCallback();
    	com.google.api.services.calendar.Calendar client = this.client; 
    	
    	BatchRequest batch = client.batch();
    	Calendars calendarsClient = client.calendars();
    	
    	for (Calendar calendar : calendars) {
    		calendarsClient.delete(calendar.getId()).queue(batch, callback);
    	}
    	
    	batch.execute();
    	
    }
    		
    /**
     * For recurring events, it returns one instance of
     * {@link org.mule.module.google.calendar.model.Event} for each recurrence instance
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-instances}
     * 
     * @param calendarId the id of the calendar containing the recurrent event
     * @param eventId the id of a recurrent event
     * @param maxAttendess The maximum number of attendees to include in the response. If there are more than the
     * 						specified number of attendees, only the participant is returned.
     * @param showDeleted Whether to include deleted events (with 'eventStatus' equals 'cancelled') in the result.
     * @param timezone Time zone used in the response
     * @param originalStart The original start time of the instance in the result
     * @param pagingConfiguration the paging configuration object
     * @return an auto paginated iterator of {@link org.mule.module.google.calendar.model.Event}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    @Paged
    public ProviderAwarePagingDelegate<Event, AbstractGoogleOAuthConnector> getInstances(
            final String calendarId,
    				final String eventId,
    				final @Optional Integer maxAttendess,
    				final @Default("false") boolean showDeleted,
    				final @Default("UTC") String timezone,
    				final @Optional String originalStart,
    				final PagingConfiguration pagingConfiguration) throws IOException {
    	
    	return new TokenBasedPagingDelegate<Event>() {
    		
    		@Override
            protected List<Event> doGetPage(AbstractGoogleOAuthConnector connector) throws IOException {
                com.google.api.services.calendar.model.Events instances = client.events().instances(calendarId, eventId)
    					.setMaxAttendees(maxAttendess)
    					.setMaxResults(pagingConfiguration.getFetchSize())
    					.setOriginalStart(originalStart)
    					.setShowDeleted(showDeleted)
    					.setTimeZone(timezone)
    					.execute();
    			
    			this.setPageToken(instances.getNextPageToken());
    			return Event.valueOf(instances.getItems(), Event.class);
    		}
		};
    }
    
    /**
     * Moves a calendar form one calendar to another
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:move-event}
     * 
     * @param sourceCalendarId the id of the calendar that currently has the event
     * @param eventId the id of the event to be moved
     * @param targetCalendarId the id of the calendar to receive the event
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the moved event
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event moveEvent(String sourceCalendarId, String eventId, String targetCalendarId) throws IOException {
    	return new Event(this.client.events().move(sourceCalendarId, eventId, targetCalendarId).execute());
    }
    
    /**
     * Shortcut method for creating an event with nothing but a description text.
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:quick-add-event}
     * 
     * @param calendarId the id of the calendar to contain the new event
     * @param text description of the new event
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the newly created event
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event quickAddEvent(String calendarId, String text) throws IOException {
    	return new Event(this.client.events().quickAdd(calendarId, text).execute());
    }
    
    /**
     * Updates an event.
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:update-event}
     * 
     * @param calendarId the id of the calendar that contains the event to be updated
     * @param eventId the if of the event to be updated
     * @param calendarEvent an instance of {@link org.mule.module.google.calendar.model.Event} with the event's new state
     * @return an instance of {@link org.mule.module.google.calendar.model.Event} representing the updated event
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public Event updateEvent(String calendarId, String eventId, @Default("#[payload]") Event calendarEvent) throws IOException {
    	return new Event(this.client.events().update(calendarId, eventId, calendarEvent.wrapped()).execute());
    }
    
    /**
     * Returns the free time the authenticated user has between two times
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-free-time}
     * 
     * @param timeMin The start of the interval for the query
     * @param timeMax The end of the interval for the query
     * @param ids List of calendars and/or groups identifiers to query.
     * @param maxCalendarExpansion Maximal number of calendars for which FreeBusy information is to be provided
     * @param timezone Time zone used in the response
     * @param datetimeFormat the format to be used to parse timeMin and timeMax
     * @return an instance of {@link org.mule.module.google.calendar.model.FreeBusy}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public FreeBusy getFreeTime(
    		String timeMin,
			String timeMax,
			@Default("#[payload]") List<String> ids,
			@Default("UTC") String timezone,
			@Default(DateTimeConstants.RFC3339) String datetimeFormat,
			@Optional Integer maxCalendarExpansion) throws IOException {
    	
    	FreeBusyRequest query = new FreeBusyRequest();

    	List<FreeBusyRequestItem> items=new ArrayList<FreeBusyRequestItem>();
    	for(String calendarOrGroupId: ids){
    		items.add(new FreeBusyRequestItem().setId(calendarOrGroupId));
    	}
    	query.setItems(items);
    	query.setTimeMin(DateTimeUtils.parseDateTime(timeMin, datetimeFormat, timezone));
    	query.setTimeMax(DateTimeUtils.parseDateTime(timeMax, datetimeFormat, timezone));
    	query.setTimeZone(timezone);
    	query.setCalendarExpansionMax(maxCalendarExpansion);

    	return new FreeBusy(this.client.freebusy().query(query).execute());
    }
    
    /**
     * Inserts a new ACL rule in a calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:insert-acl-rule}
     * 
     * @param calendarId the id of the calendar affected by the rule
     * @param scope The email address of a user or group, or the name of a domain, depending on the scope type.
     * 				Omitted for type "default".
     * @param scopeType The type of the scope. Possible values are: - "default" - The public scope. This is the default
     * 			value.
     * 			"user" - Limits the scope to a single user.
     * 		   "group" - Limits the scope to a group.
     * 		   "domain" - Limits the scope to a domain.  Note: The permissions granted to the "default", or
     * 						public, scope apply to any user, authenticated or not.
     * @param role  The role assigned to the scope. Possible values are:
     * 			"none" - Provides no access.  -
   * 			"freeBusyReader" Provides read access to free/busy information.
   * 			"reader" - Provides read access to the calendar. Private events will appear to users with reader access, but event
   * 				details will be hidden.
   * 			"writer" - Provides read and write access to the calendar. Private events will appear to users with writer access,
   * 						and event details will be visible.
   * 			"owner" - Provides ownership of the calendar. This role has all of the permissions of the writer role with the additional
   * 						ability to see and manipulate ACLs.
     * @return an instance of {@link org.mule.module.google.calendar.model.AclRule} representing the newly created rule
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public AclRule insertAclRule(
    		String calendarId,
			String scope,
			ScopeType scopeType,
			ScopeRole role) throws IOException {
    	
    	AclRule rule = new AclRule();
    	Scope scopeObject = new Scope();

    	scopeObject.setType(scopeType.name().toLowerCase());
    	scopeObject.setValue(scope);
    	rule.setScope(scopeObject);
    	rule.setRole(role.name());
    	
    	return new AclRule(this.client.acl().insert(calendarId, rule.wrapped()).execute());
    }
    
    /**
     * Deletes an ACL rule on a calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:delete-acl-rule}
     * 
     * @param calendarId the id of the calendar loosing a rule
     * @param ruleId the id of the rule to be deleted
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public void deleteAclRule(String calendarId, String ruleId) throws IOException {
    	this.client.acl().delete(calendarId, ruleId).execute();
    }
    
    /**
     * Retrieves a Rule by Id
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-acl-rule-by-id}
     * 
     * @param calendarId the id of the calendar containing the rule
     * @param ruleId the id of the rule
     * @return an instance of {@link org.mule.module.google.calendar.model.AclRule}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public AclRule getAclRuleById(String calendarId, String ruleId) throws IOException {
    	return new AclRule(this.client.acl().get(calendarId, ruleId).execute());
    }
    
    /**
     * Returns all the Acl rules for a particular calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:get-all-acl-rules}
     * 
     * @param calendarId the id of the calendar containing the rules
     * @return a list of instances of {@link org.mule.module.google.calendar.model.AclRule}
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public List<AclRule> getAllAclRules(String calendarId) throws IOException {
    	return AclRule.valueOf(this.client.acl().list(calendarId).execute().getItems(), AclRule.class);
    }
    
    /**
     * Updates a rule on a given calendar
     * 
     * {@sample.xml ../../../doc/GoogleCalendarConnector.xml.sample google-calendars:update-acl-rule}
     * 
     * @param calendarId the id of the calendar containing the rule
     * @param ruleId the id of the rule to be modified
     * @param aclRule an instance of {@link org.mule.module.google.calendar.model.AclRule} containing the new state of the rule
     * @return an instance of {@link org.mule.module.google.calendar.model.AclRule} representing the new updated state of the rule
     * @throws IOException if there's a communication error
     */
    @Processor
    @OAuthProtected
    public AclRule updateAclRule(String calendarId,String ruleId, @Default("#[payload]") AclRule aclRule) throws IOException {
    	return new AclRule(this.client.acl().update(calendarId, ruleId, aclRule.wrapped()).execute());
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public GoogleCalendarClientFactory getClientFactory() {
		return clientFactory;
	}

	public void setClientFactory(GoogleCalendarClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

    public Object getClient() {
        return this.client;
    }
}
