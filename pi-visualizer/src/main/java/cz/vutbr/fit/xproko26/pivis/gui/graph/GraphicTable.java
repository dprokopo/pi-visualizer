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

import java.util.HashMap;

/**
 * GraphicTable is a two-way mapping structure used by {@link GraphManager 
 * GraphManager} to map {@link NodeValue NodeValue} to either graphic object 
 * of any kind or to model object (expression or name value). Since every 
 * model object and every graphic object keeps reference to its NodeValue,
 * it is (thanks to GraphicTable) possible for any of them to get the opposite.
 * @author Dagmar Prokopova
 */
public class GraphicTable {
    
    //hashmap for Nodevalue id --> model object mapping
    HashMap<Integer, Object> objects;
    
    //hashmapp for NodeValue id --> graphic object mapping
    HashMap<Integer, Object> graphics;

    /**
     * Constructor initializes both mapping structures.
     */    
    public GraphicTable() {
        graphics = new HashMap<>();
        objects = new HashMap<>();
    }
    
    /**
     * Returns mode object (expression or name value) for specified NodeValue id.
     * @param i id of the NodeValue.
     * @return model object
     */
    public Object getObject(int i) {
        return objects.get(i);
    }
    
    /**
     * Returns graphic object (product of graph library) for specified NodeValue id.
     * @param i id of the NodeValue
     * @return graphic object
     */
    public Object getGraphic(int i) {
        return graphics.get(i);
    }
    
    /**
     * Adds new record into the GraphTable.
     * @param i id of the NodeValue
     * @param obj model object
     * @param gr graphic object
     */
    public void set(int i, Object obj, Object gr) {
        objects.put(i, obj);
        graphics.put(i, gr);
    }
}
