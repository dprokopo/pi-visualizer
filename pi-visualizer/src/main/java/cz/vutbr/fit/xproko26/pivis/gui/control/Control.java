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
package cz.vutbr.fit.xproko26.pivis.gui.control;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SELECTED_KEY;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;

/**
 * Control provides methods for creation of application menu and toolbar and
 * collects user actions performed in this components. All user actions are being
 * reported to control listener.
 * @author Dagmar Prokopova
 */
public class Control {
    
    /**
     * Options for choosing font size.
     */
    public enum FontSize {
        SMALL, MEDIUM, LARGE
    }
    
    /**
     * Options for choosing color scheme.
     */
    public enum ColorScheme {
        BLACK, WHITE, BLUE
    }

    /**
     * Options for choosing graph expanding style.
     */
    public enum ExpStyle { 
        HIERARCHIC, LINEAR 
    }  
    
    //reference to control listener
    private ControlListener listener;

    //control actions for file menu
    ControlAction newact;
    ControlAction openact;
    ControlAction saveact;
    ControlAction saveasact;
    ControlAction importenvact;
    ControlAction exportenvact;
    ControlAction exitact;
    
    //control actions for control menu
    ControlAction clearact;
    ControlAction visualizeact;
    ControlAction reduceact;
    ControlAction simplifyact;
    
    //control actions for settings menu
    ControlAction smallfontact;
    ControlAction mediumfontact;
    ControlAction largefontact;
    ControlAction blackcoloract;
    ControlAction bluecoloract;
    ControlAction whitecoloract;
    ControlAction linexpstyleact;
    ControlAction hierexpstyleact;
    ControlAction redlistact;
    ControlAction toolbaract;
    ControlAction soundsact;
    
    //control actions for help menu
    ControlAction howtoact;
    ControlAction aboutact;
    
    //menu bar
    private JMenuBar menu;
    //toolbar
    private JToolBar toolbar;
    
    //selected expand style
    private ExpStyle estyle;
    //selected color scheme
    private ColorScheme color;
    //selected font size
    private FontSize fontsize;
    
    /**
     * Controler which sets default choosing options, creates all control actions
     * and creates menu and toolbar.
     * @param eactions list of export actions
     */
    public Control(List<ExportAction> eactions) {
        
        estyle = ExpStyle.LINEAR;
        color = ColorScheme.BLACK;
        fontsize = FontSize.MEDIUM;

        createActions();
        createMenu(eactions);
        createToolBar();     
    }
        
