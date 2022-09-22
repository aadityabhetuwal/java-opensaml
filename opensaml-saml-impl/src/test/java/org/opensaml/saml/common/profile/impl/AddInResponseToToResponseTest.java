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

package org.opensaml.saml.common.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddInResponseToToResponse} unit test. */
public class AddInResponseToToResponseTest  extends OpenSAMLInitBaseTestCase {

    private ProfileRequestContext prc;
    
    @BeforeMethod
    public void setUp() {
        prc = new RequestContextBuilder().setInboundMessage(
                SAML1ActionTestingSupport.buildAttributeQueryRequest(null)).buildProfileRequestContext();
    }

    /**
     * Test that action errors out properly if there is no response.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test
    public void testNoResponse() throws ComponentInitializationException {
        final AddInResponseToToResponse action = new AddInResponseToToResponse();
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /**
     * Test that action proceeds properly if there is no request ID.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testNoRequestID() throws Exception {
        ((RequestAbstractType) prc.getInboundMessageContext().getMessage()).setID(null);
        
        final Response response = SAML1ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);
        
        final AddInResponseToToResponse action = new AddInResponseToToResponse();
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertNull(response.getInResponseTo());
    }
    
    @Test
    public void testSAML1Response() throws Exception {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        prc.getOutboundMessageContext().setMessage(response);

        final AddInResponseToToResponse action = new AddInResponseToToResponse();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertEquals(response.getInResponseTo(), SAML1ActionTestingSupport.REQUEST_ID);
    }
    
    @Test
    public void testSAML2Response() throws Exception {
        final LogoutResponse response = SAML2ActionTestingSupport.buildLogoutResponse();
        prc.getOutboundMessageContext().setMessage(response);

        final AddInResponseToToResponse action = new AddInResponseToToResponse();
        action.initialize();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertEquals(response.getInResponseTo(), SAML2ActionTestingSupport.REQUEST_ID);
    }
}
