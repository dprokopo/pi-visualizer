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
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * MapTable can be used to map one name reference to another,
 * for example when substitution of names is performed. MapTable holds the
 * unique identifiers of names which shall be remapped as well as the new
 * name references they should be remapped to.
 * @author Dagmar Prokopova
 */
public class MapTable extends HashMap<Integer, NameRef> {
    
    /**
     * Adds name reference pair containing the old name reference as well as
     * the new name reference it should be remapped to.
     * @param oldref old name reference
     * @param newref new name reference
     */
    public void add(NameRef oldref, NameRef newref) {
        put(oldref.getRef(), newref);
    }
    
    /**
     * Adds two lists of name references, the first one containing the old name
     * references and the second containing the new coresponding name references.
     * @param oldrefs list of old name references
     * @param newrefs list of new name references
     */
    public void add(NRList oldrefs, NRList newrefs) {
        for (int i=0; i < oldrefs.size(); i++) {
            add(oldrefs.get(i), newrefs.get(i));
        }
    }
    
    /**
     * Applies remapping process on the specified name reference. If there is
     * a record for the passed reference in the table, the name reference is
     * remapped, otherwise nothing changes.
     * @param ref name reference designated for remapping
     */
    public void remap(NameRef ref) {
        NameRef newref = get(ref.getRef());
        if (newref != null) {
            ref.setRef(newref.getRef());
            ref.setSource(newref.getSource());
            if (newref.isPrivate()) {
                ref.setPrivate();
            }
        }
    }
    
    /**
     * Applies remapping process for each name reference in the list passed 
     * as an argument.
     * @param list list of name references designated for remapping
     */
    public void remap(NRList list) {
        list.forEach((ref) -> remap(ref));
    }
    
    /**
     * Returns the list of all new name references which are stored in the table.
     * @return 
     */
    public NRList getValues() {
        return new NRList(values().stream().collect(Collectors.toList()));
    }

}
