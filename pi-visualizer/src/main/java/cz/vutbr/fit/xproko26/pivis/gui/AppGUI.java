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

import cz.vutbr.fit.xproko26.pivis.gui.control.Control;
import cz.vutbr.fit.xproko26.pivis.gui.control.ControlListener;
import cz.vutbr.fit.xproko26.pivis.gui.console.Console;
import cz.vutbr.fit.xproko26.pivis.gui.console.ConsoleListener;
import cz.vutbr.fit.xproko26.pivis.gui.graph.GraphManager;
import cz.vutbr.fit.xproko26.pivis.gui.graph.GraphManagerListener;
import cz.vutbr.fit.xproko26.pivis.gui.graph.graphlib.ExportAction;
import cz.vutbr.fit.xproko26.pivis.gui.textline.TextLine;
import cz.vutbr.fit.xproko26.pivis.gui.redpanel.ReductionTableModel;
import cz.vutbr.fit.xproko26.pivis.gui.redpanel.ReductionPanel;
import cz.vutbr.fit.xproko26.pivis.gui.redpanel.ReductionPanelListener;
import cz.vutbr.fit.xproko26.pivis.gui.enveditor.EnvironmentEditor;
import cz.vutbr.fit.xproko26.pivis.gui.enveditor.EnvironmentEditorListener;
import cz.vutbr.fit.xproko26.pivis.gui.help.AboutFrame;
import cz.vutbr.fit.xproko26.pivis.gui.help.HelpFrame;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;
import cz.vutbr.fit.xproko26.pivis.model.ProcessList;
import cz.vutbr.fit.xproko26.pivis.formater.TextFormater;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * AppGUI is a singleton class which comprises all visual components of the application.
 * It collects actions performed by the user and sends them to controller for processing.
 * It also receives commands from the controller to set or change state of specific
 * components.
 * @author Dagmar Prokopova
 */
public class AppGUI {
    
    //singleton instance of AppGUI class
    private static AppGUI instance;
    
    //reference to gui listener
    private static GUIListener listener;
    
    //graph manager
    private static GraphManager gmanager;
    
    //control elements (menu and toolbar)
    private static Control control;
    
    //console
    private static Console console;
    
    //textline
    private static TextLine textline;
    
    //environment editor
    private static EnvironmentEditor enveditor;
            
    //reduction panel
    private static ReductionPanel redpanel;
    
    //main frame
    private static MainFrame mframe;  
    
    //help frame
    private static HelpFrame helpframe;
    
    //about frame
    private static AboutFrame aboutframe;
    
