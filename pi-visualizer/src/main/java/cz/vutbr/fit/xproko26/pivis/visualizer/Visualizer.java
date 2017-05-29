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

import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.EdgeValue;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.MatchExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionVisitor;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;
import cz.vutbr.fit.xproko26.pivis.model.expressions.NilExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RestrictionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SumExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;

/**
 * Visualizer is a singleton class which provides auxiliary methods to convert 
 * an expression tree into its visual graph representation. Visualizer traverses the
 * expression and creates coresponding visual objects (NodeValue or EdgeValue) 
 * containing visual information based on the recently visited expression nodes. 
 * It also invokes appropriate VisualizerListener methods to initialize
 * creation of node or edge by graphic library..
 * @author Dagmar Prokopova
 */
public class Visualizer extends ExpressionVisitor<NodeValue> {
    
    //singleton instance of Visualizer
    private static Visualizer instance;
    
    //reference to visualizer listener
    private static VisualListener listener;
    
    //flag which indicates whether the nodes should be ordered hierarchicaly
    private static boolean hierarchic;
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of Visualizer class
     */
    public static Visualizer getInstance() {
        if(instance == null) {
            instance = new Visualizer();
        }
        return instance;
    }
    
    /**
     * Adds VisualListener.
     * @param list listener to be added
     */
    public void addListener(VisualListener list) {
        listener = list;
    }

    /**
     * Initializes traversal of the expression tree which root is passed 
     * as an argument. The second argument specifies ordering style of the nodes.
     * @param exp expression to be traversed
     * @param h true if nodes should be ordered hierarchically
     */
    public void visualize(Expression exp, boolean h) {
        hierarchic = h;
        visit(exp, null);
    }
    
    /**
     * Traverses expression tree from the expression node passed as the first
     * argument and creates visual objects for that subtree.
     * @param node node of the expression tree from which the traversal is started
     * @param parent visual object of parent node of the first created expression
     * @param previous visual object of predcessor node of the first created expression
     */
    public void visualizeBranch(Expression node, NodeValue parent, NodeValue previous) {
        NodeValue nv = visit(node, parent);
        if (previous != null) {
            createEdge(null, previous, nv, new EdgeValue("", EdgeValue.Type.E_FLOW));
        }
    }

    /**
     * Creates visual object for paralel replication expression and invokes
     * appropriate methods to connect it into the graph.
     * @param exp parallel replication expression
     * @param parent visual object of parent node
     * @param prev visual object of predcessor node
     * @param succ visual object of successor node
     */
    public void createParRepNode(Expression exp, NodeValue parent, NodeValue prev, NodeValue succ) {
        
        NodeValue nv = new NodeValue("|", NodeValue.Type.V_NODE);
        createNode(exp, parent, nv);
        if (prev != null) {
            //edge from previous expression to new one
            createEdge(null, prev, nv, new EdgeValue("", EdgeValue.Type.E_FLOW));
        }
        //edge from new node to following node
        createEdge(null, nv, succ, new EdgeValue("", EdgeValue.Type.E_FLOW));        
    }
    
    @Override
    public NodeValue visit(RootExpression node, NodeValue parent) {
        //ignore expression - no visual representation
        return visit(node.getSuccExp(), parent);
    }        
    
    @Override
    public NodeValue visit(RestrictionExpression node, NodeValue parent) {
        //ignore expression - no visual representation
        return visit(node.getSuccExp(), parent);
    }

    @Override
    public NodeValue visit(SumExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("+", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);
        
        //visit all successors and connect created visual object with edges 
        node.getSuccExps().stream().map((e) -> visit(e, parent)).forEachOrdered((nv2) -> {
            createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
        });               
        return nv1;
    }

    @Override
    public NodeValue visit(ParallelExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("|", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);

        //visit all successors and connect created visual object with edges
        node.getSuccExps().stream().map((e) -> visit(e, parent)).forEachOrdered((nv2) -> {
            createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
        });
        return nv1;
    }
    
    
    @Override
    public NodeValue visit(ParallelReplicationExpression node, NodeValue parent) {
        //check if parallel replication expression should be visualized
        if (node.isVisible()) {
            
            //extract visual object from the expression or create new
            NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("|", NodeValue.Type.V_NODE);
            createNode(node, parent, nv1);
            
            //visit all successors (except for replication helpers) and connect created visual object with edges
            for (Expression succex : node.getSuccExps()) {
                if (!succex.isReplicationHelper()) {
                    NodeValue nv2 = visit(succex, parent);
                    createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
                }
            }
            return nv1;
        } else {
            //if it should not be visualized, then it must contain only original replication branch
            return visit(node.getRepOriginal(), parent);
        }
    }
    

    @Override
    public NodeValue visit(ReplicationExpression node, NodeValue parent) {        
        if (node.isReplicationCopy()) {
            //replication copy node is not visualized
            return visit(node.getSuccExp(), parent);
        }
        else {
            //extract visual object from the expression or create new
            NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("!", NodeValue.Type.V_NODE);
            createNode(node, parent, nv1);
        
            //visit successor and connect created visual object with edge
            NodeValue nv2 = visit(node.getSuccExp(), parent);
            createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
            
            return nv1;
        }
    }
    
    @Override
    public NodeValue visit(InPrefixExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("i", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);

        //process channel name
        NodeValue channel = createName(parent, node.getChannel());
        createEdge(parent, nv1, channel, new EdgeValue("", EdgeValue.Type.E_IN));
        
        //process parameters
        node.getParams().stream().map((ref) -> createName(parent, ref)).forEachOrdered((name) -> {
            createEdge(parent, nv1, name, new EdgeValue("", EdgeValue.Type.E_PARAM));
        });
        
        //visit successor and connect created visual object with edge
        NodeValue nv2 = visit(node.getSuccExp(), parent);
        createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
        return nv1;
    }

