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

import javax.swing.JMenuItem;

/**
 * GraphListener is an interface which provedes method for reporting user
 * interaction with the graph in order to be processed.
 * @author Dagmar Prokopova
 */
public interface GraphListener {
    
    /**
     * Reports that specified group node has been expanded.
     * @param o expanded node
     */
    public void nodeExpanded(Object o);
    
    /**
     * Reports that specified group node has been collapsed.
     * @param o collapsed node
     */
    public void nodeCollapsed(Object o);
    
    /**
     * Reports that specified group node has been left-mouse-button clicked.
     * @param o clicked node
     */
    public void nodeClicked(Object o);
    
    /**
     * Reports that canvas was clicked.
     */
    public void canvasClicked();
    
    /**
     * Returns an array of menu items which shall displayed in context menu
     * opened as an reaction to rignt-mouse-button click on specific node.
     * @param o node that was right-mouse-button clicked
     * @return array of menu items.
     */
    public JMenuItem[] getPopupMenuItems(Object o);

}
