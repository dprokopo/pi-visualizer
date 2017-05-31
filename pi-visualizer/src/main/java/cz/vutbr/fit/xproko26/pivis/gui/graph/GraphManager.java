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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import cz.vutbr.fit.xproko26.pivis.gui.AudioPlayer;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.jgraphx.GraphJGraphX;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphLib;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphListener;
//import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.yfiles.GraphYFiles; /***YFILES***/

import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.visualizer.NodeValueModifier;
import cz.vutbr.fit.xproko26.pivis.visualizer.VisualListener;
import cz.vutbr.fit.xproko26.pivis.visualizer.Visualizer;

/**
 * GraphManager is a singleton class which maintains the graphical representation
 * of pi-calculus expression. It represents a bridge between a graph library
 * of any kind which is able to 'draws' graphic objects onto the graph canvas and 
 * the core of the application represented by model, controller and application gui.
 * It not only provides methods to send commands to graph library (i.e. to create
 * node/edge, or to execute automatic graph layout), but it also implements
 * methods of graph library listener to collect information about user interaction
 * with the graph. In order to visualize expression it cooperates with {@link 
 * Visualizer Visualizer} class.
 * @author Dagmar Prokopova
 */
public class GraphManager {
            
    //singleton instance of GraphManager class
    private static GraphManager instance;
    
    //reference to graph manager listener
    private static GraphManagerListener listener;
    
    //reference to graphic library
    private static GraphLib glib;
    
    //flag indicating hierarchic graph expand style
    private static boolean hierarchic;
    
    //list of node values set to expansion
    private static List<NodeValue> expandNodes;
    
    //selected node value
    private static NodeValue selection;
    