    /**
     * Private constructor which creates graph manager and all gui 
     * components and sets them into the main frame. Apart from main frame
     * creates also help frame and about frame.
     */
    private AppGUI() {
        
        //create graph manager
        gmanager = GraphManager.getInstance();
        
        //create gui entities
        control = new Control(gmanager.getExportFormats());        
        console = new Console();
        textline = new TextLine();
        enveditor = new EnvironmentEditor();
        redpanel = new ReductionPanel();      
        
        //create main frame and fill with components
        mframe = new MainFrame();       
        mframe.setControl(control.getMenu(), control.getToolBar());
        mframe.setContent(console, enveditor, gmanager.getGraphCanvas(), textline, redpanel);
        mframe.setTabPaneName(0, "Console");
        mframe.setTabPaneName(1, "Environment");
        //set focus to console
        mframe.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                console.setFocus();                
            }
        });
        
        //create help frame
        helpframe = new HelpFrame();        
        //create about frame
        aboutframe = new AboutFrame();              
    }     
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of AppGUI class
     */
    public static AppGUI getInstance() {
        if(instance == null) {
            instance = new AppGUI();
        }
        return instance;
    }
    
    /**
     * Initializes all gui components and makes main frame visible.
     */
    public void init() {                
        gmanager.init();
        console.init();
        textline.init();
        enveditor.init();
        redpanel.init();
        mframe.setVisible(true);        
    }
    
    /**
     * Adds gui listener and sets component listeners.
     * @param list listener to be added
     */
    public void addListener(GUIListener list) {                
        listener = list;        
        
        //set inner listeners
        setGraphManagerListener();
        setControlListener();
        setConsoleListener();        
        setReductionPanelListener();
        setEnvironmentListener();  
    }
    
    /* ------------ DIALOG WINDOWS ------------- */
    
    /**
     * Shows dialog window in which the user can set expression 
     * for visualization. Returns string containing the textual representation
     * of the expression.
     * @return text representation of expression 
     */
    private String visualizeDialog() {

        return (String) JOptionPane.showInputDialog (
                mframe,
                "Expression to be visualized:\n",
                "Show",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
    }
    
    /**
     * Shows dialog window which asks for user confirmation to discard current
     * state of the application. Returns 0 if user agrees.
     * @return 0 if positive answer was given
     */
    public int confirmNew() {

        Object[] options = {"Yes", "Cancel"};        
        return JOptionPane.showOptionDialog (
                mframe,
                "All unsaved changes will be lost. Do you want to continue?",
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );        
    }
    
    /**
     * Shows dialog window with error message.
     * @param msg error message to be presented
     */
    public void showError(String msg) {
        JOptionPane.showMessageDialog (
            mframe,
            msg,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    
    /* ---------- CONSOLE ------------ */
    
    /**
     * Writes help to console.
     */
    public void writeConsoleHelp() {
        console.write(TextResource.getInstance().getString("consolehelp"));
    }
    
    /**
     * Writes specified string to console.
     * @param str string to be written
     */
    public void writeConsole(String str) {
        String nl = str.equals("") ? "" : "\n";
        console.write(str + nl);
    }
    
    /**
     * Clears console.
     */
    public void clearConsole() {
        console.clear();
    }       
    
    
    /* ------------ REDUCTION PANEL ------------ */
    
    /**
     * Updates reduction list with new data.
     * @param list new list of reductions
     */
    public void updateReductionList(List<Reduction> list) {
        redpanel.fill(new ReductionTableModel(list));
    }
    
    /**
     * Sets selected record in reduction list.
     * @param i index into the reduction list
     */
    public void setReductionListIndex(int i) {
        redpanel.setSelection(i);
    }

    /**
     * Makes reduction list hidden.
     */
    public void hideReductionList() {
        control.setRedList(false);
        redpanel.setVisible(false);
        mframe.reload();        
    }
    
    /**
     * Makes reduction list visible.
     */
    public void showReductionList() {    
        control.setRedList(true);
        redpanel.setVisible(true);
        mframe.showLeftPanel(redpanel.getPreferredSize().getWidth());
        mframe.reload();
    }
    
    
    /* ----------- HELP FRAME ----------- */
    
    /**
     * Makes help frame hidden.
     */
    public void hideHelpFrame() {
        helpframe.setVisible(false);
    }
    
    /**
     * Makes help frame visible.
     */
    public void showHelpFrame() {
        helpframe.setVisible(true);
    }
    
    
    /* ------------- ABOUT FRAME ----------- */
    
    /**
     * Makes about frame hidden.
     */
    public void hideAboutFrame() {
        aboutframe.setVisible(false);
    }
    
    /**
     * Makes about frame visible.
     */
    public void showAboutFrame() {
        aboutframe.setVisible(true);
    }

    
    /* ----------- TEXTLINE ------------- */
    
    /**
     * Set textual representation of visualized expression into text line.
     * @param exp visualized expression
     */
    public void setExpressionLine(Expression exp) {
        //use text formater to create formated string
        textline.setText(TextFormater.getInstance().getString(exp));
    }
    
    
    /* ---------- ENVIRONMENT EDITOR -------------- */
    
    /**
     * Sets textual representation of process list containing all process definitions.
     * @param proclist process list
     */
    public void setEnvironment(ProcessList proclist) {
        enveditor.setText(TextFormater.getInstance().getString(proclist));
    }
    
    
    /* ----------- GRAPH MANAGER ----------- */
        
    /**
     * Visualizes specified expression in a form of graph.
     * @param exp expression to be visualized
     * @param animation flag indicating if execution of the layout of the graph should be animated
     */
    public void drawGraph(Expression exp, boolean animation) {
        gmanager.drawGraph(exp, control.getExpStyle() == Control.ExpStyle.HIERARCHIC, animation);
    }

    /**
     * Removes both reduction selection and suggestions if any in specified 
     * expression tree.
     * @param exp root of the expression tree
     */
    public void removeRedSelection(Expression exp) {
        gmanager.removeRedSelection(exp);
    }
    
    /**
     * Prepares graph for reduction selection so that all expressions
     * from the specified list are visible. In order to achieve it, it might 
     * expand nodes as well as visualize helper replication branches.
     * @param list list of expressions which have to be visible
     */
    public void prepareForRedSelection(List<Expression> list) {
        gmanager.prepareForRedSelection(list);
    }
    
    /**
     * Visualizes reduction selection - all nodes coresponding to any
     * expression from the specified list are highlighted.
     * @param list list of expressions which visual nodes should be hightlighted
     */
    public void visualizeRedSelection(List<Expression> list) {
        gmanager.visualizeRedSelection(list);
    }
    
    /**
     * Visualizes reduction suggestions - all nodes corresponding to any
     * expression from the specified list are hightlighted
     * @param list list of expressions which visual nodse should be hightlighted
     */
    public void visualizeSuggestions(List<Expression> list) {
        gmanager.visualizeSuggestions(list);
    }
    
    /**
     * Visualizes replication helper branch represented by expression specified 
     * as an argument.
     * @param exp replication helper expression to be visualized
     */
    public void visualizeHelper(Expression exp) {
        gmanager.visualizeHelper(exp);
    }  
    
    
    /* ------------ SAVE/LOAD --------------- */
    
    /**
     * Creates file chooser with specified options and reports user choice.
     * @param description description of the file
     * @param extension file extension to be used
     * @return selected file or null if nothing selected
     */
    public File getOpenFile(String description, String extension) {
        JFileChooser filechooser = new JFileChooser();
        filechooser.setCurrentDirectory(new File("."));
        filechooser.setFileFilter(new FileNameExtensionFilter(description, extension, extension.toUpperCase()));
        int choice = filechooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {   
            return filechooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Creates file chooser with specified options and reports user choice.
     * If the suffix of the selected name does not contain specified
     * extension, the extension is added.
     * @param description description of the file
     * @param extension file extension to be used
     * @return selected file or null if nothing selected
     */
    public File getSaveFile(String description, String extension) {
        JFileChooser filechooser = new JFileChooser();
        filechooser.setCurrentDirectory(new File("."));
        filechooser.setFileFilter(new FileNameExtensionFilter(description, extension, extension.toUpperCase()));
        int choice = filechooser.showSaveDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION)
        {   
            //check and eventually fix extension
            File file = filechooser.getSelectedFile();
            String fname = file.getAbsolutePath();
            if(!fname.toLowerCase().endsWith("." + extension)) {
                return (new File(fname + "." + extension));
            }
            return file;
        }
        return null;
    }
    

    /***********************************************/
    /*********** PART OF THE CONTROLLER ************/
    /***********************************************/
    
    /* ---------- TOP MENU LISTENER ----------- */
    
    /**
     * Creates and sets control listener. Most of the methods
     * are redirected directly to gui listener (controller).
     */
    private void setControlListener() {
        control.addListener(new ControlListener() {
            
            @Override
            public void menuCreateNew() {
                if (listener != null)
                    listener.createNew();
            }

            @Override
            public void menuLoadFromFile() {
                if (listener != null)
                    listener.loadFromFile();
            }

            @Override
            public void menuSaveToFile() {
                if (listener != null)
                    listener.saveToFile();
            }
            
            @Override
            public void menuSaveAsToFile() {
                if (listener != null)
                    listener.saveAsToFile();
            }

            @Override
            public void menuExport(ExportAction ea) {
                File f = getSaveFile(ea.getName().toUpperCase() + " files", ea.getExtension());
                if (f != null) {
                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(f);
                        ea.export(os);
                    } catch (FileNotFoundException ex) {
                        showError("Error: Could not export into the selected file.");
                    } catch (Exception ex) {
                        showError("Error: Export of the graph failed.");
                    } finally {
                        if (os != null) {
                            try {
                                os.flush();            
                                os.close();
                                os = null;
                                System.gc();
                            } catch (IOException ex) {}
                        }
                    }
                }
            }
            
            @Override
            public void menuClearConsole() {
                console.clear();
            }

            @Override
            public void menuVisibleRedList(boolean b) {
                if (b) {
                    showReductionList();
                } else {
                    hideReductionList();
                }
            }

            @Override
            public void menuReduce() {
                if (listener != null)
                    listener.reduceRequest();
            }

            @Override
            public void menuSimplify() {
                if (listener != null)
                    listener.simplifyRequest();
            }
            
            @Override
            public void menuExit() {
                if (listener != null)
                    listener.exit();
            }

            @Override
            public void menuHelp() {
                showHelpFrame();
            }

            @Override
            public void menuAbout() {
                showAboutFrame();
            }

            @Override
            public void menuFontSize(Control.FontSize fs) {

                switch (fs) {
                    case SMALL:
                        console.setFontSize(12);
                        textline.setFontSize(12);
                        enveditor.setFontSize(12);
                        break;
                    case MEDIUM:
                        console.setFontSize(18);
                        textline.setFontSize(18);
                        enveditor.setFontSize(18);
                        break;
                    case LARGE:
                        console.setFontSize(24);
                        textline.setFontSize(24);
                        enveditor.setFontSize(24);
                        break;
                }
            }
            
            @Override
            public void menuColorScheme(Control.ColorScheme cs) {

                switch (cs) {
                    case BLACK:
                        console.setColor(Color.BLACK, Color.WHITE);
                        break;
                    case BLUE:
                        console.setColor(new Color(55,55,180), Color.WHITE);
                        break;
                    case WHITE:
                        console.setColor(new Color(250,250,250), Color.BLACK);
                        break;
                }
            }

            @Override
            public void menuSoundOn(boolean b) {
                AudioPlayer.getInstance().setSoundOn(b);
            }

            @Override
            public void menuExpStyle(Control.ExpStyle es) {
                if (listener != null)
                    listener.expandStyleChanged();
            }            

            @Override
            public void menuLoadEnvironment() {
                if (listener != null)
                    listener.loadEnvironment();
            }

            @Override
            public void menuSaveEnvironment() {
                if (listener != null)
                    listener.saveEnvironment();
            }

            @Override
            public void menuShow() {
                String exp = visualizeDialog();
                if ((listener != null) && (exp != null) && (exp.length() > 0)) {
                    listener.showExpression(exp);
                }
            }
        });
    }    
    
    
    /* ------------ CONSOLE LISTENER ----------- */
    
    
    /**
     * Creates and sets console listener. It contaion single method for text
     * processing which is passed to gui listener (controler).
     */
    private void setConsoleListener() {
        console.addListener(new ConsoleListener() {
            @Override
            public void consoleInput(String cmd) {
                if (listener != null)
                    listener.processTextInput(cmd);
            }
        });
    }
    
    
    /* ------------ GRAPH MANAGER LISTENER ------------ */
    
    /**
     * Creates and sets graph manager listener. All methods are just passed
     * to gui listener (controller).
     */
    private void setGraphManagerListener() {
        gmanager.addListener(new GraphManagerListener() {

            @Override
            public boolean isSelectableForReduction(Expression exp) {
                if (listener != null) {
                    return listener.isSelectableForReduction(exp);
                }
                return false;
            }

            @Override
            public void nodeSelectedForReduction(Expression exp) {
                if (listener != null) {
                    listener.nodeSelectedForReduction(exp);
                }
            }

            @Override
            public void nodeDeselectedFromReduction(Expression exp) {
                if (listener != null) {
                    listener.nodeDeselectedFromReduction(exp);
                }
            }

            @Override
            public Expression replicationRequested(Expression exp) {
                if (listener != null) {
                    return listener.replicationRequested(exp);
                }
                return null;
            }
            
            @Override
            public void instanceVisualized() {
                if (listener != null) {
                    listener.instanceVisualized();
                }                
            }
            
            @Override
            public void helperVisualized(Expression exp) {
                if (listener != null) {
                    listener.helperVisualized(exp);
                }
            }


            @Override
            public void nodeExpanded() {
                if (listener != null) {
                    listener.nodeExpanded();
                }    
            }

            @Override
            public void nodeCollapsed() {
                if (listener != null) {
                    listener.nodeCollapsed();
                }    
            }
            
            @Override
            public Expression instanceRequested(Expression exp) {
                if (listener != null) {
                    return listener.instanceRequested(exp);
                }
                return null;
            }
            
            @Override
            public boolean isProcDefined(String id, int argscount) {
                if (listener != null) {
                    return listener.isProcDefined(id, argscount);
                }
                return false;
            }

            @Override
            public void selectionChanged() {
                if (listener != null) {
                    listener.selectionChanged();
                } 
            }

        });
    }
       
    
    /* ------------ REDUCTION PANEL LISTENER ----------- */
    
    /**
     * Creates and sets reduction panel listener. Most of the methods are 
     * just passed to gui listener (controller).
     */
    private void setReductionPanelListener() {
        redpanel.addListener(new ReductionPanelListener() {
            @Override
            public void reductionSelected(Reduction r) {
                if (listener != null)
                    listener.reductionSelected(r);
            }

            @Override
            public void clearButtonClicked() {
                if (listener != null)
                    listener.reductionCleared();
            }

            @Override
            public void reduceButtonClicked() {
                if (listener != null)
                    listener.reduceRequest();
            }

            @Override
            public void closeReductionList() {
                hideReductionList();                
            }
        
        });
    }       
    
    /* ----------- ENVIRONMENT LISTENER ------------------ */
    
    /**
     * Creates and sets environment panel listener. Most of the methods are 
     * just passed to gui listener (controller)
     */
    private void setEnvironmentListener() {
        enveditor.addListener(new EnvironmentEditorListener() {

            @Override
            public void commitProcDefs(String lines) {
                if (listener != null)
                    listener.setEnvironment(lines);
            }

            @Override
            public void loadProcDefs() {
                if (listener != null)
                    listener.loadEnvironment();
            }

            @Override
            public void saveProcDefs() {
                if (listener != null)
                    listener.saveEnvironment();
            }

            @Override
            public void editEnabled() {
                //change title of environment tab
                mframe.setTabPaneName(1, "Environment (edit)");                
            }

            @Override
            public void editDisabled() {
                //change title of environment tab
                mframe.setTabPaneName(1, "Environment");  
            }
            
        });
    }

}
