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
package cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.yfiles;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.graph.FilteredGraphWrapper;
import com.yworks.yfiles.graph.FolderNodeState;
import com.yworks.yfiles.graph.FoldingEdgeState;
import com.yworks.yfiles.graph.FoldingEdgeStateId;
import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IFoldingView;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelDefaults;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.INodeDefaults;
import com.yworks.yfiles.graph.LayoutUtilities;
import com.yworks.yfiles.graph.labelmodels.InteriorLabelModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.ArrowType;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.IEdgeStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.graph.styles.SimpleLabelStyle;
import com.yworks.yfiles.layout.FixNodeLayoutStage;
import com.yworks.yfiles.layout.LayoutOrientation;
import com.yworks.yfiles.layout.hierarchic.HierarchicLayout;
import com.yworks.yfiles.layout.hierarchic.LayoutMode;
import com.yworks.yfiles.utils.IListEnumerable;
import com.yworks.yfiles.utils.ItemEventArgs;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.DashStyle;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.input.AbstractPopupMenuInputMode;
import com.yworks.yfiles.view.input.ClickEventArgs;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import com.yworks.yfiles.view.input.ItemClickedEventArgs;
import com.yworks.yfiles.view.input.NavigationInputMode;
import com.yworks.yfiles.view.input.PopulateItemPopupMenuEventArgs;
import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import cz.vutbr.fit.xproko26.pivis.gui.graph.CellValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.EdgeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphLib;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.GraphListener;

/**
 * GraphYFiles represents the class which connects yFiles library to graph manager.
 * It provides methods for graph manipulation.
 * @author Dagmar Prokopova
 */
public class GraphYFiles implements GraphLib {

    private GraphListener glist;
    private final GraphComponent gcomp;
    private IGraph origraph;
    private FoldingManager foldmanager;
    private FilteredGraphWrapper filteredGraph;
    
    private HashMap<String, INodeStyle> nodestyles;
    private HashMap<String, IEdgeStyle> edgestyles;
    private HashMap<String, ILabelStyle> labelstyles;
    

    public GraphYFiles() {
        gcomp = new GraphComponent();
        gcomp.getFocusIndicatorManager().setEnabled(false);

        setStyles();
        setFiltering(); //important order (must be before enableFolding())
        enableFolding();
        configureInteraction();        
    }
    
