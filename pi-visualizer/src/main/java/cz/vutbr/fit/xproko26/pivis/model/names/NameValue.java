/* 
 * Copyright 2017 Dagmar Prokopova <xproko26@stud.fit.vutbr.cz>.
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
package cz.vutbr.fit.xproko26.pivis.model.names;

import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;

/**
 * Class which represents name value. Compared to {@link NameRef NameRef}) class,
 * NameValue contains the label which is the string representation of the name 
 * identifier.
 * @author Dagmar Prokopova
 */
public class NameValue extends Name {
    
    private static final long serialVersionUID = 1L;
    
    //textual representation of name
    private final String label;
    
    //flag indicating if the name was substitued for unique identifier
    private boolean substitued;
    
    //reference to object aggregating visual information about the name
    private NodeValue visual;
    
    /**
     * Constructor which initializes the name label.
     * @param n label of the name
     */
    public NameValue(String n) {
        label = n;        
    }

    /**
     * Returns label of the name
     * @return label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Returns true if the name was substitued.
     * @return substitution flag
     */
    public boolean isSubstitued() {
        return substitued;
    }
    
    /**
     * Sets substitution flag to true.
     */
    public void setSubstitued() {
        substitued = true;
    }
    
    /**
     * Sets object containing visual information about the name.
     * @param nv visual object
     */
    public void setVisual(NodeValue nv) {
        visual = nv;
    }
    
    /**
     * Returns object containing visual information about the name.
     * @return visual object
     */
    public NodeValue getVisual() {
        return visual;
    }
    
    /**
     * Compares two name values and returns true if their labels are equal.
     * It does not mean however that they represent the same pi-calculus name.
     * @param val name value passed for comparision
     * @return true if labels are equal
     */
    public boolean equals(NameValue val) {
        return (val.getLabel().equals(label));
    }
         
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toString() {
        return label;
    }
    
    /**
     * {@inheritDoc}
     */      
    @Override
    public String toStringDebug() {
        return label;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    protected NameValue copy() {
        NameValue copy = new NameValue(label);
        if (isPrivate()) {
            copy.setPrivate();
        }
        return copy;
    }
}
