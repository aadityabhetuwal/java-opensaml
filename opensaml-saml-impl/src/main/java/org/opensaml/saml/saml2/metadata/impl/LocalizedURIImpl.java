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

package org.opensaml.saml.saml2.metadata.impl;

import org.opensaml.core.xml.LangBearing;
import org.opensaml.core.xml.schema.impl.XSURIImpl;
import org.opensaml.saml.saml2.metadata.LocalizedURI;

import com.google.common.base.Strings;

/**
 * Concrete implementation of {@link LocalizedURI}.
 */
public class LocalizedURIImpl extends XSURIImpl implements LocalizedURI {

    /** Language. */
    private String language;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespaceURI
     * @param elementLocalName the elementLocalName
     * @param namespacePrefix the namespacePrefix
     */
    protected LocalizedURIImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public String getXMLLang() {
        return language;
    }

    /** {@inheritDoc} */
    public void setXMLLang(final String newLang) {
        final boolean hasValue = newLang != null && !Strings.isNullOrEmpty(newLang);
        language = prepareForAssignment(language, newLang);
        manageQualifiedAttributeNamespace(LangBearing.XML_LANG_ATTR_NAME, hasValue);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + language.hashCode();
        return hash;
    }
}