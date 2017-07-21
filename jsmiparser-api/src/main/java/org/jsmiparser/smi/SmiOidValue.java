/*
 * Copyright 2005 Davy Verstappen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsmiparser.smi;

import org.jsmiparser.phase.xref.XRefFallbackResolver;
import org.jsmiparser.phase.xref.XRefProblemReporter;
import org.jsmiparser.util.token.IdToken;

public class SmiOidValue extends SmiValue {

    private OidComponent m_lastOidComponent;

    private SmiOidNode m_node;

    public SmiOidValue(IdToken idToken, SmiModule module) {
        super(idToken, module);
    }

    public SmiOidValue(IdToken idToken, SmiModule internalModule, SmiOidNode node) {
        super(idToken, internalModule);
        m_node = node;
    }

    public OidComponent getLastOidComponent() {
        return m_lastOidComponent;
    }

    public void setLastOidComponent(OidComponent lastOidComponent) {
        m_lastOidComponent = lastOidComponent;
    }

    public int[] getOid() {
        return m_node.getOid();
    }

    /**
     * @return null for the root node; the OID in decimal dotted notation for all other nodes
     */
    public String getOidStr() {
        if (m_node == null) {
            throw new NullPointerException("OidNode was not resolved for " + getIdToken());
        }
        return m_node.getOidStr();
    }


    private int recurseCount = 0;
    public SmiOidNode resolveOid(XRefProblemReporter reporter, XRefFallbackResolver resolver) {
        if (recurseCount > 0) {
            reporter.reportCycle(getIdToken());
            return null;
        }

        recurseCount++;
        try {

            return doResolveOid(reporter, resolver);
        }
        finally {
            recurseCount--;
        }
    }

    private SmiOidNode doResolveOid(XRefProblemReporter reporter, XRefFallbackResolver resolver) {
        if (m_node == null) {
            m_node = m_lastOidComponent.resolveNode(getModule(), reporter, resolver);
            if (m_node != null) {
                m_node.getValues().add(this);
            }
            // assumption is that another error has already been reported for this
        }
        return m_node;
    }

    public SmiOidNode getNode() {
        return m_node;
    }

    public String getCodeId() {
        return getModule().getMib().getCodeNamingStrategy().getOidValueId(this);
    }

}