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
package cz.vutbr.fit.xproko26.pivis.gui.graph;

import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;

/**
 * GraphManagerListener contains methods mostly for reporting user interactions
 * with graph to application gui and controller. Some methods are used to
 * inform controller about finished visualization actions (i.e. visualized instance) 
 * or to get crucial information from model (i.e. whether process was defined).
 * @author Dagmar Prokopova
 */
public interface GraphManagerListener {      

    /**
     * Reports that node selection was changed.
     */
    public void selectionChanged();
    
    /**
     * Reports that node (expression) was selected for reduction.
     * @param exp selected expression
     */
    public void nodeSelectedForReduction(Expression exp);
    
    /**
     * Reports that node (expression) was deselected from reduction
     * @param exp deselected expression
     */
    public void nodeDeselectedFromReduction(Expression exp);
    
    /**
     * Reports requeted for node replication
     * @param exp expression to be replicated
     * @return replicated expression (copy of exp)
     */
    public Expression replicationRequested(Expression exp);    
    
    /**
     * Reports request for expression instantiation
     * @param exp expression to be instantiated
     * @return instance of the expression
     */
    public Expression instanceRequested(Expression exp);
    
    /**
     * Reports the fact that instace was visualized.
     */
    public void instanceVisualized();
    
    /**
     * Reports the fact that replication helper branch was visualized.
     * @param exp visualized replication helper expression
     */
    public void helperVisualized(Expression exp);
    
    /**
     * Reports node expansion.
     */
    public void nodeExpanded();
    
    /**
     * Reports node collapsion.
     */
    public void nodeCollapsed(); 
        
    /**
     * Returns true if specified process with specified number of arguments was
     * defined
     * @param id process identifier
     * @param argscout number of arguments
     * @return true if process was defined
     */
    public boolean isProcDefined(String id, int argscout);
    
    /**
     * Returns true if the expression is reducible and thus it make sense
     * to select it for reduction.
     * @param exp examined expression
     * @return true if expression is reducible
     */
    public boolean isSelectableForReduction(Expression exp);
}
