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

import java.util.ArrayList;


/**
 * NameTable represents the storage of all names used in the visualized expression
 * and process definitions in the current model context. The index of the name
 * value in this list uniquely identifies the name. This index is used by
 * {@link NameRef NameRef} class as the unique identifier.
 * @author Dagmar Prokopova
 */
public class NameTable extends ArrayList<NameValue>{
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates new record in the table in which the passed name value is stored.
     * Also returns newly created name reference which points to this value.
     * @param nv name value to be added
     * @return name reference
     */
    public NameRef createName(NameValue nv) {
        add(nv);
        NameRef nr = new NameRef(size()-1);
        if (nv.isPrivate()) {
            nr.setPrivate();
        }
        return nr;
    }
    
    /**
     * Creates the new record in the table to which the copy of the existing
     * name value is stored. The name value to be replicated is specified by
     * name reference passed as an argument. Also the name reference pointing
     * to the newly created record is returned.
     * @param ref name reference to the original name which shall be replicated
     * @return name reference of newly created copy
     */
    public NameRef replicateName(NameRef ref) {
        add(get(ref.getRef()).copy());
        NameRef nr = new NameRef(size()-1);
        if (ref.isPrivate()) {
            nr.setPrivate();
        }
        nr.setSource(ref.getSource());
        return nr;
    }
    
    /**
     * Returns the name value for specified name reference.
     * @param n name reference
     * @return name value
     */
    public NameValue getNameValue(NameRef n) {
        return get(n.getRef());
    }
    
    /**
     * Returns newly created name reference for name value passed as an argument
     * if there is a name value stored in the table which matches the passed value..
     * If there is no such name value, null is returned instead.
     * @param nv name value
     * @return name reference
     */
    public NameRef getNameRef(NameValue nv) {
        for(int i = 0; i < size(); i++) {
            NameValue n = get(i);              
            if (n.getLabel().equals(nv.getLabel())) {
                return new NameRef(i);
            }
        }
        return null;
    } 
    
    
}
