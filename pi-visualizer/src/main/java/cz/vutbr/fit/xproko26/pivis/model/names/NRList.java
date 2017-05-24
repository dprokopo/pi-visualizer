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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class which represents list of name references (see {@link NameRef NameRef})
 * @author Dagmar Prokopova
 */
public class NRList extends ArrayList<NameRef> {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Default empty constructor;
     */
    public NRList() {
    }
    
    /**
     * Constructor which creates new list out of existing list passed as parameter.
     * @param list original list
     */
    public NRList(List list) {
        super(list);
    }

    /**
     * Returns simple unformated string as textual representation of the name list
     * in which names are separated with commas.
     * @return string
     */
    @Override
    public String toString() {
        return this.stream().map(n -> n.toString()).collect(Collectors.joining(","));
    }
        
    /**
     * Returns simple unformated string as textual representation of the name list
     * for debuging purposes.
     * @return string
     */
    public String toStringDebug() {
        return this.stream().map(n -> n.toStringDebug()).collect(Collectors.joining(","));
    }
    
    /**
     * Returns copy of the name list.
     * @return copy of the list
     */          
    public NRList copy() {
        NRList copy = new NRList();
        this.forEach((n) -> {
            copy.add(n.copy());
        });
        return copy;
    }
    
    /**
     * Returns true if every name references in the list has the same source
     * as the name reference on the same index of the list passed as an argument.
     * @param list list passed for comparison
     * @return true if name references on the same index have the same source
     */
    public boolean srcequals(NRList list) {
        if (this.size() != list.size()) {
            return false;
        }
        else {
            for (int i=0; i < this.size(); i++) {
                if (!this.get(i).srcequals(list.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
