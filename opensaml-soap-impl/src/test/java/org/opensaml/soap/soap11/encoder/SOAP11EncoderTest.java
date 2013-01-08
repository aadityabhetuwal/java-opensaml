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

package org.opensaml.soap.soap11.encoder;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

import org.custommonkey.xmlunit.Diff;
import net.shibboleth.utilities.java.support.xml.XMLAssertTestNG;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPHelper;
import org.opensaml.ws.message.BaseMessageContext;
import org.opensaml.ws.message.MessageContext;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.ws.message.handler.BasicHandlerChain;
import org.opensaml.ws.message.handler.Handler;
import org.opensaml.ws.message.handler.HandlerChain;
import org.opensaml.ws.message.handler.HandlerException;
import org.opensaml.ws.message.handler.StaticHandlerChainResolver;
import org.opensaml.ws.transport.OutputStreamOutTransportAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test basic SOAP 1.1 message encoding.
 */
public class SOAP11EncoderTest extends XMLObjectBaseTestCase {
    
    private TestContext messageContext;
    
    private SOAP11Encoder encoder;
    
    private HandlerChain handlerChain;

    /** {@inheritDoc} */
    @BeforeMethod
    protected void setUp() throws Exception {
        messageContext = new TestContext();
        encoder = new SOAP11Encoder();
        
        handlerChain = new BasicHandlerChain();
        StaticHandlerChainResolver handlerChainResolver = new StaticHandlerChainResolver(handlerChain);
        messageContext.setOutboundHandlerChainResolver(handlerChainResolver);
    }
    
    /**
     * Test basic encoding of a message in an envelope.
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     */
    @Test
    public void testBasicEncoding() throws XMLParserException, UnmarshallingException, MessageEncodingException {
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        Envelope env = (Envelope) parseUnmarshallResource(soapMessage, true);
        Assert.assertNull(env.getDOM());
        
        XMLObject msg = env.getBody().getUnknownXMLObjects().get(0);
        msg.setParent(null);
        messageContext.bodyMessage = msg;
        handlerChain.getHandlers().add(new TestBodyHandler());
        
        messageContext.setOutboundMessageTransport(getOutTransport());
        encoder.encode(messageContext);
        
        Envelope encodedEnv = (Envelope) getEncodedMessage(messageContext);
        Assert.assertNotNull(encodedEnv.getDOM());
        
        XMLAssertTestNG.assertXMLIdentical(new Diff(controlEnv.getDOM().getOwnerDocument(), encodedEnv.getDOM().getOwnerDocument()), true);
    }
    
    /**
     * Test encoding with a header added by a message context handler.
     * @throws XMLParserException
     * @throws UnmarshallingException
     * @throws MessageEncodingException
     */
    @Test
    public void testEncodingWithHandler() throws XMLParserException, UnmarshallingException, MessageEncodingException {
        String controlMessage = "/data/org/opensaml/soap/soap11/SOAPHeaderMustUnderstand.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(controlMessage, false);
        
        String soapMessage = "/data/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope env = (Envelope) parseUnmarshallResource(soapMessage, true);
        Assert.assertNull(env.getDOM());
        
        XMLObject msg = env.getBody().getUnknownXMLObjects().get(0);
        msg.setParent(null);
        messageContext.bodyMessage = msg;
        messageContext.transaction = "5";
        handlerChain.getHandlers().add(new TestBodyHandler());
        handlerChain.getHandlers().add(new TestHeaderHandler());
        
        messageContext.setOutboundMessageTransport(getOutTransport());
        encoder.encode(messageContext);
        
        Envelope encodedEnv = (Envelope) getEncodedMessage(messageContext);
        Assert.assertNotNull(encodedEnv.getDOM());
        
        XMLAssertTestNG.assertXMLIdentical(new Diff(controlEnv.getDOM().getOwnerDocument(), encodedEnv.getDOM().getOwnerDocument()), true);
    }
    
    
    //
    // Helper stuff
    //
    
    protected XMLObject getEncodedMessage(TestContext messageContext) throws XMLParserException, UnmarshallingException {
        OutputStreamOutTransportAdapter adapter = (OutputStreamOutTransportAdapter) messageContext.getOutboundMessageTransport();
        ByteArrayOutputStream baos = (ByteArrayOutputStream) adapter.getOutgoingStream();
        return parseUnmarshallResourceByteArray(baos.toByteArray(), false);
    }

    protected OutputStreamOutTransportAdapter getOutTransport() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new OutputStreamOutTransportAdapter(baos);
    }

    protected XMLObject parseUnmarshallResource(String resource, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        Document soapDoc = parserPool.parse(this.getClass().getResourceAsStream(resource));
        return unmarshallXMLObject(soapDoc, dropDOM);
    }
    
    protected XMLObject parseUnmarshallResourceByteArray(byte [] input, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        Document soapDoc = parserPool.parse(bais);
        return unmarshallXMLObject(soapDoc, dropDOM);
    }

    protected XMLObject unmarshallXMLObject(Document soapDoc, boolean dropDOM) throws UnmarshallingException {
        Element envelopeElem = soapDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        if (dropDOM) {
            envelope.releaseDOM();
            envelope.releaseChildrenDOM(true);
        }
        return envelope;
    }
    
    public class TestContext extends BaseMessageContext {
        public  XMLObject bodyMessage;
        public String transaction;
    }
    
    public class TestBodyHandler implements Handler {
        
        /** {@inheritDoc} */
        public void invoke(MessageContext msgContext) throws HandlerException {
            TestContext context = (TestContext) msgContext;
            Envelope env = (Envelope) context.getOutboundMessage();
            env.getBody().getUnknownXMLObjects().add(context.bodyMessage);
        }
    }
    
    public class TestHeaderHandler implements Handler {
        
        private QName tHeaderName = new QName("http://example.org/soap/ns/transaction", "Transaction", "t");
        
        /** {@inheritDoc} */
        public void invoke(MessageContext msgContext) throws HandlerException {
            TestContext context = (TestContext) msgContext;
            Envelope env = (Envelope) context.getOutboundMessage();
            if (env.getHeader() == null) {
               env.setHeader((Header) buildXMLObject(Header.DEFAULT_ELEMENT_NAME));
            }
            XSAny tHeader =
                (XSAny) builderFactory.getBuilder(XMLObjectProviderRegistrySupport.getDefaultProviderQName()).buildObject(tHeaderName);
            tHeader.setTextContent(context.transaction);
            SOAPHelper.addSOAP11MustUnderstandAttribute(tHeader, true);
            env.getHeader().getUnknownXMLObjects().add(tHeader);
        }
    }

}
