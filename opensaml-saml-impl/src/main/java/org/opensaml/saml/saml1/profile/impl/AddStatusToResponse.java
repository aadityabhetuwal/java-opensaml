/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml1.profile.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.saml.saml1.core.StatusCode;
import org.opensaml.saml.saml1.core.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Action that sets {@link Status} content in a {@link Response} obtained from
 * a lookup strategy, typically from the outbound message context.
 * 
 * <p>If the message already contains status information, this action will overwrite it.</p>
 * 
 * <p>Options allows for the creation of a {@link StatusMessage} either explicitly,
 * or via lookup strategy.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddStatusToResponse extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddStatusToResponse.class);

    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;
    
    /** Predicate determining whether detailed error information is permitted. */
    @Nonnull private Predicate<ProfileRequestContext> detailedErrorsCondition;

    /** Optional method to obtain status codes. */
    @Nullable private Function<ProfileRequestContext,List<QName>> statusCodesLookupStrategy;

    /** Optional method to obtain a status message. */
    @Nullable private Function<ProfileRequestContext,String> statusMessageLookupStrategy;
    
    /** One or more default status codes to insert. */
    @Nonnull @NonnullElements private List<QName> defaultStatusCodes;
    
    /** A default status message to include. */
    @Nullable private String statusMessage;
    
    /** Whether to include detailed status information. */
    private boolean detailedErrors;
    
    /** Response to modify. */
    @Nullable private Response response;
    
    /** Constructor. */
    public AddStatusToResponse() {
        responseLookupStrategy = new MessageLookup<>(Response.class).compose(new OutboundMessageContextLookup());
        detailedErrorsCondition = Predicates.alwaysFalse();
        defaultStatusCodes = Collections.emptyList();
        detailedErrors = false;
    }

    /**
     * Set the predicate used to determine the detailed errors condition.
     * 
     * @param condition predicate for detailed errors condition
     */
    public void setDetailedErrorsCondition(@Nonnull final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();
        detailedErrorsCondition =
                Constraint.isNotNull(condition, "Detailed errors condition cannot be null");
    }

    /**
     * Set the optional strategy used to obtain status codes to include.
     * 
     * @param strategy strategy used to obtain status codes
     */
    public void setStatusCodesLookupStrategy(@Nullable final Function<ProfileRequestContext,List<QName>> strategy) {
        checkSetterPreconditions();
        statusCodesLookupStrategy = strategy;
    }
    
    /**
     * Set the optional strategy used to obtain a status message to include.
     * 
     * @param strategy strategy used to obtain a status message
     */
    public void setStatusMessageLookupStrategy(@Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        statusMessageLookupStrategy = strategy;
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        checkSetterPreconditions();
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the default list of status code values to insert, ordered such that the top level code is first
     * and every other code will be nested inside the previous one.
     * 
     * @param codes list of status code values to insert
     */
    public void setStatusCodes(@Nonnull @NonnullElements final List<QName> codes) {
        checkSetterPreconditions();
        defaultStatusCodes = List.copyOf(Constraint.isNotNull(codes, "Status code list cannot be null"));
    }
    
    /**
     * Set a default status message to use in the event that error detail is off,
     * or no specific message is obtained.
     * 
     * @param message default status message
     */
    public void setStatusMessage(@Nullable final String message) {
        checkSetterPreconditions();
        statusMessage = StringSupport.trimOrNull(message);
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        checkComponentActive();
        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} Response message was not returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        detailedErrors = detailedErrorsCondition.test(profileRequestContext);
        
        log.debug("{} Detailed errors are {}", getLogPrefix(), detailedErrors ? "enabled" : "disabled");
        
        return super.doPreExecute(profileRequestContext);
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Status>getBuilderOrThrow(Status.TYPE_NAME);

        final Status status = statusBuilder.buildObject();
        response.setStatus(status);
        
        if (statusCodesLookupStrategy != null) {
            final List<QName> codes = statusCodesLookupStrategy.apply(profileRequestContext);
            if (codes == null || codes.isEmpty()) {
                buildStatusCode(status, defaultStatusCodes);
            } else {
                buildStatusCode(status, codes);
            }
        } else {
            buildStatusCode(status, defaultStatusCodes);
        }
                
        // StatusMessage processing.
        if (!detailedErrors || statusMessageLookupStrategy == null) {
            if (statusMessage != null) {
                log.debug("{} Setting StatusMessage to defaulted value", getLogPrefix());
                buildStatusMessage(status, statusMessage);
            }
        } else if (statusMessageLookupStrategy != null) {
            final String message = statusMessageLookupStrategy.apply(profileRequestContext);
            if (message != null) {
                log.debug("{} Current state of request was mappable, setting StatusMessage to mapped value",
                        getLogPrefix());
                buildStatusMessage(status, message);
            } else if (statusMessage != null) {
                log.debug("{} Current state of request was not mappable, setting StatusMessage to defaulted value",
                        getLogPrefix());
                buildStatusMessage(status, statusMessage);
            }
        }
    }
    
    /**
     * Build and attach {@link StatusCode} element.
     * 
     * @param status    the element to attach to
     * @param codes     the status codes to use
     */
    private void buildStatusCode(@Nonnull final Status status, @Nonnull @NonnullElements final List<QName> codes) {
        final SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<StatusCode>getBuilderOrThrow(
                        StatusCode.TYPE_NAME);
                
        // Build nested StatusCodes.
        StatusCode statusCode = statusCodeBuilder.buildObject();
        status.setStatusCode(statusCode);
        if (codes.isEmpty()) {
            statusCode.setValue(StatusCode.RESPONDER);
        } else {
            statusCode.setValue(codes.get(0));
            final Iterator<QName> i = codes.iterator();
            i.next();
            while (i.hasNext()) {
                final StatusCode subcode = statusCodeBuilder.buildObject();
                subcode.setValue(i.next());
                statusCode.setStatusCode(subcode);
                statusCode = subcode;
            }
        }
    }
    
    /**
     * Build and attach {@link StatusMessage} element.
     * 
     * @param status    the element to attach to
     * @param message   the message to set
     */
    private void buildStatusMessage(@Nonnull final Status status, @Nonnull @NotEmpty final String message) {
        final SAMLObjectBuilder<StatusMessage> statusMessageBuilder = (SAMLObjectBuilder<StatusMessage>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<StatusMessage>getBuilderOrThrow(
                        StatusMessage.DEFAULT_ELEMENT_NAME);
        final StatusMessage sm = statusMessageBuilder.buildObject();
        sm.setValue(message);
        status.setStatusMessage(sm);
    }
    
    /** A default method to map event IDs to SAML 1 StatusCode QNames based on {@link EventContext}. */
    public static class StatusCodeMappingFunction implements Function<ProfileRequestContext,List<QName>> {

        /** Code mappings. */
        @Nonnull @NonnullElements private Map<String,List<QName>> codeMappings;
        
        /** Strategy function for access to {@link EventContext} to check. */
        @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;
        
        /**
         * Constructor.
         *
         * @param mappings the status code mappings to use
         */
        public StatusCodeMappingFunction(@Nonnull @NonnullElements final Map<String,List<QName>> mappings) {
            Constraint.isNotNull(mappings, "Status code mappings cannot be null");
            
            codeMappings = new HashMap<>(mappings.size());
            for (final Map.Entry<String,List<QName>> entry : mappings.entrySet()) {
                final String event = StringSupport.trimOrNull(entry.getKey());
                if (event != null && entry.getValue() != null) {
                    codeMappings.put(event, List.copyOf(entry.getValue()));
                }
            }
            
            eventContextLookupStrategy = new CurrentOrPreviousEventLookup();
        }
        
        /**
         * Set lookup strategy for {@link EventContext} to check.
         * 
         * @param strategy  lookup strategy
         */
        public void setEventContextLookupStrategy(
                @Nonnull final Function<ProfileRequestContext,EventContext> strategy) {
            eventContextLookupStrategy = Constraint.isNotNull(strategy, "EventContext lookup strategy cannot be null");
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public List<QName> apply(@Nullable final ProfileRequestContext input) {
            final EventContext eventCtx = eventContextLookupStrategy.apply(input);
            if (eventCtx != null && eventCtx.getEvent() != null) {
                return codeMappings.get(eventCtx.getEvent().toString());
            }
            return Collections.emptyList();
        }
    }
    
}