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

import org.jsmiparser.util.token.IdToken;

public class SmiOidMacro extends SmiOidValue {

    protected StatusAll m_status;
    private String m_description;

    public SmiOidMacro(IdToken idToken, SmiModule module) {
        this(idToken, module, null);
    }

    public SmiOidMacro(IdToken idToken, SmiModule module, String description) {
        super(idToken, module);
        m_description = description;
    }

    public StatusAll getStatus() {
        return m_status;
    }

    public StatusV1 getStatusV1() {
        return m_status.getStatusV1();
    }

    public StatusV2 getStatusV2() {
        return m_status.getStatusV2();
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }
}
