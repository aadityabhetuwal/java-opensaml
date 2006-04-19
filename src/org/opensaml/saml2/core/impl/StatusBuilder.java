/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * 
 */

package org.opensaml.saml2.core.impl;

import org.opensaml.common.impl.AbstractSAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Status;

/**
 * Builder of {@link org.opensaml.saml2.core.impl.StatusImpl}
 */
public class StatusBuilder extends AbstractSAMLObjectBuilder<Status> {

    /**
     * Constructor
     */
    public StatusBuilder() {

    }

    /*
     * @see org.opensaml.common.impl.AbstractSAMLObjectBuilder#buildObject()
     */
    public Status buildObject() {
        return buildObject(SAMLConstants.SAML20P_NS, Status.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    }

    /*
     * @see org.opensaml.xml.XMLObjectBuilder#buildObject(java.lang.String, java.lang.String, java.lang.String)
     */
    public Status buildObject(String namespaceURI, String localName, String namespacePrefix) {
        return new StatusImpl(namespaceURI, localName, namespacePrefix);
    }
}