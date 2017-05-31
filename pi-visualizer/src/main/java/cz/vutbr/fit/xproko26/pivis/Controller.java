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
package cz.vutbr.fit.xproko26.pivis;

import cz.vutbr.fit.xproko26.pivis.gui.AppGUI;
import cz.vutbr.fit.xproko26.pivis.gui.GUIListener;

import cz.vutbr.fit.xproko26.pivis.model.Model;
import cz.vutbr.fit.xproko26.pivis.model.ModelListener;
import cz.vutbr.fit.xproko26.pivis.model.ProcessDefinition;
import cz.vutbr.fit.xproko26.pivis.model.ProcessList;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;

import cz.vutbr.fit.xproko26.pivis.parser.TextParser;
import cz.vutbr.fit.xproko26.pivis.parser.TextParserListener;

import cz.vutbr.fit.xproko26.pivis.filemanager.FileManager;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import java.io.File;
import java.util.List;

/**
 * Controller is a singleton class which implements application logic,
 * modifies model based on user interactions and sets the state of gui.
 * @author Dagmar Prokopova
 */
public class Controller {

    //controller instance
    private static Controller instance;

    //GUI as an presentation layer
    private static AppGUI gui;

    //model containing application data
    private static Model model;
    
    //file manager service for saving/loading files
    private static FileManager filemgr;
    
    //parser service for parsing text input from console or file
    private static TextParser parser;