    @Override
    public void addListener(GraphListener glist) {
        this.glist = glist;
    }
        
    
    private void setStyles() {
        
        Pen pen = null;
                
        nodestyles = new HashMap<>();
        edgestyles = new HashMap<>();
        labelstyles = new HashMap<>();
                
        ShapeNodeStyle flowNS = new ShapeNodeStyle();
        flowNS.setShape(ShapeNodeShape.ELLIPSE);
        flowNS.setPaint(Colors.WHITE);
        nodestyles.put("node", flowNS);

        ShapeNodeStyle nameNS = new ShapeNodeStyle();
        nameNS.setShape(ShapeNodeShape.RECTANGLE);
        nameNS.setPaint(Colors.LIGHT_GRAY);
        nameNS.setPen(Pen.getTransparent());
        nodestyles.put("name", nameNS);
        
        ShapeNodeStyle privnameNS = new ShapeNodeStyle();
        privnameNS.setShape(ShapeNodeShape.RECTANGLE);
        privnameNS.setPaint(Colors.DARK_GRAY);
        privnameNS.setPen(Pen.getTransparent());
        nodestyles.put("privname", privnameNS);
        
        PanelNodeStyle groupNS = new PanelNodeStyle();
        groupNS.setColor(Colors.WHITE);
        groupNS.setInsets(new InsetsD(40, 10, 10, 10));        
        groupNS.setLabelInsetsColor(new Color(245,245,245));
        nodestyles.put("group", new CollapsibleNodeStyleDecorator(groupNS));
        
        PanelNodeStyle selgroupNS = new PanelNodeStyle();
        selgroupNS.setColor(Colors.WHITE);
        selgroupNS.setInsets(new InsetsD(40, 10, 10, 10));        
        selgroupNS.setLabelInsetsColor(Color.BLUE); 
        nodestyles.put("selgroup", new CollapsibleNodeStyleDecorator(selgroupNS));
        
        PanelNodeStyle folderNS = new PanelNodeStyle();
        folderNS.setColor(Colors.WHITE);
        nodestyles.put("folder", new CollapsibleNodeStyleDecorator(folderNS));
        
        ShapeNodeStyle selfolderNS = new ShapeNodeStyle();
        selfolderNS.setPaint(Color.WHITE);
        pen = new Pen(Color.BLUE);
        pen.setThickness(2);
        selfolderNS.setPen(pen);
        nodestyles.put("selfolder", new CollapsibleNodeStyleDecorator(selfolderNS));
        
        PanelNodeStyle nogroupNS = new PanelNodeStyle();
        nogroupNS.setColor(Colors.WHITE);
        nodestyles.put("nogroup", nogroupNS);
        
        ShapeNodeStyle selnogroupNS = new ShapeNodeStyle();
        selnogroupNS.setPaint(Color.WHITE);
        pen = new Pen(Color.BLUE);
        pen.setThickness(2);
        selnogroupNS.setPen(pen);
        nodestyles.put("selnogroup", selnogroupNS);
        
        
        PolylineEdgeStyle paramES = new PolylineEdgeStyle();
        pen = new Pen(Colors.GRAY);
        pen.setDashStyle(new DashStyle((new double[] {5}), 30 ));
        paramES.setPen(pen);
        edgestyles.put("param", paramES);

        Arrow defaultArrow = new Arrow(ArrowType.DEFAULT, new Pen(Colors.BLACK), Colors.BLACK);
        Arrow circleArrow = new Arrow(ArrowType.CIRCLE, new Pen(Colors.GRAY), Colors.GRAY);
        Arrow pointerArrow = new Arrow(ArrowType.SHORT, new Pen(Colors.GRAY), Colors.GRAY);
        
        PolylineEdgeStyle inES = new PolylineEdgeStyle();
        inES.setSourceArrow(pointerArrow);        
        inES.setTargetArrow(circleArrow);
        inES.setPen(new Pen(Colors.GRAY));
        edgestyles.put("in", inES);
        
        PolylineEdgeStyle outES = new PolylineEdgeStyle();
        outES.setSourceArrow(circleArrow);
        outES.setTargetArrow(pointerArrow);
        outES.setPen(new Pen(Colors.GRAY));
        edgestyles.put("out", outES);
        
        PolylineEdgeStyle flowES = new PolylineEdgeStyle();
        flowES.setSourceArrow(Arrow.NONE);
        flowES.setTargetArrow(defaultArrow);
        flowES.setPen(new Pen(Colors.BLACK));
        edgestyles.put("flow", flowES);
        
        PolylineEdgeStyle selparamES = new PolylineEdgeStyle();
        pen = new Pen(Colors.BLUE);
        pen.setDashStyle(new DashStyle((new double[] {5}), 30 ));
        pen.setThickness(2);
        selparamES.setPen(pen);
        edgestyles.put("selparam", selparamES);

        defaultArrow = new Arrow(ArrowType.DEFAULT, new Pen(Colors.BLUE), Colors.BLUE);
        circleArrow = new Arrow(ArrowType.CIRCLE, new Pen(Colors.BLUE), Colors.BLUE);
        pointerArrow = new Arrow(ArrowType.SHORT, new Pen(Colors.BLUE), Colors.BLUE);
        
        PolylineEdgeStyle selinES = new PolylineEdgeStyle();
        selinES.setSourceArrow(pointerArrow);        
        selinES.setTargetArrow(circleArrow);
        pen = new Pen(Colors.BLUE);
        pen.setThickness(2);
        selinES.setPen(pen);
        edgestyles.put("selin", selinES);
        
        PolylineEdgeStyle seloutES = new PolylineEdgeStyle();
        seloutES.setSourceArrow(circleArrow);
        seloutES.setTargetArrow(pointerArrow);
        pen = new Pen(Colors.BLUE);
        pen.setThickness(2);
        seloutES.setPen(pen);
        edgestyles.put("selout", seloutES);
        
        PolylineEdgeStyle selflowES = new PolylineEdgeStyle();
        selflowES.setSourceArrow(Arrow.NONE);
        selflowES.setTargetArrow(defaultArrow);
        pen = new Pen(Colors.BLUE);
        pen.setThickness(2);
        selflowES.setPen(pen);
        edgestyles.put("selflow", selflowES);
        
        
        
        INodeDefaults nodeDefaults = gcomp.getGraph().getNodeDefaults();
        ILabelDefaults labelDefaults = nodeDefaults.getLabelDefaults();
        labelDefaults.setLayoutParameter(InteriorLabelModel.CENTER);
        SimpleLabelStyle defaultLS = new SimpleLabelStyle();
        defaultLS.setTextPaint(Colors.BLACK);
        defaultLS.setFont(new Font ("Arial", Font.BOLD , 16));
        labelDefaults.setStyle(defaultLS);
        
        SimpleLabelStyle nameLS = new SimpleLabelStyle();
        nameLS.setFont(new Font ("Arial", Font.ITALIC , 17));
        nameLS.setUsingFractionalFontMetricsEnabled(true);
        labelstyles.put("name", nameLS);
        
        SimpleLabelStyle privLS = new SimpleLabelStyle();
        privLS.setTextPaint(Colors.WHITE);
        privLS.setUsingFractionalFontMetricsEnabled(true);
        privLS.setFont(new Font ("Arial", Font.ITALIC , 17));
        labelstyles.put("privname", privLS);
        
        SimpleLabelStyle groupLS = new SimpleLabelStyle();
        groupLS.setTextPaint(Colors.BLACK);
        groupLS.setUsingFractionalFontMetricsEnabled(true);
        groupLS.setFont(new Font ("Arial", Font.ITALIC | Font.BOLD , 16));
        groupLS.setInsets(new InsetsD(5,2,5,2));
        labelstyles.put("group", groupLS);

    }
    
    
    private void setFiltering() {
        origraph = gcomp.getGraph();
        filteredGraph = new FilteredGraphWrapper(origraph, this::nodePredicate , this::edgePredicate);
        gcomp.setGraph(filteredGraph);
    }
    
