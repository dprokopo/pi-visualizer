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

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsbase.util.UserProperties;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.ps.EPSGraphics2D;

import org.w3c.dom.Document;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;

/**
 * JGraphXFileExporter provides methods for JGraphX-graph export.
 * @author Dagmar Prokopova
 */
public class JGraphXFileExporter {
    
    //graph canvas containging the graph representation
    private final mxGraphComponent component;
    
    /**
     * Initializes class attribute.
     * @param comp graph canvas with graph to be exported
     */
    public JGraphXFileExporter(mxGraphComponent comp) {
        component = comp;
    }
    
    /**
     * Saves graph into the specified output stream using format specified by export action.
     * @param os final output stream
     * @param action export action specifying the exported file format
     * @throws Exception 
     */
    public void export(FileOutputStream os, ExportAction action) throws Exception {
        String ext = action.getExtension();
        
        switch (ext) {
            case "png":
            case "bmp":
            case "jpg":
                exportBitmap(os, ext, action.isTransparent());
                break;
            case "svg":
                exportSvg(os, ext);
                break;
            case "emf":
            case "eps":
                exportVector(os, ext, action.isTransparent());
            default:
                break;                
        }
    } 
    
    /**
     * Saves graph into the secified bitmap format using mxCellRenderer.
     * @param os output stream
     * @param extension extension specifying the format of the file
     * @param transparent true if background should be transparent
     * @throws Exception 
     */
    public void exportBitmap(FileOutputStream os, String extension, boolean transparent) throws Exception {
        
        mxGraph graph = component.getGraph(); 
        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, graph.getView().getScale(), (transparent) ? null : Color.WHITE, true, null);
        ImageIO.write(image, extension, os);
    }
    
    /**
     * Transfers graph into svg document with the usage of mxCellRenderer.
     * @param os output sream
     * @throws Exception 
     */
    public void exportSvg(FileOutputStream os, String extension) throws Exception {
        
        mxGraph graph = component.getGraph();        
        Document doc = mxCellRenderer.createSvgDocument(graph, null, graph.getView().getScale(), null, null);
        
        //write the SVG Document into the specified file
        OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
        writeDocument(doc, writer);       
    }
    
    /**
     * Writes created svg document into the file.
     * @param svgDocument created svg document
     * @param writer output stream writer
     * @throws Exception 
     */
    private void writeDocument(Document doc, Writer writer) throws Exception {
        try {
            //prepare the DOM document for writing
            Source source = new DOMSource(doc);
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
     * Exports graph into vector formats (not svg) using FreeHEP library.
     * @param os output stream
     * @param extension extension specifying the format of the file
     * @param transparent true if background should be transparent
     */    
    public void exportVector(FileOutputStream os, String extension, boolean transparent) throws Exception {

        mxGraph graph = component.getGraph(); 
        Object[] cells = new Object[] { graph.getModel().getRoot() }; 
        java.awt.Rectangle rect = graph.getPaintBounds(cells).getRectangle();
        Dimension size = new Dimension((int)rect.getWidth()+2, (int)rect.getHeight()+2);
        
        //create and initialize the VectorGraphics
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
            //fill background
            Paint fill = (transparent) ? null : Color.WHITE;
            if (fill != null) {
                final Paint oldPaint = graphics.getPaint();
                graphics.setPaint(fill);
                graphics.fill(new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()));
                graphics.setPaint(oldPaint);
            }

            mxGraphics2DCanvas canvas = new mxGraphics2DCanvas(graphics); 
            mxGraphView view = graph.getView();

            double previousScale = canvas.getScale();
            mxPoint previousTranslate = canvas.getTranslate();

            try {
                canvas.setTranslate(-rect.x+1, -rect.y+1);
                canvas.setScale(view.getScale());

                for (int i = 0; i < cells.length; i++) {
                    graph.drawCell(canvas, cells[i]);
                }
            } finally {
                canvas.setScale(previousScale);
                canvas.setTranslate(previousTranslate.getX(), previousTranslate.getY());
            }

        } finally {
            graphics.dispose();
        }

        gfx.endExport();
    }    
    
    /**
     * Prepares emf graphic.
     * @param os output stream
     * @param size size of the exported graphic
     * @return created emf graphic
     */
    private EMFGraphics2D createEmfGraphics(FileOutputStream os, Dimension size) {
        EMFGraphics2D gfx = new EMFGraphics2D(os, size);
        gfx.setDeviceIndependent(true);
        return gfx;
    }

    /**
     * Prepares eps graphic.
     * @param os output stream
     * @param size size of the exported graphic
     * @return created eps graphic
     */
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
