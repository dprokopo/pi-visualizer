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
package cz.vutbr.fit.xproko26.pivis.model;

import java.io.Serializable;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.names.NameTable;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;

/**
 * Data class contains data from which the application state can be completely 
 * restored.
 * @author Dagmar Prokopova
 */
public class Data implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    //table with name values necessary for name references
    private final NameTable nametable;
    //list of defined processes
    private final ProcessList proclist;
    //visualized expression
    private Expression expression;
    //selected reduction
    private Reduction reduction;    
    
    /**
     * Data constructor creates empty process list and empty name table
     */
    public Data() {
        proclist = new ProcessList();
        nametable = new NameTable();
    }    
    
    /**
     * Sets new visualized expression
     * @param e visualized expression
     */
    public void setExpression(Expression e) {
        expression = e;
    }
    
    /**
     * Returns visualized expression
     * @return visualized expression
     */
    public Expression getExpression() {
        return expression;
    }
    
    /**
     * Sets new reduction
     * @param r reduction
     */
    public void setReduction(Reduction r) {
        reduction = r;
    }
    
    /**
     * Returns reduction
     * @return reduction
     */
    public Reduction getReduction() {
        return reduction;
    }
    
    /**
     * Removes all process definitions from process list
     */
    public void clearProcList() {
        proclist.clear();
    }
    
    /**
     * Returns process list containing process definitions
     * @return process list
     */
    public ProcessList getProcList() {
        return proclist;
    }
    
    /**
     * Adds new process definition into process list and returns old one
     * @param proc new process definition
     * @return old process definition of specified process
     */
    public ProcessDefinition addProcDef(ProcessDefinition proc) {
        return proclist.add(proc);
    }
    
    /**
     * Returns abstraction expression of specified process
     * @param id process identifier
     * @return abstraction expression of the process
     */
    public Expression getProcess(String id) {
        return proclist.get(id);
    }
        
    /**
     * Returns name table
     * @return name table
     */
    public NameTable getNameTable() {
        return nametable;
    }
    

}
