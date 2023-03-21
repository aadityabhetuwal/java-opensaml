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

package org.opensaml.soap.wsaddressing;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * The &lt;wsa:Address&gt; element.
 * 
 * @see "WS-Addressing 1.0 - Core"
 * 
 */
public interface Address extends AttributedURI {

    /** Element local name. */
    @Nonnull @NotEmpty public static final String ELEMENT_LOCAL_NAME = "Address";

    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME =
        new QName(WSAddressingConstants.WSA_NS, ELEMENT_LOCAL_NAME, WSAddressingConstants.WSA_PREFIX);

    /** Anonymous address URI. */
    @Nonnull @NotEmpty public static final String ANONYMOUS = WSAddressingConstants.WSA_NS + "/anonymous";

    /** None address URI. */
    @Nonnull @NotEmpty public static final String NONE = WSAddressingConstants.WSA_NS + "/none";
}
