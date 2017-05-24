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
package cz.vutbr.fit.xproko26.pivis.visualizer;

import cz.vutbr.fit.xproko26.pivis.gui.graph.EdgeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;

/**
 * Interface containing methods invoked by Visualizer to inform graph manager
 * that visual object of node or edge was created and so that corresponding 
 * graphical node or edge can be created. Some of the methods are used to get 
 * information about the model internal representation or graphical representation.
 * @author Dagmar Prokopova
 */
public interface VisualListener {
 
    /**
     * Informs about creation of the visual object for the specific node.
     * @param parent visual object of parent
     * @param node visual object of the node
     * @param o expression node or name value
     */
    public void createdNode(NodeValue parent, NodeValue node, Object o);
    
    /**
     * Reports that the specified visual object was reused.
     * @param node reused visual object
     */
    public void reusedNode(NodeValue node);
    
    /**
     * Informs about creation of the visual object for the specific edge.
     * @param parent visual object of parent
     * @param n1 visual object of the source node
     * @param n2 visual object of the target node
     * @param edge visual object of the edge
     */
    public void createdEdge(NodeValue parent, NodeValue n1, NodeValue n2, EdgeValue edge);
    
    /**
     * Returns true if the specified process with the specified number of arguments
     * was already defined.
     * @param id process identifier.
     * @param args number of process arguments
     * @return true if process is defined
     */
    public boolean isProcDefined(String id, int args);
    
    /**
     * Returns true if there was a graphic object already created for specified 
     * visual object.
     * @param nv checked visual object
     * @return true if graphic object exists
     */
    public boolean isVisualized(NodeValue nv);
    
}
