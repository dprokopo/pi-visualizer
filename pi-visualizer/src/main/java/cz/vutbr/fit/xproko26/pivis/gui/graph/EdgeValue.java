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
package cz.vutbr.fit.xproko26.pivis.gui.graph;

/**
 * EdgeValue is a class which stores visual information about
 * the graph edge.
 * It extends {@link CellValue CellValue} class about the type variable which
 * should be reflected by any graph library to create appropriate edge type.
 * @author Dagmar Prokopova
 */
public class EdgeValue extends CellValue {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * All edge type options.
     */
    public enum Type {
        E_FLOW, E_PARAM, E_IN, E_OUT
    }
    
    //edge type
    private EdgeValue.Type type;
    
    /**
     * Creates EdgeValue of specified label and type.
     * @param l label
     * @param t type
     */
    public EdgeValue(String l, EdgeValue.Type t) {
        super(l);        
        type = t;
    }
    
    /**
     * Sets specified edge type.
     * @param t type
     */
    public void setType(EdgeValue.Type t) {
        type = t;
    }
    
    /**
     * Returns edge type.
     * @return type
     */
    public EdgeValue.Type getType() {
        return type;
    }
}
