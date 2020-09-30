/***************************************************************************
 * Copyright (C) 2016 iObserve Project (https://www.iobserve-devops.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package org.iobserve.analysis.userbehavior.data;

import java.util.Objects;

/**
 * States the end of a call sequence.
 *
 * @author David Peter, Robert Heinrich, Nicolas Boltz
 */
public class ExitElement extends ISequenceElement {

    private int absoluteCount;

    /**
     * default constructor.
     */
    public ExitElement() {

    }

    @Override
    public int getAbsoluteCount() {
        return this.absoluteCount;
    }

    @Override
    public void setAbsoluteCount(final int absoluteCount) {
        this.absoluteCount = absoluteCount;
    }
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof ExitElement) {
    		return true;
    	}
    	return false;
    }
    
    @Override
    public int hashCode() {
    	return Objects.hash(this.getClass());
    }
}
