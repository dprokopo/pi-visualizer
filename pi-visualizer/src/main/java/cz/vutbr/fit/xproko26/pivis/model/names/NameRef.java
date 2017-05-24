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

import cz.vutbr.fit.xproko26.pivis.model.Model;

/**
 * Class which represents name reference. Compared to {@link NameValue NameValue}) 
 * class, NameRef does not contain label of the name but instead contains numeric 
 * index which uniquely identifies the pi-calculus name.
 * @author Dagmar Prokopova
 */
public class NameRef extends Name {
    
    private static final long serialVersionUID = 1L;
    
    //unique name identifier
    private Integer ref;
    
    //unique name identifier of the name source
    private Integer source;

    /**
     * Constructor which initializes the name reference and the source of the name
     * with the specified name identifier.
     * @param i unique name identifier
     */
    public NameRef(int i) {
        ref = i;
        source = i;
    }
    
    /**
     * Returns the unique identifier of the name.
     * @return unique identifier
     */
    public int getRef() {
        return ref;
    }
    
    /**
     * Sets the unique identifier of the name
     * @param r unique identifier
     */
    public void setRef(int r) {
        ref = r;
    }
    
    /**
     * Returns the unique identifier of the original source name.
     * For example if name was copied the source points to the original name.
     * @return unique identifier of the source
     */
    public int getSource() {
        return source;
    }
    
    /**
     * Sets the unique identifier of the source name.
     * @param src unique identifier of the source
     */
    public void setSource(int src) {
        source = src;
    }

    /**
     * Compares two name references and returns true if their identifiers are equal.
     * It means that they represent exactly the same pi-calculus name.
     * @param r name reference passed for comparison
     * @return true if identifiers are equal
     */
    public boolean equals(NameRef r) {
        return (r.getRef() == ref);
    }

    /**
     * Compares two name references and returns true if their labels are equal.
     * It does not mean however that they represent exactly the same pi-calculus name.
     * @param r name reference passed for comparison
     * @return true if labels are equal
     */
    public boolean labelEquals(NameRef r) {
        NameValue nv1 = getNameValue();
        NameValue nv2 = Model.getInstance().getNameTable().getNameValue(r);
        String label1 = nv1.getLabel();
        String label2 = nv2.getLabel();
        
        return ((!nv1.isSubstitued()) && (!nv2.isSubstitued()) && (label1.equals(label2)));
    }
    
    /**
     * Compares two name references and returns true if their sources are equal.
     * It does not mean however that they represent exactly the same pi-calculus name.
     * @param r name reference passed for comparison
     * @return true if sources identifiers are equal
     */
    public boolean srcequals(NameRef r) {
        return (r.getSource() == source);
    }
    
    /**
     * Substitutes name for unique name which is not used yet.
     */
    public void substitute() {
        getNameValue().setSubstitued();
    }

    /**
     * Returns corresponding name value. 
     * @return name value
     */
    public NameValue getNameValue() {
        return Model.getInstance().getNameTable().getNameValue(this);
    }

    /**
     * {@inheritDoc}
     */ 
    @Override
    public String toString() {
        NameValue nv = getNameValue();
        String ret = nv.getLabel();
        if (nv.isSubstitued()) {
            ret += "#" + ref;
        }
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */     
    @Override
    public String toStringDebug() {
        return "ref_" + ref.toString();
    }
    
    /**
     * {@inheritDoc}
     */     
    @Override
    public NameRef copy() {
        NameRef copy = new NameRef(ref);
        copy.setSource(getSource());
        if (isPrivate()) {
            copy.setPrivate();
        }
        return copy;
    }
}