    private boolean nodePredicate(INode node) {
        CellValue cv = (CellValue) node.getTag();
        return cv.isVisible();
    }
    
    private boolean edgePredicate(IEdge edge) {
        CellValue cv = (CellValue) edge.getTag();
        return cv.isVisible();
    }

    
    
    private void enableFolding() {
        //create the folding manager
        foldmanager = new FoldingManager(gcomp.getGraph());        
        //replace the displayed graph with a folding view
        gcomp.setGraph(foldmanager.createFoldingView().getGraph());
    }        
    
    
    private void configureInteraction() {
        
        GraphViewerInputMode vim = new GraphViewerInputMode();      

        NavigationInputMode nm = vim.getNavigationInputMode();
        nm.addGroupCollapsedListener((Object o, ItemEventArgs<INode> args) -> {
            INode n = args.getItem();
            if (glist != null) {
                INode node = gcomp.getGraph().getFoldingView().getMasterItem(n);
                glist.nodeCollapsed(node);
            }
        });
        
        nm.addGroupExpandedListener((Object o, ItemEventArgs<INode> args) -> {
            INode n = args.getItem();
            if (glist != null) {                
                INode node = gcomp.getGraph().getFoldingView().getMasterItem(n);
                glist.nodeExpanded(node);
            }
        });               
        nm.setEnabled(true);        
        nm.setExpandGroupAllowed(true);
        nm.setCollapseGroupAllowed(true);
        

        vim.setSelectableItems(GraphItemTypes.NONE); 
        vim.addCanvasClickedListener((Object o, ClickEventArgs t) -> {
            if (glist != null) {
                    glist.canvasClicked();
                }
        });
        
        vim.addItemLeftClickedListener((Object o, ItemClickedEventArgs<IModelItem> args) -> {
            ICanvasObject cobj = gcomp.getCanvasObject(args.getLocation());
            if (cobj != null) {
                Object obj = cobj.getUserObject();
                INode node = null;
                if (obj instanceof ILabel) {
                    node = (INode)((ILabel)obj).getOwner();
                }
                else if (obj instanceof INode) {
                    node = (INode) obj;
                }
                                
                if ((node != null) && (glist != null)) {
                    glist.nodeClicked(gcomp.getGraph().getFoldingView().getMasterItem(node));
                }
            }            
        });
        
        vim.setPopupMenuItems(GraphItemTypes.NODE);
        
        AbstractPopupMenuInputMode pop = vim.getPopupMenuInputMode();
        pop.setEnabled(true);
        vim.addPopulateItemPopupMenuListener(this::populateItemPopupMenu);
                                
        gcomp.setInputMode(vim);

    }
    
