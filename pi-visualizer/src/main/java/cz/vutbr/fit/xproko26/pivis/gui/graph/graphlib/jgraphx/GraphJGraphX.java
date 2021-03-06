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
package cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import cz.vutbr.fit.xproko26.pivis.gui.graph.CellValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.EdgeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphLib;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphListener;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.jgraphx.layout.mxHierarchicalLayout;

/**
 * GraphJGraphX represents the class which connects JGraphX library to graph manager.
 * It provides methods for graph manipulation.
 * @author Dagmar Prokopova
 */
public class GraphJGraphX implements GraphLib {

    //graph listener for reporting user interaction
    private GraphListener glist;
    
    //graph canvas
    private final mxGraphComponent gcomp;
    
    //complete graph representation
    private mxGraph graph;  
    
    /**
     * Initializes graph and graph canvas, prepares visual styles and sets graph
     * to uneditable viewer mode.
     */
    public GraphJGraphX() {
        
        //create graph
        graph = new mxGraph() {
            @Override
            public boolean isCellFoldable(Object cell, boolean collapse) {
                mxCellState state = view.getState(cell);
                Map<String, Object> style = (state != null) ? state.getStyle() : getCellStyle(cell);
                return mxUtils.isTrue(style, mxConstants.STYLE_FOLDABLE, true);
            }                        
        };
                
        //enable folding of group nodes
        graph.addListener(mxEvent.CELLS_FOLDED, new mxEventSource.mxIEventListener() {
            @Override
            public void invoke(Object o, mxEventObject eo) {
                Object[] array = (Object[]) eo.getProperty("cells");
                for (Object n : array) {
                    mxCell cell = (mxCell) n;
                    if (cell.isCollapsed()) {
                        //report that node was collapsed
                        glist.nodeCollapsed(n);                                                
                    } else {                 
                        //report that node was expanded
                        glist.nodeExpanded(n);                        
                    }
                }
            }            
        });
        
        //configure graph
        configureGraph();
        
        //create graph component containing the created graph
        gcomp = new mxGraphComponent(graph)
        {
            @Override
            public boolean isPanningEvent(MouseEvent e) {
                //enable panning if not clicked on item
                if (gcomp.getCellAt(e.getX(), e.getY()) != null) {
                    return false;
                }
                return true;
            }
        };
        //configure graph component               
        configureGraphComponent();
        
        //set stylesheets
        setStyles();
        
        //set user interaction
        setUserInteraction();
    }
    
    /**
     * Configures graph to allow only basic interaction, but not structure modification.
     */
    private void configureGraph() {
        
        graph.setCellsEditable(false);          //disable label editing
        graph.setCellsCloneable(false);         //disable cloning of the cells with CTRL
        graph.setCellsDisconnectable(false);    //disable disconnecting edges
        graph.setCellsResizable(false);         //disable resizing of the cells
        graph.setDropEnabled(false);            //disable adding nodes into groups
        
        graph.setAutoSizeCells(true);
        graph.setConstrainChildren(true);
        graph.setDefaultOverlap(0);              
        graph.setCollapseToPreferredSize(true);        
    }
    
    /**
     * Configures graph component to allow only basic interaction, but not structure modification.
     */
    private void configureGraphComponent() {
        
        gcomp.setConnectable(false);
        gcomp.setDragEnabled(false);           
        gcomp.getGraphHandler().setRemoveCellsFromParent(false);        
        gcomp.setPanning(true);
        
        gcomp.getViewport().setOpaque(true);
        gcomp.getViewport().setBackground(Color.WHITE);  
        
        mxSwingConstants.EDGE_SELECTION_COLOR = new Color(0,0,0,0);
        mxSwingConstants.VERTEX_SELECTION_COLOR = new Color(0,0,0,0);
    }
    
