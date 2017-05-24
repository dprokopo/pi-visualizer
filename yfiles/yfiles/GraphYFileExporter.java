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


import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasComponent;
import com.yworks.yfiles.view.Colors;
import com.yworks.yfiles.view.GraphComponent;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.export.ContextConfigurator;
import com.yworks.yfiles.view.export.PixelImageExporter;
import java.awt.Color;


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.ps.EPSGraphics2D;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;

/**
 * GraphYFileExporter provides methods for yFiles-graph export.
 * @author Dagmar Prokopova
 */
public class GraphYFileExporter {
    
    private final GraphComponent component;
    private final PixelImageExporter exporter;
    
    GraphYFileExporter(GraphComponent comp) {
        component = comp;
        exporter = new PixelImageExporter(createContextConfigurator());
    }
    
    private GraphComponent getExportComponent() {
        //create new component without selectors, etc.
        GraphComponent comp = new GraphComponent();
        comp.setSize(component.getSize());
        comp.setGraph(component.getGraph());
        comp.setViewPoint(component.getViewPoint());
        comp.setBackground(component.getBackground());
        comp.repaint();
        return comp;
    }
    
    private ContextConfigurator createContextConfigurator() {
        RectD regionToExport = component.getContentRect();
        ContextConfigurator configurator = new ContextConfigurator(regionToExport.getEnlarged(1));
        configurator.setScale(component.getZoom());
        return configurator;
    }
        
    public void export(FileOutputStream os, ExportAction action) throws Exception {
        if (exporter.isFormatSupported(action.getExtension())) {
            exportBitmap(os, action.getExtension(), action.isTransparent());
        } else if (action.getExtension().equals("svg")){
            exportSvg(os);
        } else {
            exportVector(os, action.getExtension(), action.isTransparent());
        }
    }        
    
    public void exportBitmap(FileOutputStream os, String extension, boolean transparent) throws Exception {       
        if (transparent) {
            exporter.setTransparencyEnabled(true);
        } else {
            exporter.setUsingCanvasComponentBackgroundColorEnabled(true);
        }
        
        exporter.export(getExportComponent(), os, extension);         
    }

    public DocumentFragment getSVGDocFragment() {
        //export to an SVG element
        Element svgRoot = exportToSVGElement();
        DocumentFragment svgDocumentFragment = svgRoot.getOwnerDocument().createDocumentFragment();
        svgDocumentFragment.appendChild(svgRoot);
        
        return svgDocumentFragment;
    }
    
    
    public void exportSvg(FileOutputStream os) throws Exception {
        // write the SVG Document into the specified file
        OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
        writeDocument(getSVGDocFragment(), writer);
    }
    
    private Element exportToSVGElement() {
        //create a SVG document
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

        //create a converter for this document
        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(doc);

        //paint the content of the exporting graph component to the Graphics object
        paint(getExportComponent(), svgGraphics2D);

        svgGraphics2D.dispose();
        Element svgRoot = svgGraphics2D.getRoot(doc.getDocumentElement());
        svgRoot.setAttributeNS(XMLConstants.XMLNS_NAMESPACE_URI, XMLConstants.XMLNS_PREFIX + ":"
                + XMLConstants.XLINK_PREFIX, XMLConstants.XLINK_NAMESPACE_URI);
        return svgRoot;
    }
    
    private void writeDocument(DocumentFragment svgDocument, Writer writer) throws Exception {
        try {
            //prepare the DOM document for writing
            Source source = new DOMSource(svgDocument);
            Result result = new StreamResult(writer);

            //write the DOM document to the file
            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                tf.setAttribute("indent-number", 2);
            } catch (IllegalArgumentException iaex) {}
            Transformer xformer = tf.newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, result);
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Paints the canvas on the provided graphics context.
     */
    private void paint(CanvasComponent canvas, Graphics2D gfx) {
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ContextConfigurator cnfg = createContextConfigurator();
        final Graphics2D graphics = (Graphics2D) gfx.create();
        try {
            // fill background
            Paint fill = Colors.TRANSPARENT;
            if (fill != null) {
                final Paint oldPaint = graphics.getPaint();
                graphics.setPaint(fill);
                graphics.fill(new Rectangle2D.Double(0, 0, cnfg.getViewWidth(), cnfg.getViewHeight()));
                graphics.setPaint(oldPaint);
            }

            IRenderContext paintContext = cnfg.createRenderContext(canvas);
            graphics.transform(paintContext.getToWorldTransform());

            // export the canvas content
            canvas.exportContent(paintContext).paint(paintContext, graphics);
        } finally {
            graphics.dispose();
        }
    }
    
    
    
    public void exportVector(FileOutputStream os, String extension, boolean transparent) {
            
        final ContextConfigurator cnfg = createContextConfigurator();
        final GraphComponent canvas = getExportComponent();
        final Dimension size = new Dimension(cnfg.getViewWidth(), cnfg.getViewHeight());

        // create and initialize the VectorGraphics
        final VectorGraphics gfx;
        switch (extension) {
            case "eps":
                gfx = createEpsGraphics(os, size);
                break;
            default:
                gfx = createEmfGraphics(os, size);
                break;
        }
        
        gfx.startExport();

        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final Graphics2D graphics = (Graphics2D) gfx.create();
        try {
            // fill background
            Paint fill = (transparent) ? Colors.TRANSPARENT : Color.WHITE;
            if (fill != null) {
                final Paint oldPaint = graphics.getPaint();
                graphics.setPaint(fill);
                graphics.fill(new Rectangle2D.Double(0, 0, cnfg.getViewWidth(), cnfg.getViewHeight()));
                graphics.setPaint(oldPaint);
            }

            IRenderContext paintContext = cnfg.createRenderContext(canvas);
            graphics.transform(paintContext.getToWorldTransform());

            // export the canvas content
            canvas.exportContent(paintContext).paint(paintContext, graphics);
        } finally {
            graphics.dispose();
        }

        gfx.endExport();
    }    
    
    private EMFGraphics2D createEmfGraphics(FileOutputStream os, Dimension size) {
        EMFGraphics2D gfx = new EMFGraphics2D(os, size);
        gfx.setDeviceIndependent(true);
        return gfx;
    }

    private EPSGraphics2D createEpsGraphics(FileOutputStream os, Dimension size) {
        Properties properties = new Properties();
        properties.putAll(EPSGraphics2D.getDefaultProperties());
        properties.setProperty(EPSGraphics2D.PAGE_SIZE, EPSGraphics2D.CUSTOM_PAGE_SIZE);
        properties.setProperty(EPSGraphics2D.CUSTOM_PAGE_SIZE, size.width + ", " + size.height);
        UserProperties.setProperty(properties, EPSGraphics2D.PAGE_MARGINS, new Insets(0, 0, 0, 0));
        UserProperties.setProperty(properties, EPSGraphics2D.FIT_TO_PAGE, false);

        EPSGraphics2D gfx = new EPSGraphics2D(os, size);
        gfx.setProperties(properties);
        return gfx;
    }
}