    private void populateItemPopupMenu(Object source, PopulateItemPopupMenuEventArgs<IModelItem> args) {
        JMenuItem[] items = (glist != null) ? glist.getPopupMenuItems(args.getItem()) : null;

        if (items != null) {
            JPopupMenu popupMenu = (JPopupMenu) args.getMenu();
            for (JMenuItem item : items) {
                popupMenu.add(item);
            }
        }

        args.setShowingMenuRequested(true);
        args.setHandled(true);
    }
    
    @Override
    public void clear() {
        origraph.clear();
    }

    @Override
    public JComponent getGraphComponent() {
        return gcomp;
    }

    @Override
    public Object createNode(Object parent, NodeValue value) {               
        
        INode node = null;
        ILabel label = null;
        FolderNodeState foldstat = null;

        switch (value.getType()) {
            case V_NODE:   
                node = origraph.createNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("node"), value);
                origraph.addLabel(node, value.getLabel(), InteriorLabelModel.CENTER);
                break;
            case V_NAME:
                node = origraph.createNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("name"), value);
                label = origraph.addLabel(node, value.getLabel(), InteriorLabelModel.CENTER, labelstyles.get("name"));
                origraph.setNodeLayout(node, new RectD(0, 0, label.getLayout().getWidth()+2*10, 30));
                break;
            case V_PRIVNAME:                
                node = origraph.createNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("privname"), value);
                label = origraph.addLabel(node, value.getLabel(), InteriorLabelModel.CENTER, labelstyles.get("privname"));                
                origraph.setNodeLayout(node, new RectD(0, 0, label.getLayout().getWidth()+2*10, 30));
                break;
            case V_LGROUP:
                node = origraph.createGroupNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("folder"), value);
                label = origraph.addLabel(node, value.getLabel(), InteriorLabelModel.CENTER, labelstyles.get("group"));
                origraph.setNodeLayout(node, new RectD(0, 0, label.getLayout().getWidth()+2*10, 30));
             
                foldstat = foldmanager.getFolderNodeState(node);
                foldstat.setStyle(nodestyles.get("folder"));
                break;
                
            case V_HGROUP:
                node = origraph.createGroupNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("group"), value);
                label = origraph.addLabel(node, value.getLabel(), InteriorLabelModel.NORTH, (ILabelStyle) labelstyles.get("group").clone());
                origraph.setNodeLayout(node, new RectD(0, 0, label.getLayout().getWidth()+2*10, 30));
                
                foldstat = foldmanager.getFolderNodeState(node);
                foldstat.setStyle(nodestyles.get("folder"));
                foldstat.getLabels().first().setLayoutParameter(InteriorLabelModel.CENTER);
                break;
                
            case V_NOGROUP:
                node = origraph.createGroupNode((INode)parent, new RectD(0, 0, 30, 30), nodestyles.get("nogroup"), value);
                label = origraph.addLabel(node, value.getLabel(), InteriorLabelModel.CENTER, labelstyles.get("group"));
                origraph.setNodeLayout(node, new RectD(0, 0, label.getLayout().getWidth()+2*10, 30));
                break;
        }
                
        return node;
    }

    @Override
    public Object createEdge(Object parent, Object source, Object target, EdgeValue value) {
        IEdge edge = null;
        
        switch (value.getType()) {
            case E_FLOW:
                edge = origraph.createEdge((INode)source, (INode)target, edgestyles.get("flow"), value);
                break;
            case E_PARAM:
                edge = origraph.createEdge((INode)source, (INode)target, edgestyles.get("param"), value);
                break;
            case E_IN:
                edge = origraph.createEdge((INode)source, (INode)target, edgestyles.get("in"), value);
                break;
            case E_OUT:
                edge = origraph.createEdge((INode)source, (INode)target, edgestyles.get("out"), value);
                break;
        }
        return edge;
    }

    @Override
    public void executeLayout(boolean animation) {
        HierarchicLayout layout = new HierarchicLayout();
        layout.prependStage(new FixNodeLayoutStage());
        layout.setLayoutOrientation(LayoutOrientation.LEFT_TO_RIGHT);
        layout.setLayoutMode(LayoutMode.FROM_SCRATCH);
        
        if (animation) {
            LayoutUtilities.morphLayout(gcomp, layout, Duration.ofMillis(400), null);
        } else {
            LayoutUtilities.applyLayout(gcomp.getGraph(), layout);
        }
        
        gcomp.fitGraphBounds();
    }


    @Override
    public void collapse(Object[] nodes) {
        IFoldingView view = gcomp.getGraph().getFoldingView();
        for (Object node : nodes) {            
            view.collapse(view.getViewItem((INode) node));
            if (glist != null) {
                glist.nodeCollapsed(node);
            }
        }
    }
    
    @Override
    public void expand(Object[] nodes) {
        IFoldingView view = gcomp.getGraph().getFoldingView();
        for (Object node : nodes) {            
            view.expand(view.getViewItem((INode) node));
            if (glist != null) {
                glist.nodeExpanded(node);
            }
        }
    }

    @Override
    public CellValue getValue(Object cell) {
        if (cell instanceof INode)
            return (CellValue)((INode) cell).getTag();
        else if (cell instanceof IEdge)
            return (CellValue)((IEdge) cell).getTag();
        else
            return null;
    }


    @Override
    public List<Object> getEdges(Object node) {
        List<Object> ret = new ArrayList<>();
        IListEnumerable <IEdge> edges = origraph.edgesAt((INode)node);
        for (int i=0; i < edges.size(); i++) {
            ret.add(edges.getItem(i));
        }
        return ret;
    }
    
    @Override
    public Object getTarget(Object edge) {
        return ((IEdge) edge).getTargetNode();
        
    }

    @Override
    public Object getSource(Object edge) {
        return ((IEdge) edge).getSourceNode();
    }


    @Override
    public List<Object> getChildren(Object group) {
        List<Object> ret = new ArrayList<>();
        IListEnumerable <INode> children = origraph.getChildren((INode)group);
        for (int i=0; i < children.size(); i++) {
            ret.add(children.getItem(i));
        }
        return ret;
    }
    
    @Override
    public void setVisible(Object o, boolean b) {
        if (o instanceof INode) {
            filteredGraph.nodePredicateChanged();
        } else {
            origraph.clearBends((IEdge) o);
            filteredGraph.edgePredicateChanged();
        }
    }

    @Override
    public void setReductionSelected(Object o, boolean b) {
        if (((NodeValue) getValue(o)).getType() == NodeValue.Type.V_NODE) {
            INode node = (INode) o;
            ShapeNodeStyle style = ((ShapeNodeStyle) node.getStyle()).clone(); 
            if (b) {
                style.setPaint(Color.RED);
            } else {
                style.setPaint(Color.WHITE);
            }
            origraph.setStyle(node, style);
        }
    }
    
    @Override
    public void setSuggested(Object o, boolean b) {
        if (((NodeValue) getValue(o)).getType() == NodeValue.Type.V_NODE) {
            INode node = (INode) o;
            ShapeNodeStyle style = ((ShapeNodeStyle) node.getStyle()).clone();            
            if (b) {
                style.setPaint(new Color(255,220,220));
            } else {
                style.setPaint(Color.WHITE);
            }            
            origraph.setStyle(node, style);
        }
    }
    
    
    private void selectNode(INode node) {
        NodeValue.Type type = ((NodeValue)getValue(node)).getType();  
        
        switch (type) {
            case V_NODE:
            case V_NAME:
            case V_PRIVNAME:
                ShapeNodeStyle style = ((ShapeNodeStyle) node.getStyle()).clone();
                Pen p = new Pen(Color.BLUE);
                p.setThickness(2);
                style.setPen(p);
                origraph.setStyle(node, style);
                break;
            case V_LGROUP:
                foldmanager.getFolderNodeState(node).setStyle(nodestyles.get("selfolder"));
                origraph.setStyle(node, nodestyles.get("selfolder"));
                break;
            case V_HGROUP:
                FolderNodeState stat = foldmanager.getFolderNodeState(node);
                stat.setStyle(nodestyles.get("selfolder"));
                origraph.setStyle(node, nodestyles.get("selgroup"));

                SimpleLabelStyle sls = (SimpleLabelStyle) node.getLabels().first().getStyle();
                IFoldingView view = gcomp.getGraph().getFoldingView();
                
                //if (view.isInFoldingState(view.getViewItem(node))) {
                if (((NodeValue)node.getTag()).isCollapsed()) {
                    sls.setTextPaint(Color.BLACK);
                } else {
                    sls.setTextPaint(Color.WHITE);
                }
/*

*/
                break;
            case V_NOGROUP:
                origraph.setStyle(node, nodestyles.get("selnogroup"));
                break;
        }
        

    }
    
    private void deselectNode(INode node) {
        
        NodeValue.Type type = ((NodeValue)getValue(node)).getType();  
        
        
        switch (type) {
            case V_NODE:            
                ShapeNodeStyle style1 = ((ShapeNodeStyle) node.getStyle()).clone();
                Pen p = new Pen(Color.BLACK);
                p.setThickness(1);                
                style1.setPen(p);
                origraph.setStyle(node, style1);
                break;
            case V_NAME:
            case V_PRIVNAME:
                ShapeNodeStyle style2 = ((ShapeNodeStyle) node.getStyle()).clone();
                style2.setPen(Pen.getTransparent());
                origraph.setStyle(node, style2);
                break;
            case V_LGROUP:
                foldmanager.getFolderNodeState(node).setStyle(nodestyles.get("folder"));
                origraph.setStyle(node, nodestyles.get("folder"));
                break;
            case V_HGROUP:
                foldmanager.getFolderNodeState(node).setStyle(nodestyles.get("folder"));
                origraph.setStyle(node, nodestyles.get("group"));
                SimpleLabelStyle sls = (SimpleLabelStyle) node.getLabels().first().getStyle();
                sls.setTextPaint(Color.BLACK);
                break;
            case V_NOGROUP:
                origraph.setStyle(node, nodestyles.get("nogroup"));
                break;
        }
        
        
        

    }
    
    private void selectEdge(IEdge edge) {
        EdgeValue.Type type = ((EdgeValue)getValue(edge)).getType(); 
        switch (type) {
            case E_FLOW:
                origraph.setStyle(edge, edgestyles.get("selflow"));
                setFoldStyle(edge, edgestyles.get("selflow"));
                break;
            case E_IN:
                origraph.setStyle(edge, edgestyles.get("selin"));
                break;
            case E_OUT:
                origraph.setStyle(edge, edgestyles.get("selout"));
                break;
            case E_PARAM:
                origraph.setStyle(edge, edgestyles.get("selparam"));
                setFoldStyle(edge, edgestyles.get("selparam"));
                break;
            default:
                break;
        }
    }

    
    private void deselectEdge(IEdge edge) {
        EdgeValue.Type type = ((EdgeValue)getValue(edge)).getType();
        switch (type) {
            case E_FLOW:
                origraph.setStyle(edge, edgestyles.get("flow"));
                setFoldStyle(edge, edgestyles.get("flow"));
                break;
            case E_IN:
                origraph.setStyle(edge, edgestyles.get("in"));
                break;
            case E_OUT:
                origraph.setStyle(edge, edgestyles.get("out"));
                break;
            case E_PARAM:
                origraph.setStyle(edge, edgestyles.get("param"));
                setFoldStyle(edge, edgestyles.get("param"));
                break;
            default:
                break;
        }
    }
    
    
    private void setFoldStyle(IEdge edge, IEdgeStyle style) {

        try {
            Iterator<Entry<FoldingEdgeStateId, FoldingEdgeState>> iter = foldmanager.getAllViewStates(edge).iterator();
            while (iter.hasNext()) {
                Entry<FoldingEdgeStateId, FoldingEdgeState> entry = iter.next();
                entry.getValue().setStyle(style);
            }
        } catch (Exception ex) {
        }
    }
    
    
    @Override
    public void setSelected(Object o, boolean b) {
        
        if (o instanceof INode) {
            if (b)
                selectNode((INode) o);
            else
                deselectNode((INode) o);
        }
        else if (o instanceof IEdge) {
            if (b)
                selectEdge((IEdge) o);
            else
                deselectEdge((IEdge) o);
        }
    }

    @Override
    public void remove(Object o) {
        origraph.remove((IModelItem)o);
    }
    

    @Override
    public Object getParent(Object node) {
        return origraph.getParent((INode) node);
    }
    
    @Override
    public List<ExportAction> getExportFormats() {
        List<ExportAction> ret = new ArrayList<>();
        ret.add(new ExportAction("svg", "svg", true) {
            @Override
            public void export(FileOutputStream os) throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        }); 
        ret.add(new ExportAction("emf", "emf", true) {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        });
        ret.add(new ExportAction("eps") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        });
        ret.add(new ExportAction("png", "png", true) {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        });
        
        ret.add(new ExportAction("jpg") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        });
        
        ret.add(new ExportAction("bmp") {
            @Override
            public void export(FileOutputStream os)  throws Exception {
                (new GraphYFileExporter(gcomp)).export(os, this);
            }
        });
        return ret;
    }
    
    @Override
    public boolean supportsFolding() {
        return false;
    }
  
}