    /**
     * Initializes stylesheets that define look of the nodes and edges.
     */
    private void setStyles() {
        
        //style edges - default
        Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
        style.put(mxConstants.STYLE_ROUNDED, true); 
                
        //style edges - flow
        style = new HashMap<>();
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        graph.getStylesheet().putCellStyle("flow", style);
        
        
        //style edges - in
        style = new HashMap<>();
        style.put(mxConstants.STYLE_STROKECOLOR, "#777777");
        style.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_BLOCK);
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_OVAL);
        graph.getStylesheet().putCellStyle("in", style);
        
        //style edges - out
        style = new HashMap<>();
        style.put(mxConstants.STYLE_STROKECOLOR, "#777777");
        style.put(mxConstants.STYLE_STARTARROW, mxConstants.ARROW_OVAL);
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_BLOCK);
        graph.getStylesheet().putCellStyle("out", style);
        
        //style edges - params
        style = new HashMap<>();
        style.put(mxConstants.STYLE_STROKECOLOR, "#555555");
        style.put(mxConstants.STYLE_DASHED, true);
        style.put(mxConstants.STYLE_DASH_PATTERN, 3);
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        graph.getStylesheet().putCellStyle("param", style);

        
        //style vertices - default
        style = graph.getStylesheet().getDefaultVertexStyle();
        style.put(mxConstants.STYLE_FILLCOLOR, "#ffffff");
        style.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        style.put(mxConstants.STYLE_FONTSIZE, 16);        
        style.put(mxConstants.STYLE_SPACING_TOP, 5);
        style.put(mxConstants.STYLE_SPACING_LEFT, 6);
        style.put(mxConstants.STYLE_SPACING_RIGHT, 6);
        style.put(mxConstants.STYLE_SPACING_BOTTOM, 3);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
        style.put(mxConstants.STYLE_FOLDABLE, false);
		
        //style vertices - flow node
        style = new HashMap<>();
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);		        
        graph.getStylesheet().putCellStyle("node", style);

        //style vertices - names
        style = new HashMap<>();
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTSIZE, 17);
	style.put(mxConstants.STYLE_FILLCOLOR, "#dddddd");        
        graph.getStylesheet().putCellStyle("name", style);
        
        //style vertices - private names
        style = new HashMap<>();
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTSIZE, 17);
	style.put(mxConstants.STYLE_FILLCOLOR, "#777777");
        style.put(mxConstants.STYLE_FONTCOLOR, "#FFFFFF");
        graph.getStylesheet().putCellStyle("privname", style);
        
        //style vertices - process name
        style = new HashMap<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTSIZE, 17);
	style.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        graph.getStylesheet().putCellStyle("procname", style);
               
        //style vertices - group
        style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTSIZE, 18);
        style.put(mxConstants.STYLE_SPACING_LEFT, 9);
        style.put(mxConstants.STYLE_SPACING_RIGHT, 9);
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
        style.put(mxConstants.STYLE_SHADOW, true);
        style.put(mxConstants.STYLE_FOLDABLE, true);
        graph.getStylesheet().putCellStyle("group", style);
        
        //style vertices - nogroup
        style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FONTSIZE, 18);
        style.put(mxConstants.STYLE_SPACING_LEFT, 9);
        style.put(mxConstants.STYLE_SPACING_RIGHT, 9);
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
        style.put(mxConstants.STYLE_SHADOW, true);
        style.put(mxConstants.STYLE_FOLDABLE, false);
        graph.getStylesheet().putCellStyle("nogroup", style);
        
        //style vertices - proc
        style = new HashMap<>();
        style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_ITALIC);
        style.put(mxConstants.STYLE_FILLCOLOR, "#eeeeee"); 
        style.put(mxConstants.STYLE_FONTSIZE, 18);
        style.put(mxConstants.STYLE_SPACING_LEFT, 9);
        style.put(mxConstants.STYLE_SPACING_RIGHT, 9);
	style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
        style.put(mxConstants.STYLE_SHADOW, true);
        style.put(mxConstants.STYLE_FOLDABLE, false);
        graph.getStylesheet().putCellStyle("proc", style);        
    }
    
    /**
     * Configures basic user interaction by setting several listeners.
     */
    private void setUserInteraction() {

        //add mouse listener to report click on item or canvas
        gcomp.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {                
                if (e.getButton() == 1) {
                    mxCell cell = (mxCell) gcomp.getCellAt(e.getX(), e.getY());
                    if (cell == null) {
                        if (glist != null) {
                            //graph canvas was clicked
                            glist.canvasClicked();
                        }
                    } else {
                        if (cell.isVertex()) {
                            if (glist != null) {
                                //node was clicked
                                glist.nodeClicked(cell);
                            }
                        }
                    }
                }
                else {
                    // handles context menu on the Mac where the trigger is on mousepressed
                    mouseReleased(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    //create popup menu
                    showGraphPopupMenu(e);
                }
            }
        });
        
        //add mouse wheel listener to zoom in or out
        gcomp.getGraphControl().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if (e.getWheelRotation() < 0) {
                    gcomp.zoomIn();
                } else {
                    gcomp.zoomOut();
                }
            }
        });

    }
    
    /**
     * Create popup menu for specified right mouse click event
     * @param e right mouse click event
     */
    private void showGraphPopupMenu(MouseEvent e) {        
        Object cell = gcomp.getCellAt(e.getX(), e.getY());
        if (cell != null) {
            //check if clicked item is node
            if (graph.getModel().isVertex(cell)) {
                //obtain menu items from graph manager
                JMenuItem[] items = glist.getPopupMenuItems(cell);
                if (items != null && items.length > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    for (JMenuItem item : items) {
                        menu.add(item);
                    }
                    //transfer coordinates
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), gcomp);
                    menu.show(gcomp, pt.x, pt.y);
                }            
            }
        }
        e.consume();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(GraphListener glist) {
        this.glist = glist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getGraphComponent() {
        return gcomp;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void executeLayout(boolean animation) {
                      
        graph.getModel().beginUpdate();
        try {
            //important for final centering
            graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(0, 0, 0, 0));        
        } finally {
            graph.getModel().endUpdate();            
        }      

        //layout all groups separately
        for (Object o : getChildren(graph.getDefaultParent())) {
            mxICell child = (mxICell) o;
            if (child.isVertex() && graph.isCellFoldable(child, true) && !child.isCollapsed()) {
                layoutChildren(child);
            }
        }

        //execute layout for the whole graph
        graph.getModel().beginUpdate();
        try {
            graphLayout(graph).execute(graph.getDefaultParent());
        } finally {
            //center the graph in the graph canvas
            center();
            graph.getModel().endUpdate();
        }                
    }
    
    /**
     * Configures hierarchical layout.
     * @param graph graph to layout
     * @return hierarchical layout
     */
    private mxHierarchicalLayout graphLayout(mxGraph graph) {
        
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
        layout.setOrientation(SwingConstants.WEST);
        layout.setResizeParent(true);
        layout.setMoveParent(true);
        layout.setLayoutFromSinks(false);
        layout.setParentBorder(20);
        layout.setInterRankCellSpacing(75);
        return layout;
        
    }
    
    /**
     * Executes layout for elements in a single group node. Recursively calls itself
     * for all group nodes found as children.
     * @param group group node to be positioned
     */
    private void layoutChildren(mxICell group) {
        
        for (Object o : getChildren(group)) {
            mxICell child = (mxICell) o;
            if (child.isVertex() && graph.isCellFoldable(child, true) && !child.isCollapsed()) {
                layoutChildren(child);
            }
        }
        
        graph.getModel().beginUpdate();
        try {
            graphLayout(graph).execute(group, getGroupRoot(group));
        } finally {
            graph.getModel().endUpdate();
        }
    }
    
    /**
     * Searches for the root node of the group.
     * @param group group for which the root should be found
     * @return array of roots
     */
    private Object[] getGroupRoot(mxICell group) {
                
        mxIGraphModel model = graph.getModel();
        for (int i = 0; i < group.getChildCount(); i++) {
            mxCell cell = (mxCell) model.getChildAt(group, i);
            if (model.isVertex(cell)) {
                List<Object> edges = getEdges(cell);
                boolean rootcandidate = true;
                for (Object edge : edges) {
                    mxCell source = (mxCell) getSource(edge);
                    mxCell target = (mxCell) getTarget(edge);
                    
                    //check if the source node was in one of the previous groups
                    mxICell groupparent = group.getParent();
                    while (groupparent != null) {
                        if (source.getParent().getId().equals(groupparent.getId())) {
                            return new Object[] { cell };                        
                        }
                        groupparent = groupparent.getParent();
                    }
                    if (target.getId().equals(cell.getId())) {
                        rootcandidate = false;
                    }
                }
                if (rootcandidate) {
                    return new Object[] {cell};
                }
            }
        }
        return null;
    }
    
    /**
     * Places the layouted graph into the center of the graph canvas.
     */
    public void center() {                  

        graph.refresh();

        double compwidth = gcomp.getSize().getWidth();
        double compheight = gcomp.getSize().getHeight();

        double graphwidth = graph.getGraphBounds().getWidth();
        double graphheight = graph.getGraphBounds().getHeight();

        double widthdiff = compwidth - graphwidth;
        double heightdiff = compheight - graphheight;
        
        double widthoffset = 30; //minimum padding
        double heightoffset = 30; //minimum padding
        
        double w = 0;
        double h = 0;
        
        if (widthdiff > 0) {
            widthoffset = widthdiff/2;
        } else {    
            w = widthoffset;
        }
        if (heightdiff > 0) {
            heightoffset = heightdiff/2;
        } else {
            h = heightoffset;
        }
        
        mxGeometry g = graph.getModel().getGeometry(graph.getDefaultParent());
        graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(widthoffset+g.getX(), heightoffset+g.getY(), g.getWidth(), g.getHeight()));
        graph.setMinimumGraphSize(new mxRectangle(0,0,graphwidth+2*w,graphheight+2*h));                 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        graph.removeCells(graph.getChildCells(graph.getDefaultParent()));
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Object createNode(Object parent, NodeValue value) {
        
        graph.getModel().beginUpdate();
        mxCell node = (mxCell) graph.insertVertex((parent == null) ? graph.getDefaultParent() : parent, String.valueOf(value.getID()), value, 0, 0, 36, 36);
        
        //set visual representation of the node based on its type
        switch (value.getType()) {
            case V_NODE:
                node.setStyle("node");
                break;
            case V_NAME:
                node.setStyle("name");
                graph.updateCellSize(node);
                break;
            case V_PRIVNAME:
                node.setStyle("privname");              
                graph.updateCellSize(node);               
                break;                     
            case V_PROCNAME:
                node.setStyle("procname");              
                graph.updateCellSize(node);               
                break;  
            case V_LGROUP:
            case V_HGROUP:
                node.setStyle("group");
                graph.updateCellSize(node); 
                node.getGeometry().setAlternateBounds(new mxRectangle(0, 0, node.getGeometry().getWidth(), node.getGeometry().getHeight()));                                                                               
                break;
            case V_NOGROUP:
                node.setStyle("nogroup");
                graph.updateCellSize(node);
                break;
            case V_PROC:
                node.setStyle("proc");
                graph.updateCellSize(node);
                break;
        }
        
        graph.getModel().endUpdate();        
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object createEdge(Object parent, Object source, Object target, EdgeValue value) {
        
        graph.getModel().beginUpdate();
        mxCell edge = (mxCell) graph.insertEdge((parent == null) ? graph.getDefaultParent() : parent, String.valueOf(value.getID()), value, source, target);
        
        //set visual representation of the edge based on its type
        switch (value.getType()) {
            case E_FLOW:
                edge.setStyle("flow");
                break;
            case E_PARAM:
                edge.setStyle("param");
                break;
            case E_IN:
                edge.setStyle("in");
                break;
            case E_OUT:
                edge.setStyle("out");
                break;
        }
        
        graph.getModel().endUpdate();        
        return edge;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void collapse(Object[] nodes) {
        graph.getModel().beginUpdate();  
        graph.foldCells(true, true, nodes);
        graph.getModel().endUpdate();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public void expand(Object[] nodes) {
        graph.getModel().beginUpdate();  
        graph.foldCells(false, true, nodes);  
        graph.getModel().endUpdate();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public CellValue getValue(Object cell) {
        return (CellValue)((mxCell) cell).getValue();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public List<Object> getEdges(Object node) {
        return Arrays.asList(mxGraphModel.getEdges(graph.getModel(), node));
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Object getTarget(Object edge) {
        return ((mxCell) edge).getTarget();        
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Object getSource(Object edge) {
        return ((mxCell) edge).getSource();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getChildren(Object group) {
        return Arrays.asList(mxGraphModel.getChildVertices(graph.getModel(), group));
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void remove(Object o) {
        graph.removeCells(new Object[] {o});
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public Object getParent(Object node) {
        return ((mxCell) node).getParent();
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void setVisible(Object o, boolean b) {
        mxCell cell = (mxCell) o;
        cell.setVisible(b);
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void setReductionSelected(Object o, boolean b) {
        if (((NodeValue) getValue(o)).getType() == NodeValue.Type.V_NODE) {
            //modify style of the specified node
            if (b) {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ff0000", new Object[]{o});
            } else {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffffff", new Object[]{o});
            }
            graph.refresh();
        }        
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void setSuggested(Object o, boolean b) {
        if (((NodeValue) getValue(o)).getType() == NodeValue.Type.V_NODE) {
            //modify style of the specified node
            if (b) {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffdcdc", new Object[]{o});
            } else {
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "#ffffff", new Object[]{o});
            }
            graph.refresh();
        }        
    }         
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public void setSelected(Object o, boolean b) {
        mxCell cell = (mxCell) o;
        if (cell.isVertex()) {
            if (b) {
                selectNode(cell);
            } else {
                deselectNode(cell);
            }
        }
        else if (cell.isEdge()) {
            if (b) {
                selectEdge(cell);
            } else {
                deselectEdge(cell);
            }
        }
    }
    
    /**
     * Changes style of the specified node to demonstrate its selection.
     * @param cell selected node
     */    
    private void selectNode(mxCell cell) {
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#0000ff", new Object[]{cell});
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "2", new Object[]{cell});
        gcomp.refresh();
    }
    
    /**
     * Changes style of previously selected node back to its normal look.
     * @param cell deselected node
     */    
    private void deselectNode(mxCell cell) {
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#000000", new Object[]{cell});
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", new Object[]{cell});
        gcomp.refresh();
    }
    
    /**
     * Changes style of the specified edge to demonstrate its selection.
     * @param cell selected edge
     */
    private void selectEdge(mxCell cell) {
        graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#0000ff", new Object[]{cell});
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "2", new Object[]{cell});
        gcomp.refresh();
    }
    
    /**
     * Changes style of previously selected edge back to its normal look.
     * @param cell deselected edge
     */
    private void deselectEdge(mxCell cell) {
        
        EdgeValue.Type type = ((EdgeValue)getValue(cell)).getType();
        switch (type) {
            case E_FLOW:
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#000000", new Object[]{cell});
                break;
            case E_IN:
            case E_OUT:
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#777777", new Object[]{cell});
                break;
            case E_PARAM:
                graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "#555555", new Object[]{cell});
                break;
            default:
                break;
        }
                        
        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1", new Object[]{cell});
        gcomp.refresh();
    }

    /**
     * {@inheritDoc}
     */ 
    @Override
    public List<ExportAction> getExportFormats() {
        List<ExportAction> ret = new ArrayList<>();
        ret.add(new ExportAction("svg") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });
        ret.add(new ExportAction("emf", "emf", true) {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });
        ret.add(new ExportAction("eps") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });
        ret.add(new ExportAction("png", "png", true) {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });        
        ret.add(new ExportAction("jpg") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });        
        ret.add(new ExportAction("bmp") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new JGraphXFileExporter(gcomp)).export(os, this);
            }
        });
        return ret;
    }

    /**
     * {@inheritDoc}
     */     
    @Override
    public boolean supportsFolding() {
        return true;
    }

}
