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
package cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib;

import java.util.List;
import javax.swing.JComponent;
import cz.vutbr.fit.xproko26.pivis.gui.graph.CellValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.EdgeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;

/**
 * GraphLib is an interface which specifies methods that need to be implemented 
 * by any graph library used for pi-calculus graphic visualization.
 * @author Dagmar Prokopova
 */
public interface GraphLib {
    
    /**
     * Adds graph listener to report user interaction with graph.
     * @param glist listener to be added
     */
    public void addListener(GraphListener glist);

    /**
     * Returns true if graph library supports folding. (i.e. all children nodes 
     * are automatically hidden upon collapsing the group and made visible
     * upon expansion)
     * node.
     * @return true if folding is supported
     */
    public boolean supportsFolding();
    
    /**
     * Returns graph canvas component.
     * @return graph canvas
     */
    public JComponent getGraphComponent();    
    
    /**
     * Executes graph layout with or without animation effect based on the 
     * specified argument.
     * @param animation true if animation is allowed
     */
    public void executeLayout(boolean animation);
    
    /**
     * Clears all graphic object from the canvas.
     */
    public void clear();
     
    /**
     * Creates new graph node inside of the specified parent and stores its value.
     * @param parent parent of the node
     * @param value value of the node to be saved
     * @return created node
     */
    public Object createNode(Object parent, NodeValue value);
    
    /**
     * Creates new edge inside of the specified parent and stores its value.
     * @param parent parent of the edge
     * @param source source node of the edge
     * @param target target node of the edge
     * @param value value of the edge to be saved
     * @return created edge
     */
    public Object createEdge(Object parent, Object source, Object target, EdgeValue value);
    
    /**
     * Collapses specified nodes.
     * @param nodes an array of nodes to be collapsed
     */
    public void collapse(Object[] nodes);
    
    /**
     * Expands specified nodes.
     * @param nodes an array of nodes to be expanded
     */
    public void expand(Object[] nodes);

    /**
     * Returns list of all children of specified group node.
     * @param group group node
     * @return list of children nodes
     */
    public List<Object> getChildren(Object group);
    
    /**
     * Returns list of edges connected to specified node
     * @param node node which edges are searched
     * @return list of edges
     */
    public List<Object> getEdges(Object node);
    
    /**
     * Returns target node of the specified edge.
     * @param edge edge which target should be returned
     * @return target node
     */
    public Object getTarget(Object edge);
    
    /**
     * Returns source node of the specified edge.
     * @param edge ede which source should be returned.
     * @return source node
     */
    public Object getSource(Object edge);
    
    /**
     * Returns parent node of the specified node.
     * @param node node which parent should be returned
     * @return parent node
     */
    public Object getParent(Object node);
    
    /**
     * Returns stored CellValue of specified object.
     * @param cell graphic object
     * @return cell value
     */
    public CellValue getValue(Object cell);
               
    /**
     * Sets object as visible or invisible.
     * @param o object which state should be changed
     * @param b true if object should be made visible
     */
    public void setVisible(Object o, boolean b);
    
    /**
     * Sets object as selected for reduction
     * @param o object which state should be changed
     * @param b true if object should be made selected for reduction
     */
    public void setReductionSelected(Object o, boolean b);
    
    /**
     * Sets object as suggested for reduction
     * @param o object which state should be changed
     * @param b true if object should be made suggested for reduction
     */
    public void setSuggested(Object o, boolean b);
    
    /**
     * Sets object as selected/highlighted
     * @param o object which state should be changed
     * @param b true if object should be selected/highlighted
     */
    public void setSelected(Object o, boolean b);
    
    /**
     * Removes specified object from graph canvas.
     * @param o object to be removed
     */    
    public void remove(Object o);
    
    /**
     * Returns list of available export actions.
     * @return list of export actions
     */
    public List<ExportAction> getExportFormats();
}
