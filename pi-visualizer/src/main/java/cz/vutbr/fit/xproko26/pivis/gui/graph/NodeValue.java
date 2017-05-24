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
 * NodeValue is a class which stores visual information about
 * the graph node. It is shared by model expression (or name value)
 * as well as by graphic object created by any type of graph library.
 * It extends {@link CellValue CellValue} class about the type variable which
 * should be reflected by any graph library to create appropriate node type
 * and about additional flags which indicate whether the node is collapsed, 
 * selected for reduction or suggested for reduction selection.
 * @author Dagmar Prokopova
 */
public class NodeValue extends CellValue {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * All node type options.
     */
    public enum Type {
        V_NODE,     //classic structural node representing pi-calculus operation
        V_NAME,     //node representing pi-calculus name
        V_PRIVNAME, //node representing pi-calculus restricted name
        V_LGROUP,   //node representing pi-calculus concretion for linear layout
        V_HGROUP,   //node representing pi-calculus concretion for hierarchical layout
        V_NOGROUP   //node representing undefined pi-calculus process
    }
    
    //node type
    private Type type;

    //node flags
    private boolean collapsed;
    private boolean redselected;    
    private boolean suggested;
    
    /**
     * Creates NodeValue of specified label and type and flags set to default
     * values - collapsed: true, selected for reduction: false, suggested for 
     * reduction: false.
     * @param l label
     * @param t type
     */
    public NodeValue(String l, Type t) {
        super(l);        
        type = t;       
        collapsed = true;
        redselected = false;
        suggested = false;
    }

    /**
     * Sets specified node type.
     * @param t node type
     */
    public void setType(Type t) {
        type = t;
    }
    
    /**
     * Returns type of the node.
     * @return 
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets value of collapsed flag.
     * @param b boolean value to be set
     */
    public void setCollapsed(boolean b) {
        collapsed = b;
    }
    
    /**
     * Returns value of collapsed flag, which is true if node is collapsed.
     * @return collapsed flag
     */
    public boolean isCollapsed() {
        return collapsed;
    }
    
    /**
     * Sets value of reduction-selected flag
     * @param b boolean value to be set
     */
    public void setReductionSelected(boolean b) {
        redselected = b;
    }
    
    /**
     * Returns value of reduction-selected flag, which is true if node is selected
     * for reduction.
     * @return reduction-selected flag
     */
    public boolean isReductionSelected() {
        return redselected;
    }

    /**
     * Sets value of suggested flag
     * @param b boolean value to be set
     */
    public void setSuggested(boolean b) {
        suggested = b;
    }
    
    /**
     * Returns value of suggested flag, which is true if node is suggested 
     * for reduction selection.
     * @return suggested flag
     */
    public boolean isSuggested() {
        return suggested;
    }
    
    /**
     * Returns true if node is expandable, which means that the corresponding
     * process is defined.
     * @return true if node can be expanded
     */
    public boolean isExpandable() {
        return ((type == Type.V_HGROUP) || (type == Type.V_LGROUP));
    }
    
    /**
     * Returns true if node is replicable, which means it represents replication
     * node
     * @return true if node can be replicated 
     */
    public boolean isReplicable() {
        return ((type == Type.V_NODE) && getLabel().equals("!"));
    }
    
    /**
     * Returns true if node represents pi-calculus name.
     * @return true if node represens name
     */
    public boolean isName() {
        return ((type == Type.V_NAME ) || (type == Type.V_PRIVNAME));
    }
    
}
