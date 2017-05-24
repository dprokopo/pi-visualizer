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

import java.io.Serializable;

/**
 * CellValue is an abstract class which stores visual information about
 * the graph node or edge. It is shared by model expression (or name value)
 * as well as by graphic object created by any type of graph library.
 * It contains static idcounter variable which ensures, that every created 
 * CellValue will have an unique identifier. Apart from the id, it stores
 * also the label which should be visualized in the graphic object and set of
 * flags which represent the curent state of the graphic object like its visibility
 * or selection.
 * @author Dagmar Prokopova
 */
public abstract class CellValue implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //id counter as static class variable
    private static int idcounter = 0;
    
    //unique identifier
    private final int id;

    //label of the graph node or edge
    private String label;
    
    //visibility flag
    private boolean visible;
    
    //flag indicating selection
    private boolean selected;
    
    /**
     * Creates CellValue of the specified label with unique id and
     * flags set to default values - visibility: true, selection: false.
     * @param l 
     */
    public CellValue(String l) {
        id = idcounter++;
        label = l;
        visible = true;
        selected = false;
    }

    /**
     * Returns unique identifier of the cell value.
     * @return numeric id
     */
    public int getID() {
        return id;
    }

    /**
     * Returns label of the cell value.
     * @return string label
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets specified label of the cell value.
     * @param l label to be set
     */
    public void setLabel(String l) {
        label = l;
    }

    /**
     * Sets value of visibility flag.
     * @param b boolean value to be set
     */
    public void setVisible(boolean b) {
        visible = b;
    }
    
    /**
     * Sets value of selection flag.
     * @param b boolean value to be set
     */
    public void setSelected(boolean b) {
        selected = b;
    }    
    
    /**
     * Returns value of visibility flag, which is true if object is visible.
     * @return visibility flag
     */
    public boolean isVisible() {
        return visible;
    }    
    
    /**
     * Returns value of selection flag, which is true if object is selected.
     * @return selection flag
     */
    public boolean isSelected() {
        return selected;
    }
    
    /**
     * Returns textual representation of the cell value - the label.
     * @return label
     */
    @Override
    public String toString() {
        return label;
    }
    
    /**
     * Compares two cell values and returns true if their IDs are equal.
     * @param cv cell value passed for comparison
     * @return true if cell values represent the same object
     */
    public boolean equals(CellValue cv) {
        return (id == cv.getID());
    }
}
