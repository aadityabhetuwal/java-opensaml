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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;

/**
 * A concrete implementation of {@link org.opensaml.saml.saml2.core.Assertion}.
 */
public class AssertionImpl extends AbstractSignableSAMLObject implements Assertion {

    /** SAML Version of the assertion. */
    private SAMLVersion version;

    /** Issue Instant of the assertion. */
    private Instant issueInstant;

    /** ID of the assertion. */
    private String id;

    /** Issuer of the assertion. */
    private Issuer issuer;

    /** Subject of the assertion. */
    private Subject subject;

    /** Conditions of the assertion. */
    private Conditions conditions;

    /** Advice of the assertion. */
    private Advice advice;

    /** Statements of the assertion. */
    private final IndexedXMLObjectChildrenList<Statement> statements;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AssertionImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        version = SAMLVersion.VERSION_20;
        statements = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public SAMLVersion getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    public void setVersion(final SAMLVersion newVersion) {
        version = prepareForAssignment(version, newVersion);
    }

    /** {@inheritDoc} */
    public Instant getIssueInstant() {
        return issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(final Instant newIssueInstance) {
        issueInstant = prepareForAssignment(issueInstant, newIssueInstance);
    }

    /** {@inheritDoc} */
    public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    public Issuer getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    public void setIssuer(final Issuer newIssuer) {
        issuer = prepareForAssignment(issuer, newIssuer);
    }

    /** {@inheritDoc} */
    public Subject getSubject() {
        return subject;
    }

    /** {@inheritDoc} */
    public void setSubject(final Subject newSubject) {
        subject = prepareForAssignment(subject, newSubject);
    }

    /** {@inheritDoc} */
    public Conditions getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    public void setConditions(final Conditions newConditions) {
        conditions = prepareForAssignment(conditions, newConditions);
    }

    /** {@inheritDoc} */
    public Advice getAdvice() {
        return advice;
    }

    /** {@inheritDoc} */
    public void setAdvice(final Advice newAdvice) {
        advice = prepareForAssignment(advice, newAdvice);
    }

    /** {@inheritDoc} */
    public List<Statement> getStatements() {
        return statements;
    }

    /** {@inheritDoc} */
    public List<Statement> getStatements(final QName typeOrName) {
        return (List<Statement>) statements.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public List<AuthnStatement> getAuthnStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS, AuthnStatement.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AuthnStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    public List<AuthzDecisionStatement> getAuthzDecisionStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS,
                AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AuthzDecisionStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    public List<AttributeStatement> getAttributeStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AttributeStatement>) statements.subList(statementQName);
    }
    
    /** {@inheritDoc} */
    public String getSignatureReferenceID(){
        return id;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (issuer != null) {
            children.add(issuer);
        }
        
        if (getSignature() != null){
            children.add(getSignature());
        }
        
        if (subject != null) {
            children.add(subject);
        }
        
        if (conditions != null) {
            children.add(conditions);
        }
        
        if (advice != null) {
            children.add(advice);
        }
        
        children.addAll(statements);

        return Collections.unmodifiableList(children);
    }
}