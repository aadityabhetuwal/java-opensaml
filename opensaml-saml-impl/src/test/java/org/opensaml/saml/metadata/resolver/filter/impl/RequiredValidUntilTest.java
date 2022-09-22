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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolverTest;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** Unit test for {@link RequiredValidUntilFilter}. */
public class RequiredValidUntilTest extends XMLObjectBaseTestCase {

    private File metadataFile;

    @BeforeMethod
    protected void setUp() throws Exception {
        URL mdURL = FilesystemMetadataResolverTest.class
                .getResource("/org/opensaml/saml/saml2/metadata/simple-metadata.xml");
        metadataFile = new File(mdURL.toURI());
    }

    @Test
    public void testRequiredValidUntil() throws Exception {
        RequiredValidUntilFilter filter = new RequiredValidUntilFilter();
        filter.setMaxValidityInterval(Duration.ZERO);

        FilesystemMetadataResolver metadataProvider = new FilesystemMetadataResolver(metadataFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setMetadataFilter(filter);
        try {
            metadataProvider.initialize();
        } catch (ComponentInitializationException e) {
            Assert.fail("Filter disallowed metadata that contained a proper validUntil attribute");
        }
    }

    @Test
    public void testRequiredValidUntilWithMaxValidity() throws Exception {
        RequiredValidUntilFilter filter = new RequiredValidUntilFilter();
        filter.setMaxValidityInterval(Duration.ofSeconds(1));

        FilesystemMetadataResolver metadataProvider = new FilesystemMetadataResolver(metadataFile);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.setMetadataFilter(filter);

        try {
            metadataProvider.initialize();
            Assert.fail("Filter accepted metadata with longer than allowed validity period.");
        } catch (ComponentInitializationException e) {
            // we expect this
            return;
        }
    }
    
    @Test
    public void testRequiredValidUntilAlreadyPast() throws Exception {
        SAMLObjectBuilder<EntitiesDescriptor> entitiesDescriptorBuilder = (SAMLObjectBuilder<EntitiesDescriptor>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<EntitiesDescriptor>getBuilderOrThrow(
                        EntitiesDescriptor.TYPE_NAME);
        EntitiesDescriptor descriptor = entitiesDescriptorBuilder.buildObject();
        descriptor.setValidUntil(Instant.now().minusMillis(10000));

        RequiredValidUntilFilter filter = new RequiredValidUntilFilter();
        filter.setMaxValidityInterval(Duration.ofSeconds(-1));
        filter.filter(descriptor, new MetadataFilterContext());
        
        filter = new RequiredValidUntilFilter();
        filter.filter(descriptor, new MetadataFilterContext());
    }
    
}