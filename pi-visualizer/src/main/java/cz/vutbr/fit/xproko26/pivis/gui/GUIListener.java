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

import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;

/**
 * GUIListener contains methods mostly for reporting user actions performed
 * on some of the gui components to controller.
 * @author Dagmar Prokopova
 */
public interface GUIListener {
    
    //----------------- CONTROL MENU ----------------------
    
    /**
     * Reports that reset of the application state was requested by user action.
     */
    public void createNew();
    
    /**
     * Reports that load of the file was requested by user action.
     */
    public void loadFromFile();
    
    /**
     * Reports that immediate save of the file was requested by user action.
     */
    public void saveToFile();
    
    /**
     * Reports that save with file specification was requested by user action.
     */
    public void saveAsToFile();
    
    /**
     * Reports that visualization of specified expression was requested 
     * by user action
     * @param exp textual representation of expression to be visualized
     */
    public void showExpression(String exp);
    
    /**
     * Reports that exit of the application was requested by user action.
     */
    public void exit();
    
    /**
     * Reports that reduction of the expression was requested by user action.
     */
    public void reduceRequest();
    
    /**
     * Reports that simplification of the expression was requested by user action.
     */
    public void simplifyRequest();
    
    /**
     * Reports that expansion style of the graph was changed by user action.
     */
    public void expandStyleChanged();

    
    //----------------- CONSOLE ----------------------
    
     /**
     * Reports that there was a user input performed into the console which should
     * be processed.
     * @param cmd command to be parsed
     */
    public void processTextInput(String cmd);
    
    
    //----------------- ENVIRONMENT PANEL ----------------------
    
    /**
     * Reports that user modified and commited process definitions in 
     * environment editor
     * @param lines text string containing set of process definitions
     */
    public void setEnvironment(String lines);
    
    /**
     * Reports that load of the environment from file was requested by user action.
     */
    public void loadEnvironment();
    
    /**
     * Reports that export of the environment into the file was requested 
     * by user action.
     */
    public void saveEnvironment();
    
    
    //----------------- GRAPH CANVAS ----------------------
    
    /**
     * Returns true if specified expression can be selected for reduction.
     * @param exp expression to be examined
     * @return true if reduction is possible for specified expression
     */
    public boolean isSelectableForReduction(Expression exp);
    
    /**
     * Reports that specified expression was selected for reduction by user action.
     * @param exp expression selected for reduction
     */
    public void nodeSelectedForReduction(Expression exp);
    
    /**
     * Reports that specified expression was deselected from reduction by user
     * action.
     * @param exp expression deselected from reduction
     */
    public void nodeDeselectedFromReduction(Expression exp);
    
    /**
     * Reports that whole reduction was selected from reduction list by user action.
     * @param r seleted reduction
     */
    public void reductionSelected(Reduction r);
    
    /**
     * Reports that the reduction was cleared by user action.
     */
    public void reductionCleared();
    
    /**
     * Reports that the simple selection of node was changed by user action.
     */
    public void selectionChanged();
    
    /**
     * Reports that the replication of specified expression was requested 
     * by user action.
     * @param exp expression which was set for replication
     * @return replicated expression (subtree)
     */
    public Expression replicationRequested(Expression exp);
    
    /**
     * Reports that the instanciace of specified expression was requested
     * by user action.
     * @param exp expression which was set for instanciation
     * @return instance of the expression
     */
    public Expression instanceRequested(Expression exp);
    
    /**
     * Reports that the instance was visualized.
     */
    public void instanceVisualized();
    
    /**
     * Reports that the replication helper was visualized.
     * @param exp 
     */
    public void helperVisualized(Expression exp);
    
    /**
     * Reports that the node in the graph was expanded.
     */
    public void nodeExpanded();
    
    /**
     * Reports that the node in the graph was collapsed.
     */
    public void nodeCollapsed();

    /**
     * Returns true if specified process with speccified number of arguments
     * was already defined and thus is present in process list.
     * @param id process identifier
     * @param args arguments
     * @return true if process definition exists
     */
    public boolean isProcDefined(String id, NRList args);
    

}
