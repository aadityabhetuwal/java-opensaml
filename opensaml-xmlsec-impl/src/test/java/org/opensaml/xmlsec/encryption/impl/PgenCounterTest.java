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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.PgenCounter;

/**
 *
 */
public class PgenCounterTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedCryptoBinaryContent;

    /**
     * Constructor
     *
     */
    public PgenCounterTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/PgenCounter.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedCryptoBinaryContent = "someCryptoBinaryValue";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final PgenCounter cbType = (PgenCounter) unmarshallElement(singleElementFile);
        
        assert cbType != null;
        Assert.assertEquals(expectedCryptoBinaryContent, cbType.getValue(), "pgenCounter value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final PgenCounter cbType = (PgenCounter) buildXMLObject(PgenCounter.DEFAULT_ELEMENT_NAME);
        cbType.setValue(expectedCryptoBinaryContent);
        
        assertXMLEquals(expectedDOM, cbType);
    }

}
