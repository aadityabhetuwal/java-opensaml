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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;

/**
 * SAML 2.0 Core AssertionURIRef.
 */
public interface AssertionURIRef extends SAMLObject, XSURI, Evidentiary {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AssertionURIRef";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /**
     * Gets the URI of the assertion this references.
     * 
     * @return the URI of the assertion this references
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getAssertionURI() {
        DeprecationSupport.warn(ObjectType.METHOD, "getAssertionURI", AssertionURIRef.class.toString(), "getURI");
        return getURI();
    }

    /**
     * Sets the URI of the assertion this references.
     * 
     * @param uri the URI of the assertion this references
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setAssertionURI(@Nullable final String uri) {
        DeprecationSupport.warn(ObjectType.METHOD, "setAssertionURI", AssertionURIRef.class.toString(), "setURI");
        setURI(uri);
    }
    
}