    //graphic table to map NodeValue to model object or graphic object
    private static GraphicTable graphictable;
    
    
    /**
     * Constructor which creates graph library.
     */
    private GraphManager() {
        
        //create graphical library        
        glib = (GraphLib) new GraphJGraphX();
        //glib = (GraphLib) new GraphYFiles();	/***YFILES***/
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of AppGUI class
     */
    public static GraphManager getInstance() {
        if(instance == null) {
            instance = new GraphManager();
        }
        return instance;
    }
    
    /**
     * Invokes graph library method to clear graph canvas.
     */
    public void init() {
        glib.clear();
    }
    
    /**
     * Returns graph canvas component.
     * @return graph canvas
     */
    public JComponent getGraphCanvas() {
        return glib.getGraphComponent();
    }
    
    /**
     * Adds graph manager listener and sets graph library listener and visualizer
     * listener.
     * @param list listener to be added
     */
    public void addListener(GraphManagerListener list) {
        listener = list;
        
        setGraphListener();
        setVisualizerListener();
    }
            
    /**
     * Initializes local variables and invokes visualizer method to traverse
     * expression. This will eventually result in expression being visualized
     * and layouted with the help of graph library.
     * @param exp expression to be visualized
     * @param style true if hierarchic style should be applied, false otherwise
     * @param animation true if layout of the graph should be animated
     */
    public void drawGraph(Expression exp, boolean style, boolean animation) {
        if (exp == null) {
            return;
        }

        Lock.getInstance().set(0, () -> {
            hierarchic = style;        
            glib.clear();
            expandNodes = new ArrayList<>();
            selection = null;
            graphictable = new GraphicTable();
            Visualizer.getInstance().visualize(exp, hierarchic);
            expand(expandNodes);
        }, () -> layout(animation));
    }
    
    /**
     * Invokes NodeValueModifier method to traverse specified expression
     * while removing reduction-selection and suggestion mark from each
     * encountered node value.
     * @param exp root of the expression to be treaveresd
     */
    public void removeRedSelection(Expression exp) {
        NodeValueModifier.getInstance().traverse(exp, (NodeValue nv) -> {
            if (nv.isReductionSelected()) {                     
                setReductionSelected(nv, false);
            }
            if (nv.isSuggested()) {
                setSuggested(nv,false);
            }
        });
    }
    
    /**
     * Makrs every expression from the specified list as selected for reduction.
     * @param exps list of expressions to be marked
     */
    public void visualizeRedSelection(List<Expression> exps) {
        for (Expression e : exps) {
            if (e.getVisual() != null) {
                setReductionSelected(e.getVisual(), true);
            }
        }
    }
    
    /**
     * Marks every expression from the specified list as suggested for reduction.
     * @param exps list of expressions to be marked
     */
    public void visualizeSuggestions(List<Expression> exps) {
        for (Expression e : exps) {
            if (e.getVisual() != null) {
                setSuggested(e.getVisual(), true);
            }
        }
    }
    
    /**
     * Checks whether the parallel replication expression should be visualized
     * and does so if needed. After that invokes method to visualize the replication
     * branch prefixed with helper expression.
     * @param helper helper expression to be visualized
     */
    public void visualizeHelper(Expression helper) {
        
        Lock.getInstance().set(0, () -> {
            
            ParallelReplicationExpression par = (ParallelReplicationExpression) helper.getParent();
            //create parallel node if not visible
            NodeValue nv = par.getVisual();
            if ((nv == null) || (graphictable.getGraphic(nv.getID()) == null)) {
                createParallelReplication(par, par.getRepOriginal());
            }
            //visualize branch behind helper
            createReplicationBranch(par, (ReplicationExpression) helper);
            
            if (listener != null) {
                listener.helperVisualized(helper);
            }

        }, () -> layout(true));
    }
    
    /**
     * Makes all expressions from the specified list visible which means
     * it expands all ancestor nodes and visualizes ancestor helper branches.
     * @param elist list of expressions to be visualized
     */
    public void prepareForRedSelection(List<Expression> elist) {
        
        Lock.getInstance().set(1, () -> {
            
            for (Expression exp : elist) {
                Stack<Expression> stack = exp.getParentStack();
                while (!stack.empty()) {
                    Expression prevexp = stack.pop();
                    if (prevexp.isReplicationHelper()) { //if it is helper, it needs to be visualized
                        visualizeHelper(prevexp);
                    } else if (prevexp instanceof ConcretizeExpression) {
                        NodeValue nv = prevexp.getVisual();
                        if (nv.isCollapsed()) { //if it is collapsed, it needs to be expanded
                            expand(nv);
                        }
                    }
                }
            }
        }, () -> layout(true));
    }
    
    /**
     * Marks specified node as selected.
     * @param nv nodevalue to be marked as selected
     */
    public void visualizeSelection(NodeValue nv) {

        if (!nv.isSelected()) {
            setSelected(nv, true);
            for (EdgeValue ev : getEdges(nv)) {
                setSelected(ev, true);
            }
        }
        selection = nv;
    }
    
    /**
     * Clears the selection.
     */
    public void clearSelection() {
        
        if (selection != null) {
            setSelected(selection, false);
            for (EdgeValue ev : getEdges(selection)) {
                setSelected(ev, false);
            }
        }        
        selection = null;
    }

     
    /* -------------- EXPAND/COLLAPSE ----------------- */
        
    /**
     * Expands the group node specified by node value passed as an agrument.
     * @param group node to be expanded
     */
    private void makeExpanded(NodeValue group) {

        group.setCollapsed(false);
        NodeValue.Type type = group.getType();
        
        if (type == NodeValue.Type.V_HGROUP) {
            if (!isVisualized(group)) {
                try {
                    visualizeInstance(group);
                } catch (Exception ex) {
                    return;
                }
            }
            else {
                showChildren(group);
            }
            
            //hide edges connecting to group
            getEdges(group).forEach((edge) -> {    
                setVisible(edge, false);                
                //shold hide name too?
                hideUnusedName(getTarget(edge));
                
            });
        }
        else if (type == NodeValue.Type.V_LGROUP) {
            if (!isVisualized(group)) {
                try {
                    visualizeInstance(group);
                } catch (Exception ex) {
                    return;
                }
            }
            else {
                setSuccesorsVisible(group, true);
            }
        }               
    }

    /**
     * Collapses the group node specified by node value passed as an agrument.
     * @param group node to be collapsed
     */
    private void makeCollapsed(NodeValue group) {

        group.setCollapsed(true);
        NodeValue.Type type = group.getType();
        
        if (type == NodeValue.Type.V_HGROUP) {
            
            hideChildren(group);            
            
            //show edges connecting to group
            getEdges(group).forEach((edge) -> {
                //test if name needs to be visualized first
                showUsedName(getTarget(edge));
                setVisible(edge, true);                
            });
        }
        else if (type == NodeValue.Type.V_LGROUP) { 
            setSuccesorsVisible(group, false);
        }        
    }    
            
    /**
     * Checks whether the name specified by node value passed as an agrument
     * has any visible edges. If here is none, hides the name itself.
     * @param nv node value which should be set hidden
     */
    private void hideUnusedName(NodeValue nv) {
        if (nv.isName()) {
            boolean hide = true;
            for (EdgeValue edge : getEdges(nv)) {
                if (edge.isVisible()) {
                    hide = false;
                    break;
                }
            }
            if (hide) {
                setVisible(nv, false);
            }
        }
    }
    
    /**
     * Visualizes the name specified by node value passed as an argument
     * @param nv node value which should be set as visible
     */
    private void showUsedName(NodeValue nv) {
        if (nv.isName() && !nv.isVisible()) {
            setVisible(nv, true);
        }
    }        
    
    /**
     * Sets all children nodes of the specified group node as visible.
     * @param group group node whose children should be made visible
     */
    private void showChildren(NodeValue group) {
        for (NodeValue child : getChildren(group)) {

            if (child.getType() == NodeValue.Type.V_HGROUP && !child.isCollapsed()) {
                showChildren(child);
            } else {
                //display all edges
                for (EdgeValue edge : getEdges(child)) {
                    //do not visualize edges connecting to expanded groups
                    if (getSource(edge).isCollapsed() && getTarget(edge).isCollapsed()) {                        
                        //test if name needs to be visualized first
                        showUsedName(getTarget(edge));                        
                        setVisible(edge, true);                        
                    }
                }
            }
            //display the node itself
            if (glib.supportsFolding()) { //in case the glib supports folding, the node does not need to be made visible explicitly
                child.setVisible(true);
            } else {
                setVisible(child, true);
            }
            hideUnusedName(child);
        }
    }
        
    /**
     * Sets all children nodes of the specified group node as hidden.
     * @param group group node whose children should be made hidden
     */
    private void hideChildren(NodeValue group) {
        for (NodeValue child : getChildren(group)) {

            //if expanded HGROUP -> hide all inside
            if (child.getType() == NodeValue.Type.V_HGROUP && !child.isCollapsed()) {
                hideChildren(child);
            }
            //hide all child edges
            getEdges(child).forEach((edge) -> {
                setVisible(edge, false);                
                //shold hide name too?
                hideUnusedName(getTarget(edge));                
            });
            
            //hide the node itself
            if (glib.supportsFolding()) { //in case the glib supports folding, the node does not have to be hidden explicitly
                child.setVisible(false); 
            } else {
                setVisible(child, false);
            }
        }
    }
    
    /**
     * Sets all successors of the specified group node as hidden or visible
     * according to the value of second argument passed.
     * @param group group node whose successors should be made visible or hidden
     * @param visible true if successors should be made visible, false otherwise
     */
    private void setSuccesorsVisible(NodeValue group, boolean visible) {
        for (EdgeValue edge : getEdges(group)) {
            NodeValue source = getSource(edge);
            NodeValue target = getTarget(edge);
            //the first outgoing edge connecting next node, not name
            if (group.equals(source) && (!target.isName())) {
                setVisible(edge, visible);
                if (visible)
                    show(target);
                else
                    hide(target);
            }
        }
    }
    
    /**
     * Recursively sets all successor nodes and their edges as visible.
     * @param node currently processed node value
     */
    private void show(NodeValue node) {
        setVisible(node, true);
        
        for (EdgeValue edge : getEdges(node)) {
            NodeValue target = getTarget(edge);
            
            if (!edge.isVisible()) {
                if (node.equals(target)) {
                    return;
                } else {
                    boolean stop = (node.getType() == NodeValue.Type.V_LGROUP) && (node.isCollapsed());
                    if (!stop || target.isName()) { //stop if group is collapsed or target is name
                        setVisible(edge, true);
                        show(target);
                    }
                }
            }
        }
    }    
    
    /**
     * Recursively sets all successor nodes and their edges as hidden.
     * @param node currently processed node value
     */
    private void hide(NodeValue node) {
        
        for (EdgeValue edge : getEdges(node)) {
            NodeValue target = getTarget(edge);
            
            if (edge.isVisible()) {
                if (node.equals(target)) {
                    return;
                } else {
                    setVisible(edge, false);
                    hide(target);
                }
            }
        }
        setVisible(node, false);
    }                    
    
    /**
     * Returns true if specified node value is visualized group node.
     * @param nv node value to be examined
     * @return true if node was visualized
     */
    private boolean isVisualized(NodeValue nv) {
        
        if (nv.getType() == NodeValue.Type.V_HGROUP) {
            //group in hierarchic style is visualized if it has at least one child node
            if (getChildren(nv).size() > 0) {
                return true;
            }
        }
        else if (nv.getType() == NodeValue.Type.V_LGROUP) {
            //group in linear style is visualized of ther is at least one outgoing edge which does not end in name
            for (EdgeValue edge : getEdges(nv)) {
                NodeValue source = getSource(edge);
                NodeValue target = getTarget(edge);
                if (nv.equals(source) && !target.isName()) {
                    return true;                    
                }
            }
        }        
        return false;
    }
        
    /**
     * Requests instance and invokes visualizer method to visualize it.
     * @param nv node value which instance should be visualized
     * @throws Exception throws exception if specified concretization expression 
     * (process) was not defined
     */
    private void visualizeInstance(NodeValue nv) throws Exception {

        Expression instexp = null;
        if (listener != null) {
            instexp = listener.instanceRequested((Expression) getObject(nv));
        }
        
        if (instexp == null) { //this means that process definition has been changed
            throw new Exception("Error: Missing process definition.");
        }
        
        expandNodes.clear();
        
        if (hierarchic) {            
            NodeValue previous = null;
            for (EdgeValue edge : getEdges(nv)) {
                if (nv.equals(getTarget(edge))) {
                    previous = getSource(edge);
                    break;
                }
            }
            Visualizer.getInstance().visualizeBranch(instexp, nv, previous); 
        } else {
            Visualizer.getInstance().visualizeBranch(instexp, null, nv);
        }

        expand(expandNodes);
        
        if (listener != null) {
            listener.instanceVisualized(instexp);
        }
        
    }    
    
    
    /* ------------------ REPLICATION -------------------- */
    
    
    /**
     * Creates parallel replication node and connects it in between existing
     * nodes.
     * @param node parallel replication expression
     * @param rep neighbour replication node
     */    
    private void createParallelReplication(ParallelReplicationExpression node, Expression rep) {
        
        //remove old edge
        NodeValue succ = rep.getVisual();
        NodeValue prev = null;
        for (EdgeValue ev : getEdges(succ)) {            
            if (succ.equals(getTarget(ev))) {
                prev = getSource(ev);
                remove(ev);
                break;
            }
        }
        
        //add new node with new edges
        Visualizer.getInstance().createParRepNode(node, getParent(succ), prev, succ);

    }
    
    /**
     * Invokes visualizer method to visualize replication branch.
     * @param node preceding parallel replication expression node
     * @param rexp root of the expression subtree which should be visualized
     */
    private void createReplicationBranch(Expression node, ReplicationExpression rexp) {               
        Visualizer.getInstance().visualizeBranch(rexp.getSuccExp(), (hierarchic) ? getParent(node.getVisual()) : null, node.getVisual());
    }
    
    
    /* ------------------ USER INTERACTION --------------------- */
    
    /**
     * Invokes listener method to find out whether node is selectable for reduction.
     * @param nv node to be examined
     * @return true if node is reducible
     */
    private boolean isSelectableForReduction(NodeValue nv) {
        if (nv.getType() == NodeValue.Type.V_NODE) {
            if (listener != null) {
                return listener.isSelectableForReduction((Expression) getObject(nv));
            }
        }
        return false;
    }
    
    /**
     * Creates array of menu items which should be displayed in a context menu
     * upon right mouse button click on specified node.
     * @param nv node value for which the context menu is created
     * @return array of menu items
     */
    private JMenuItem[] createGraphMenuItems(NodeValue nv) {

        if (isSelectableForReduction(nv)) {

            JMenuItem item = new JMenuItem();
            if (nv.isReductionSelected()) {
                item.setText("Deselect");
                item.addActionListener(e -> {
                    if (listener != null) {
                        listener.nodeDeselectedFromReduction((Expression) getObject(nv));
                    }
                    AudioPlayer.getInstance().coin();
                });

            } else {
                item.setText("Select for reduction");
                item.addActionListener(e -> {
                    if (listener != null) {
                        listener.nodeSelectedForReduction((Expression) getObject(nv));
                    }
                    AudioPlayer.getInstance().coin();
                });
            }

            return new JMenuItem[]{item};
        }
        else if (nv.isReplicable()) {
            
            JMenuItem item = new JMenuItem();
            item.setText("Replicate");
            item.addActionListener(e -> {
                if (listener != null) {
                    Expression helper = listener.replicationRequested((Expression) getObject(nv));
                    if (helper != null) {
                        visualizeHelper(helper);
                    }
                }
            });
            return new JMenuItem[]{item};
        }
        
        return null;
    }       
    
    
    /* -------------------- NodeValue to GRAPHLIB TRANSLATORS --------------------- */
    
    /**
     * Requests creation of new node and returns its graphic object.
     * @param parent parent node value
     * @param node created node value
     * @return graphic object of created node
     */
    private Object createNode(NodeValue parent, NodeValue node) {
        return glib.createNode((parent == null) ? null : getGraphic(parent), node);
    }
    
    /**
     * Requests creation of new edge and returns its graphic object.
     * @param parent parent node value
     * @param n1 node value of source node
     * @param n2 node value of target node
     * @param edge edge value of created edge
     * @return graphic object of created edge
     */
    private Object createEdge(NodeValue parent, NodeValue n1, NodeValue n2, EdgeValue edge) {
        return glib.createEdge((parent == null) ? null : getGraphic(parent), getGraphic(n1), getGraphic(n2), edge);
    }

    /**
     * Returns list of edge values which are connected to specified node.
     * @param nv node value of the node which edges are searched
     * @return list of edge values
     */
    private List<EdgeValue> getEdges(NodeValue nv) {
        List<Object> edges = glib.getEdges(getGraphic(nv));        
        return edges.stream().map(e -> (EdgeValue) glib.getValue(e)).collect(Collectors.toList());
    }

    /**
     * Returns list of child node values of specified node.
     * @param nv node value of the node which children are searched
     * @return list of children node values
     */
    private List<NodeValue> getChildren(NodeValue nv) {
        List<Object> nodes = glib.getChildren(getGraphic(nv));
        return nodes.stream().map(n -> (NodeValue) glib.getValue(n)).collect(Collectors.toList());
    }

    /**
     * Returns node value of source node of specified edge
     * @param ev edge value of examined edge
     * @return node value of source node
     */
    private NodeValue getSource(EdgeValue ev) {
        return (NodeValue) glib.getValue(glib.getSource(getGraphic(ev)));
    }

    /**
     * Returns node value of target node of specified edge
     * @param ev edge value of examined edge
     * @return node value of target node
     */
    private NodeValue getTarget(EdgeValue ev) {
        return (NodeValue) glib.getValue(glib.getTarget(getGraphic(ev)));
    }
    
    /**
     * Returns node value of parent node.
     * @param nv node value of examined node
     * @return node value of parent node
     */
    private NodeValue getParent(NodeValue nv) {
        return (NodeValue) glib.getValue(glib.getParent(getGraphic(nv)));
    }

    /**
     * Requests graph library to expand specified group node.
     * @param nv node value of the node which shall be expanded
     */
    private void expand(NodeValue nv) {
        glib.expand(new Object[]{getGraphic(nv)});
    }
    
    /**
     * Requests graph library to expand specified group nodes.
     * @param nlist list of group nodes to be expanded
     */
    private void expand(List<NodeValue> nlist) {
        glib.expand(nlist.stream().map(node -> getGraphic(node)).toArray());
    }
    
    /**
     * Marks specified object as visible or hidden based on the second argument.
     * @param cv cell value of specified object.
     * @param b true if object should be made visible
     */
    private void collapse(NodeValue nv) {
        glib.collapse(new Object[]{getGraphic(nv)});   
    }
    
    /**
     * Marks specified object as visible or hidden based on the second argument.
     * @param cv cell value of specified object.
     * @param b true if object should be made visible
     */
    private void setVisible(CellValue cv, boolean b) {
        cv.setVisible(b);
        glib.setVisible(getGraphic(cv), b);
    }

    /**
     * Marks specified node as reduction selected if second argument is true,
     * otherwise removes the flag.
     * @param cv node value of specified node.
     * @param b true if node should be decorated as selected for reduction
     */
    private void setReductionSelected(NodeValue cv, boolean b) {
        cv.setReductionSelected(b);
        Object graphic = getGraphic(cv);
        if (graphic != null) {
            glib.setReductionSelected(graphic, b);
        }
    }
    
    /**
     * Marks specified node as reduction suggested if second argument is true,
     * otherwise removes the flag.
     * @param cv node value of specified node.
     * @param b true if node should be decorated as suggested for reduction
     */
    private void setSuggested(NodeValue cv, boolean b) {
        cv.setSuggested(b);
        Object graphic = getGraphic(cv);
        if (graphic != null) {
            glib.setSuggested(graphic, b);
        }
    }
    
    /**
     * Marks specified object as selected or deselected based on second argument.
     * @param cv cell value of specified object.
     * @param b true if object should be selected
     */
    private void setSelected(CellValue cv, boolean b) {
        cv.setSelected(b);
        Object graphic = getGraphic(cv);
        if (graphic != null) {
            glib.setSelected(graphic, b);
        }
    }

    /**
     * Requests graph library to remove specified object.
     * @param cv cell value of the object which shall be removed
     */
    private void remove(CellValue cv) {
        glib.remove(getGraphic(cv));
    }
    
    /**
     * Requests graph library to execute graph layout.
     * @param effect true if the layout should be animated and with audio effect
     */
    private void layout(boolean effect) {
        if (effect) {
            AudioPlayer.getInstance().woosh();
        }
        glib.executeLayout(effect);
    }
    
    /**
     * Returns cell value for specified graphic object.
     * @param o graphic object
     * @return cell value
     */
    private CellValue getValue(Object o) {
        return glib.getValue(o);
    }
    
    /**
     * Returns list of export actions which are provided by the specific graph library.
     * @return list of export actions
     */
    public List<ExportAction> getExportFormats() {
        return glib.getExportFormats();
    }
    
    /* ---------- GRAPHIC TABLE ------------ */
    
    /**
     * Returns graphic object for the specified cell value
     * @param cv cell value which graphic object should be returned
     * @return graphic object
     */
    private Object getGraphic(CellValue cv) {
        return graphictable.getGraphic(cv.getID());
    }
    
    /**
     * Returns model object for the specified cell value
     * @param cv cell value which model object should be returned
     * @return model object
     */
    private Object getObject(CellValue cv) {
        return graphictable.getObject(cv.getID());
    }

    /* ------------ GRAPH LISTENER ----------- */
    
    /**
     * Creates and sets graph library listener to collect
     * information about user interaction with graph in order to be able
     * to react to such interactions by invoking appropriate methods.
     */
    private void setGraphListener() {
        glib.addListener(new GraphListener() {
            
            @Override
            public void nodeExpanded(Object o) {
                boolean userclick = Lock.getInstance().set(0, () -> {
                    makeExpanded((NodeValue) getValue(o));            
                }, () -> layout(true));
                if (userclick) {
                    clearSelection();
                    visualizeSelection((NodeValue)glib.getValue(o));
                }
                if (listener != null) {
                    listener.nodeExpanded();
                }
            }
            
            @Override
            public void nodeCollapsed(Object o) {
                boolean userclick = Lock.getInstance().set(0, () -> {
                    makeCollapsed((NodeValue) getValue(o));            
                }, () -> layout(true));
                if (userclick) {
                    clearSelection();
                    visualizeSelection((NodeValue)glib.getValue(o));
                }
                if (listener != null) {
                    listener.nodeCollapsed();
                }
            }

            @Override
            public JMenuItem[] getPopupMenuItems(Object o) {
                CellValue cv = getValue(o);
                if (cv instanceof NodeValue) {
                    return createGraphMenuItems((NodeValue)cv);
                }
                else {
                    return null;
                }
            }            

            @Override
            public void nodeClicked(Object o) {        
                NodeValue nv = (NodeValue) getValue(o);               
                if (listener != null) {
                    if (nv.isSelected()) {
                        clearSelection();
                    }
                    else {
                        clearSelection();
                        visualizeSelection(nv);
                    }
                }

                if (listener != null) {
                    listener.selectionChanged();
                }
            }

            @Override
            public void canvasClicked() {
                clearSelection();
                if (listener != null) {
                    listener.selectionChanged();
                }
            }
        });
    }
    

    /* ------------ VISUALIZER LISTENER ------------- */
    
    /**
     * Creates and sets visualizer listener to get information when node or edge 
     * is created and to process visualizer requests.
     */
    private void setVisualizerListener() {
        Visualizer.getInstance().addListener(new VisualListener() {

            @Override
            public void createdNode(NodeValue parent, NodeValue node, Object o) {
                
                //set visible by default
                node.setVisible(true); 
                
                //create graphical node
                Object gnode = createNode(parent, node);
                
                //save graphic into table
                graphictable.set(node.getID(), o, gnode);   
                
                //if selected before, mark as selected
                if (node.isReductionSelected())
                    setReductionSelected(node, true);
                
                //if suggested before, mark as selected
                if (node.isSuggested())
                    setSuggested(node, true);
                
                //if used to be expanded, add into list for later expansion
                if (!node.isCollapsed())
                    expandNodes.add(node);
                
                if (node.isSelected()) {
                    setSelected(node, true);
                    selection = node;
                }
                
                //every group should be collapsed first
                if (node.isExpandable())
                    collapse(node);                                                
            }

            @Override
            public void createdEdge(NodeValue parent, NodeValue n1, NodeValue n2, EdgeValue edge) {
                
                //set visible by default
                edge.setVisible(true);  
                
                //create graphical edge
                Object gedge = createEdge(parent, n1, n2, edge);
                
                //save graphic into table
                graphictable.set(edge.getID(), null, gedge);                
                
                if (n1.isSelected() || n2.isSelected()) {
                    setSelected(edge, true);
                }
            }
            
            @Override
            public void reusedNode(NodeValue node) {
                //set visible by default
                setVisible(node, true);
            }

            @Override
            public boolean isProcDefined(String id, NRList args) {
                if (listener != null) {
                    return listener.isProcDefined(id, args);
                }
                return false;
            }
            
            @Override
            public boolean isVisualized(NodeValue nv) {
                return (graphictable.getGraphic(nv.getID()) != null);
            }
        });
    }   
}