    /**
     * Private constructor which creates GUI, model, file manager and parser
     * as the main components that controller interacts with.
     */
    private Controller() {
        gui = AppGUI.getInstance(); 
        model = Model.getInstance();   
        filemgr = FileManager.getInstance();
        parser = TextParser.getInstance();            
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of Controller class
     */
    public static Controller getInstance() {
        if(instance == null) {
            instance = new Controller();
        }
        return instance;
    }   
    
    /**
     * Sets listeners for used components and initializes model.
     */
    public void run() {
        
        setParserListener();
        setModelListener();
        setGUIListener();
        
        model.init();
    }

    /***********************************************/
    /*********** TEXT PARSER LISTENER **************/
    /***********************************************/
    
    /**
     * Creates and sets parser listener for processing various commands.
     */
    private void setParserListener() {
        parser.addListener(new TextParserListener() {

            @Override
            public void cmdExit() {
                end();
            }

            @Override
            public void cmdClear() {
                gui.clearConsole();
            }

            @Override
            public void cmdReset() {
                reset();
            }

            @Override
            public void cmdHelp() {
                gui.writeConsoleHelp();
            }

            @Override
            public void cmdAgent(ProcessDefinition procdef) {
                model.addProcDef(procdef);
            }

            @Override
            public void cmdShow(Expression expr) {
                model.setExpression(expr);
            }

            @Override
            public void cmdList() {
                gui.showReductionList();
            }

            @Override
            public void cmdReduce() {
                reduce();
            }

            @Override
            public void cmdSimplify() {
                simplify();
            }

            @Override
            public void cmdEnv(String id) {
                gui.writeConsole(model.getProcList().getString(id));
            }
        });
    }

    
    /***********************************************/
    /*************** MODEL LISTENER ****************/
    /***********************************************/
        
    /**
     * Creates and sets model listener to respond to model changes
     */
    private void setModelListener() {
        
        Model.getInstance().addListener(new ModelListener() {
            @Override
            public void redListModified(List<Reduction> list, int index) {
                gui.updateReductionList(list);
                gui.setReductionListIndex(index);
            }

            @Override
            public void procListModified(ProcessList list) {
                //redraw graph without animation - to add missing +/- buttons
                gui.drawGraph(model.getExpression(), false);
                //update environment editor
                gui.setEnvironment(list);
            }

            @Override
            public void expressionModified(Expression exp) {
                //redraw graph with animation
                gui.drawGraph(exp, true);
                //update expression line
                gui.setExpressionLine(exp);
            }

            @Override
            public void reductionModified(int index) {
                //expand and visualize graph if needed
                gui.prepareForRedSelection(model.getSelection());
                //remove old highlighting and set current one
                processSelection(model.getExpression());
                //update expression line
                gui.setExpressionLine(model.getExpression());
                //set index in reduction table
                gui.setReductionListIndex(index);
            }

            @Override
            public void initialized() {
                gui.init();
            }
        });
    }       
    

    /***********************************************/
    /***************** GUI LISTENER ****************/
    /***********************************************/
    
    /**
     * Creates and sets GUI listener to respond to user interaction
     */
    private void setGUIListener() {

        gui.addListener(new GUIListener() {

            /* ------------ console action --------------- */
            
            @Override
            public void processTextInput(String cmd) {
                try {
                    //parse console command..
                    parser.parseCommand(cmd);
                } catch (Exception ex) {
                    //..or write error message if parsing failed
                    gui.writeConsole(ex.getMessage());
                }
            }            
            
            /* ----------- control menu actions ----------- */

            @Override
            public void createNew() {
                reset();
            }

            @Override
            public void loadFromFile() {
                //if model is modified, ask for user confirmation
                if (!model.isModified() || (gui.confirmNew() == 0)) {
                    File f = gui.getOpenFile("PiVis savings", "pvs");
                    if (f != null) {
                        try {
                            model.setData(filemgr.load(f));
                        } catch (Exception ex) {
                            gui.showError(ex.getMessage());
                        }
                    }
                }
            }

            @Override
            public void saveToFile() {
                if (filemgr.isFileCached()) {
                    try {
                        filemgr.save(model.getData());
                        model.setModified(false);
                    } catch (Exception ex) {
                        saveAsToFile();
                    }
                } else {
                    saveAsToFile();
                }

            }

            @Override
            public void saveAsToFile() {
                File f = gui.getSaveFile("PiVis savings", "pvs");
                if (f != null) {
                    try {
                        filemgr.save(model.getData(), f);
                        model.setModified(false);
                    } catch (Exception ex) {
                        gui.showError(ex.getMessage());
                    }
                }
            }

            @Override
            public void showExpression(String exp) {
                try {
                    //parse show command..
                    parser.parseCommand("show " + exp);
                } catch (Exception ex) {
                    //..or show error message if parsing failed
                    gui.showError("Error: Could not parse specified expression.");
                }
            }

            @Override
            public void exit() {
                end();
            }

            @Override
            public void reduceRequest() {
                reduce();
            }

            @Override
            public void simplifyRequest() {
                simplify();
            }

            @Override
            public void expandStyleChanged() {
                gui.drawGraph(model.getExpression(), true);
            }

            
            /* -------------- graph actions -------------- */
            
            @Override
            public boolean isSelectableForReduction(Expression exp) {
                return model.isReductionSelectable(exp);
            }

            @Override
            public void nodeSelectedForReduction(Expression exp) {
                model.selectForReduction(exp);                
            }

            @Override
            public void nodeDeselectedFromReduction(Expression exp) {
                model.deselectFromReduction(exp);
            }

            @Override
            public void reductionSelected(Reduction r) {
                model.setReduction(r.copy());
            }

            @Override
            public void reductionCleared() {
                model.setReduction(null);
            }
            
            @Override
            public void selectionChanged() {
                gui.setExpressionLine(model.getExpression());
            }

            @Override
            public boolean isProcDefined(String id, NRList args) {
                try {
                    model.getProcDef(id, args);
                    return true;
                } catch (Exception e) {
                    gui.writeConsole(e.getMessage());
                    return false;
                }
            }
            
            @Override
            public Expression replicationRequested(Expression exp) {
                return model.getReplicationHelper(exp);
            }

            @Override
            public Expression instanceRequested(Expression exp) {
                try {
                    return model.getExpressionInstance(exp);
                } catch (Exception ex) {
                    gui.writeConsole(ex.getMessage());
                    return null;
                }
            }

            @Override
            public void instanceVisualized(Expression exp) {
                processSelection(exp);
            }
            
            @Override
            public void helperVisualized(Expression exp) {
                model.changeHelperToCopy(exp);
                processSelection(exp);
                gui.setExpressionLine(model.getExpression());
            }

            @Override
            public void nodeExpanded() {
                gui.setExpressionLine(model.getExpression());
            }

            @Override
            public void nodeCollapsed() {
                gui.setExpressionLine(model.getExpression());
            }


            /* ------------ environment actions ----------- */
            
            @Override
            public void setEnvironment(String lines) {
                try {
                    model.setProcDefs(parser.parseProcDefs(lines));
                } catch (Exception ex) {
                    gui.showError(ex.getMessage());
                }
            }

            @Override
            public void loadEnvironment() {
                File f = gui.getOpenFile("Text file", "txt");
                if (f != null) {
                    try {
                        model.setProcDefs(parser.parseProcDefs(filemgr.getString(f)));
                    } catch (Exception ex) {
                        gui.showError(ex.getMessage());
                    }
                }
            }

            @Override
            public void saveEnvironment() {
                File f = gui.getSaveFile("Text file", "txt");
                if (f != null) {
                    try {
                        filemgr.saveString(model.getProcList().getString(null), f);
                    } catch (Exception ex) {
                        gui.showError(ex.getMessage());
                    }
                }
            }

        });
    }

    
    /***********************************************/
    /**************** HELPER METHODS ***************/
    /***********************************************/
    
    /**
     * If there are unsaved changes in the model, asks for user permission and
     * then finishes the program.
     */
    private void end() {
        if (!model.isModified() || (gui.confirmNew() == 0)) {
            System.exit(0);
        }
    }
    
    /**
     * Calls model method to reduce expression and prints out warning to console 
     * if reduction cannot be done (i.e. nothing selected for reduction).
     */
    private void reduce() {
        try {
            model.reduce();
        } catch (Exception ex) {
            gui.writeConsole(ex.getMessage());
        }
    }
        
    /**
     * Calls model method to simplify expression and prints out warning 
     * to console if simplification cannot be done (i.e. no expression set).
     */
    private void simplify() {
        try {
            model.simplify();
        } catch (Exception ex) {
            gui.writeConsole(ex.getMessage());
        }
    }
    
    private void reset() {
        if (!model.isModified() || (gui.confirmNew() == 0)) {
            model.init();
            filemgr.init();
        }
    }
    
    /**
     * Removes old selection and suggestions and visualizes current ones.
     * @param exp expression which should be processed for selection
     */
    private void processSelection(Expression exp) {        
        gui.removeRedSelection(exp);
        gui.visualizeRedSelection(model.getSelection());
        gui.visualizeSuggestions(model.getSuggestions());
    }

}
