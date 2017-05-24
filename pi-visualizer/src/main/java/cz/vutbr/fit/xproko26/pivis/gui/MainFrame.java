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
package cz.vutbr.fit.xproko26.pivis.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/**
 * MainFrame is the main application frame which comprises and positions 
 * most of the application gui components.
 * @author Dagmar Prokopova
 */
public class MainFrame extends JFrame {
    
    //the top level container
    JSplitPane content;
    
    //tab pane
    JTabbedPane tabpane;
   
    /**
     * Constructor which initializes the defualt look, size, position
     * and behavior of the main frame.
     */
    public MainFrame() {
        
        setMinimumSize(new Dimension(1024, 720));
        setPreferredSize(new Dimension(1024,720));
        setTitle("PI-Visualizer");
        setIconImage(new ImageIcon(getClass().getResource("/images/appicon.png")).getImage());
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());     
        
        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Sets application menu and toolbar on top of the window.
     * @param menu application menu
     * @param toolbar application toolbar
     */
    public void setControl(JMenuBar menu, JToolBar toolbar) {
        setJMenuBar(menu);
        getContentPane().add(toolbar, BorderLayout.NORTH);
    }
    
    /**
     * Sets and positions application inner components.
     * @param firsttab the first tab of upper tab pane
     * @param secondtab the second tab of upper tab pane
     * @param centercomp central component
     * @param bottcomp bottom text line
     * @param leftpanel resizeble and hideable left panel
     */
    public void setContent(JComponent firsttab, JComponent secondtab, JComponent centercomp, JComponent bottcomp, JPanel leftpanel) {
                  
        tabpane = new JTabbedPane();
        tabpane.addTab("", firsttab);
        tabpane.addTab("", secondtab);
        tabpane.setSelectedIndex(0);
        
        JSplitPane bottomsp = new JSplitPane();
        bottomsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        bottomsp.setDividerSize(3);
        bottomsp.setTopComponent(centercomp);               
        bottomsp.setBottomComponent(bottcomp);
        bottomsp.setResizeWeight(1);
        
        JSplitPane mainsp = new JSplitPane();
        mainsp.setOrientation(JSplitPane.VERTICAL_SPLIT);
        mainsp.setDividerLocation(180);
        mainsp.setDividerSize(5);
        mainsp.setTopComponent(tabpane);               
        mainsp.setBottomComponent(bottomsp);
        
        content = new JSplitPane();
        content.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        content.setDividerSize(5);
        content.setLeftComponent(leftpanel);               
        content.setRightComponent(mainsp);
        
        getContentPane().add(content);
    }
    
    /**
     * Reloads frame content.
     */
    public void reload() {
        
        //prevent changing size of the window upon pack
        Dimension oldminsize = getMinimumSize();
        setMinimumSize(getSize());
        pack();
        setMinimumSize(oldminsize);
    }    
    
    /**
     * Moves divider to right to make space for left panel.
     * @param width the width of the left panel
     */
    public void showLeftPanel(double width) {
        content.setDividerLocation((int)width);
    }
    
    /**
     * Sets the title of tab at specified position.
     * @param i index of the tab
     * @param name title to be set
     */
    public void setTabPaneName(int i, String name) {
        tabpane.setTitleAt(i, name);
    }
}