    @Override
    public NodeValue visit(OutPrefixExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("o", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);

        //process channel name
        NodeValue channel = createName(parent, node.getChannel());
        createEdge(parent, nv1, channel, new EdgeValue("", EdgeValue.Type.E_OUT));
        
        //process parameters
        node.getParams().stream().map((ref) -> createName(parent, ref)).forEachOrdered((name) -> {
            createEdge(parent, nv1, name, new EdgeValue("", EdgeValue.Type.E_PARAM));
        });
        
        //visit successor and connect created visual object with edge
        NodeValue nv2 = visit(node.getSuccExp(), parent);
        createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
        return nv1;
    }

    @Override
    public NodeValue visit(TauPrefixExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("t", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);
        
        //visit successor and connect created visual object with edge
        NodeValue nv2 = visit(node.getSuccExp(), parent);
        createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW));
        return nv1;
    }

    @Override
    public NodeValue visit(MatchExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("=", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);

        //process left name
        NodeValue name1 = createName(parent, node.getLeft());
        createEdge(parent, nv1, name1, new EdgeValue("", EdgeValue.Type.E_PARAM)); 
        
        //process right name
        NodeValue name2 = createName(parent, node.getRight());
        createEdge(parent, nv1, name2, new EdgeValue("", EdgeValue.Type.E_PARAM)); 
        
        //visit successor and connect created visual object with edge
        NodeValue nv2 = visit(node.getSuccExp(), parent);
        createEdge(parent, nv1, nv2, new EdgeValue("", EdgeValue.Type.E_FLOW)); 
        return nv1;
    }

    @Override
    public NodeValue visit(ConcretizeExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue(node.getIDRef().toString(), NodeValue.Type.V_PROC);
        nv1.setLabel(node.getIDRef().getNameValue().getLabel()); //update label
        
        if (node.isReduced()) {
            //enhance label of the process identifier if reduced flag is on
            nv1.setLabel("*" + node.getIDRef().toString());
        }

        if (node.getIDRef().isDefProcess() && (listener != null)) {
            //check if process is defined and set visual node type accordingly
            if (listener.isProcDefined(node.getIDRef().toString(), node.getArgs())) {
                nv1.setType(hierarchic ? NodeValue.Type.V_HGROUP : NodeValue.Type.V_LGROUP);
            } else {
                if (node.getSuccExp() == null) {
                    nv1.setType(NodeValue.Type.V_NOGROUP);
                }
            }
        }
        createNode(node, parent, nv1);
            
        //connect arguments
        for (NameRef nref : node.getArgs()) {
            NodeValue name = createName(parent, nref);
            createEdge(parent, nv1, name, new EdgeValue("", EdgeValue.Type.E_PARAM)); 
        }
      
        return nv1;
    }

    @Override
    public NodeValue visit(NilExpression node, NodeValue parent) {
        //extract visual object from the expression or create new
        NodeValue nv1 = (node.getVisual() != null) ? node.getVisual() : new NodeValue("0", NodeValue.Type.V_NODE);
        createNode(node, parent, nv1);        
        return nv1;
    }
    
    @Override
    public NodeValue visit(AbstractionExpression node, NodeValue parent) {
        //ignore expression - no visual representation
        return visit(node.getSuccExp(), parent);
    }        

    /**
     * Invokes listener method to create node and stores the visual
     * object into the expression node.
     * @param node expression node
     * @param parent visual object of parent
     * @param nv visual object of processed node
     */
    private void createNode(Expression node, NodeValue parent, NodeValue nv) {        
        if (listener != null) {            
            listener.createdNode(parent, nv, node);
        }
        node.setVisual(nv);
    }    
    
    /**
     * Invokes listener method to create edge.
     * @param parent visual object of parent node
     * @param n1 visual object of source node
     * @param n2 visual object of target node
     * @param edge visual object of edge created
     */
    private void createEdge(NodeValue parent, NodeValue n1, NodeValue n2, EdgeValue edge) {        
        if (listener != null) {
            listener.createdEdge(parent, n1, n2, edge);
        }
    }
    
    /**
     * Checks whether the specified name reference was already visualized and
     * invokes listener method either to create new node or to reuse already
     * created one.
     * @param parent visual object of parent node
     * @param ref name reference
     * @return visual objec for the speficied name reference
     */
    private NodeValue createName(NodeValue parent, NameRef ref) {
        
        NodeValue par = parent;
        
        //extract visual object from the name value or create new
        NodeValue nameval = ref.getNameValue().getVisual();
        if (nameval == null) {
            NodeValue.Type type = NodeValue.Type.V_NAME;
            if (ref.isPrivate()) {
                type = NodeValue.Type.V_PRIVNAME;
            }
            else if (ref.isDefProcess()) {
                type = NodeValue.Type.V_PROCNAME;
            }
            nameval = new NodeValue(ref.getNameValue().getLabel(), type);
        }

        if (ref.isDefProcess()) {
            par = null;
        }
        
        if (listener != null && listener.isVisualized(nameval)) {
            //name node was already visualized - just inform listener that the node should be reused
            listener.reusedNode(nameval);  
        }
        else {
            //save the visual object and inform listener that new node should be created
            ref.getNameValue().setVisual(nameval);
            if (listener != null) {
                listener.createdNode(par, nameval, ref.getNameValue());
            }            
        }
        return nameval;
    }
}
