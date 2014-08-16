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

package org.opensaml.security.config;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.security.messaging.CertificateNameOptions;
import org.opensaml.security.x509.X509CredentialValidationConfiguration;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.impl.BasicX509CredentialValidationConfiguration;

import com.google.common.collect.Sets;

/**
 * An initializer which initializes the global configuration instance of 
 * {@link X509CredentialValidationConfiguration}.
 */
public class X509CredentialValidationConfiguratonInitializer implements Initializer {

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        CertificateNameOptions nameOptions = new CertificateNameOptions();
        nameOptions.setEvaluateSubjectCommonName(true);
        nameOptions.setSubjectAltNames(Sets.newHashSet(X509Support.DNS_ALT_NAME, X509Support.URI_ALT_NAME));
        
        BasicX509CredentialValidationConfiguration config = new BasicX509CredentialValidationConfiguration();
        config.setCertificateNameOptions(nameOptions);
        
        ConfigurationService.register(X509CredentialValidationConfiguration.class, config);
    }

}