    /**
     * Creates all control actions which are used by both menu and toolbar.
     */
    private void createActions() {
                
        /* ----- FILE ACTIONS ----- */
                
        newact = new ControlAction("New", new ImageIcon(getClass().getResource("/images/new-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuCreateNew();
            }
        };
        
        openact = new ControlAction("Open", new ImageIcon(getClass().getResource("/images/open-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuLoadFromFile();
            }
        };
        
        saveact = new ControlAction("Save", new ImageIcon(getClass().getResource("/images/save-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuSaveToFile();
            }
        };
        
        saveasact = new ControlAction("Save as", new ImageIcon(getClass().getResource("/images/saveas-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.ALT_MASK)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuSaveAsToFile();
            }
        };
        
        importenvact = new ControlAction("Import environment", null, null) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuLoadEnvironment();
            }
        };
        
        exportenvact = new ControlAction("Export environment", null, null) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuSaveEnvironment();
            }
        };
        
        exitact = new ControlAction("Exit", new ImageIcon(getClass().getResource("/images/exit-icon.png")), null) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuExit();
            }
        };        
        
        
        /* ----- CONTROL ACTIONS ----- */                
        
        clearact = new ControlAction("Clear console", new ImageIcon(getClass().getResource("/images/clear-icon.png")), null) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuClearConsole();
            }
        };
        
        visualizeact = new ControlAction("Visualize expression", new ImageIcon(getClass().getResource("/images/visualize-icon.png")), 
        KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuShow();
            }
        };
                
        reduceact = new ControlAction("Reduce expression", new ImageIcon(getClass().getResource("/images/reduce-icon.png")),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuReduce();
            }
        };
        
        simplifyact = new ControlAction("Simplify expression", new ImageIcon(getClass().getResource("/images/simplify-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0)) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuSimplify();
            }
        };    
        
        
        /* ----- SETTING ACTIONS ----- */
                
        smallfontact = new ControlAction("Small", new ImageIcon(getClass().getResource("/images/smallfont-icon.png")), null, false) {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                fontsize = FontSize.SMALL;
                if (listener != null) 
                    listener.menuFontSize(FontSize.SMALL);
            }
        };
        
        mediumfontact = new ControlAction("Medium", new ImageIcon(getClass().getResource("/images/mediumfont-icon.png")), null, true) {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                fontsize = FontSize.MEDIUM;
                if (listener != null) 
                    listener.menuFontSize(FontSize.MEDIUM);
            }
        };
        
        largefontact = new ControlAction("Large", new ImageIcon(getClass().getResource("/images/largefont-icon.png")), null, false) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                fontsize = FontSize.LARGE;
                if (listener != null)
                    listener.menuFontSize(FontSize.LARGE);
            }
        };  
        
        blackcoloract = new ControlAction("Black", new ImageIcon(getClass().getResource("/images/black-icon.png")), null, true) {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                color = ColorScheme.BLACK;
                if (listener != null) 
                    listener.menuColorScheme(ColorScheme.BLACK);
            }
        };
        
        bluecoloract = new ControlAction("Blue", new ImageIcon(getClass().getResource("/images/blue-icon.png")), null, false) {
            @Override
            public void actionPerformed(ActionEvent ae) {                
                color = ColorScheme.BLUE;
                if (listener != null) 
                    listener.menuColorScheme(ColorScheme.BLUE);
            }
        };
        
        whitecoloract = new ControlAction("White", new ImageIcon(getClass().getResource("/images/white-icon.png")), null, false) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                color = ColorScheme.WHITE;
                if (listener != null)
                    listener.menuColorScheme(ColorScheme.WHITE);
            }
        };    
        
        hierexpstyleact = new ControlAction("Hieararchical", new ImageIcon(getClass().getResource("/images/hierarchical-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK), false) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                estyle = ExpStyle.HIERARCHIC;
                linexpstyleact.putValue(SELECTED_KEY, false);
                hierexpstyleact.putValue(SELECTED_KEY, true);
                if (listener != null) 
                    listener.menuExpStyle(ExpStyle.HIERARCHIC);
            }
        };
        
        linexpstyleact = new ControlAction("Linear", new ImageIcon(getClass().getResource("/images/linear-icon.png")), 
            KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK), true) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                estyle = ExpStyle.LINEAR;
                hierexpstyleact.putValue(SELECTED_KEY, false);
                linexpstyleact.putValue(SELECTED_KEY, true);
                if (listener != null)
                    listener.menuExpStyle(ExpStyle.LINEAR);
            }
        };    
        
        redlistact = new ControlAction("Reduction list", null, null, false) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuVisibleRedList((boolean)redlistact.getValue(SELECTED_KEY));
            }
        };
        
        toolbaract = new ControlAction("Toolbar", null, null, true) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                toolbar.setVisible((boolean)toolbaract.getValue(SELECTED_KEY));
            }
        };
        
        soundsact = new ControlAction("Sounds", null, null, true) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuSoundOn((boolean)soundsact.getValue(SELECTED_KEY));
            }
        };    
        

        /* ----- HELP ACTIONS ----- */        
        
        howtoact = new ControlAction("How to", new ImageIcon(getClass().getResource("/images/help-icon.png")), KeyStroke.getKeyStroke("F1")) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuHelp();
            }
        };
        
        aboutact = new ControlAction("About", new ImageIcon(getClass().getResource("/images/about-icon.png")), null) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (listener != null)
                    listener.menuAbout();
            }
        };        
    }
    
    /**
     * Creates menubar coprised of 4 main menus - file, control, settings and help
     * with usage of cotrol actions.
     * @param eactions list of export actions
     */
    private void createMenu(List<ExportAction> eactions) {
        
        menu = new JMenuBar();
        
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.add(createMenuItem(newact, KeyEvent.VK_N));
        menuFile.addSeparator();
        menuFile.add(createMenuItem(openact, KeyEvent.VK_O));
        menuFile.add(createMenuItem(saveact, KeyEvent.VK_S));
        menuFile.add(createMenuItem(saveasact, KeyEvent.VK_A));
        menuFile.addSeparator();
        menuFile.add(createMenuItem(importenvact, KeyEvent.VK_I));
        menuFile.add(createMenuItem(exportenvact, KeyEvent.VK_E));
        if (eactions.size() > 0) {
            menuFile.add(createExportMenu(eactions));
        }
        menuFile.addSeparator();
        menuFile.add(createMenuItem(exitact, KeyEvent.VK_X));       
        menu.add(menuFile);
        
        JMenu menuControl = new JMenu("Control");
        menuControl.setMnemonic(KeyEvent.VK_C);
        menuControl.add(createMenuItem(clearact, KeyEvent.VK_C));
        menuControl.add(createMenuItem(visualizeact, KeyEvent.VK_V));
        menuControl.add(createMenuItem(reduceact, KeyEvent.VK_R));
        menuControl.add(createMenuItem(simplifyact, KeyEvent.VK_S));
        menu.add(menuControl);

        JMenu menuSettings = new JMenu("Settings");
        menuSettings.setMnemonic(KeyEvent.VK_S);
        menuSettings.add(createRadioSubMenu("Font size", new ControlAction[] {smallfontact, mediumfontact, largefontact}));
        menuSettings.add(createRadioSubMenu("Console color", new ControlAction[] {blackcoloract, bluecoloract, whitecoloract}));        
        menuSettings.add(createRadioSubMenu("Graph style", new ControlAction[] {hierexpstyleact, linexpstyleact}));
        menuSettings.addSeparator();
        menuSettings.add(new JCheckBoxMenuItem(redlistact));
        menuSettings.add(new JCheckBoxMenuItem(toolbaract));
        menuSettings.addSeparator();
        menuSettings.add(new JCheckBoxMenuItem(soundsact));
        menu.add(menuSettings);        
        
        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuHelp.add(createMenuItem(howtoact, KeyEvent.VK_H));
        menuHelp.add(createMenuItem(aboutact, KeyEvent.VK_A));
        menu.add(menuHelp);
        
    }
    
    /**
     * Creates simple menu item using the specified action and mnemonic.
     * @param a control action
     * @param m mnemonic
     * @return created menu item
     */
    private JMenuItem createMenuItem(ControlAction a, int m) {
        JMenuItem item = new JMenuItem(a);
        item.setMnemonic(m);
        return item;
    }
    
    /**
     * Creates export menu containing items which upon performed action
     * invokes listener method for export. This method takes as a parameter
     * expoct action object.
     * @param eactions list of export actions
     * @return created menu
     */
    private JMenu createExportMenu(List<ExportAction> eactions) {
        JMenu itemExport = new JMenu("Export graph as");
        for (ExportAction ea : eactions) {
            JMenuItem exportSubItem = new JMenuItem(ea.getName());
            exportSubItem.addActionListener((ActionEvent e) -> {
                if (listener != null) {
                    listener.menuExport(ea);
                }
            });
            itemExport.add(exportSubItem);
        }
        return itemExport;
    }
    
    /**
     * Creates radio submenu containing items specified by list of actions.
     * @param str menu title
     * @param actionlist list of control actions
     * @return created menu
     */
    private JMenu createRadioSubMenu(String str, ControlAction[] actionlist) {
        
        JMenu submenu = new JMenu(str);        
        ButtonGroup menuGroup = new ButtonGroup();
        
        for (ControlAction action : actionlist) {
            JMenuItem item = new JRadioButtonMenuItem(action);
            menuGroup.add(item);
            submenu.add(item);
        }
        
        return submenu;
    }

    /**
     * Creates tool bar with usage of some control actions. Toolbar contains
     * only basic subset of actions which can be performed from menu.
     */
    private void createToolBar() {
        
        toolbar = new JToolBar();
        toolbar.add(createToolBarButton(newact));
        toolbar.add(createToolBarButton(openact));
        toolbar.add(createToolBarButton(saveact));
        toolbar.add(createToolBarButton(saveasact));
        toolbar.addSeparator();
        toolbar.add(createToolBarButton(clearact));
        toolbar.add(createToolBarButton(visualizeact));
        toolbar.add(createToolBarButton(reduceact));
        toolbar.add(createToolBarButton(simplifyact));
        toolbar.addSeparator();
        toolbar.add(createToolBarButton(hierexpstyleact));
        toolbar.add(createToolBarButton(linexpstyleact));
        toolbar.addSeparator();
        toolbar.add(createToolBarButton(howtoact));
        
    }    
    
    /**
     * Creates toolbar button which contains only icon. Text is displayed
     * as tooltip instead.
     * @param action
     * @return 
     */
    private JButton createToolBarButton(ControlAction action) {
        JButton button = new JButton(action); 
        button.setText("");
        button.setToolTipText((String) action.getValue(NAME));
        return button;
    }
    
    /**
     * Returns application menubar.
     * @return menubar component
     */
    public JMenuBar getMenu() {
        return menu;
    }
    
    /**
     * Returns application toolbar.
     * @return toolbar component
     */
    public JToolBar getToolBar() {
        return toolbar;
    }
    
    /**
     * Adds control listener.
     * @param l listener to be added
     */
    public void addListener(ControlListener l) {
        listener = l;
    }
    
    /**
     * Returns selected color shceme option.
     * @return color scheme
     */
    public ColorScheme getColorScheme() {
        return color;
    }
    
    /**
     * Returns selected font size option.
     * @return font size
     */
    public FontSize getFontSize() {
        return fontsize;
    }
    
    /**
     * Returns selected expand style option.
     * @return expand style
     */
    public ExpStyle getExpStyle() {
        return estyle;
    }
    
    /**
     * Sets the select value of action which maintains showing/hiding reduction list.
     * @param b true if reduction list checkbox is selected
     */
    public void setRedList(boolean b) {
        redlistact.putValue(SELECTED_KEY, b);
    }

}
