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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;

/**
 * Classs which represents storage of all processes definitions.
 * @author Dagmar Prokopova
 */
public class ProcessList extends HashMap<String, Expression> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Adds new process definition into the list and returns old process definition.
     * @param proc new process definition
     * @return replaced process definition
     */
    public ProcessDefinition add(ProcessDefinition proc) {
        ProcessDefinition oldprocdef = null;
        Expression oldexp = get(proc.getID());
        if (oldexp != null) {
            oldprocdef = new ProcessDefinition(proc.getID(), oldexp);
        }
        put(proc.getID(), proc.getExpression());
        return oldprocdef;
    }        
    
    /**
     * Returns iterator containing all defined process identifiers.
     * @return iterator with process identifiers
     */
    public Iterator<String> getProcessIterator() {
        return keySet().iterator();
    }
    
    /**
     * Returns specified process definition as simple unformated text string
     * in case that proc is specified. If procId is null, returns all process
     * definitions from the list separated by newline.
     * @param procId process identifier
     * @return textual representation of specified process or all process definitions
     */
    public String getString(String procId) {
        String ret = "";
        
        //create set of process identifiers
        Set set;
        if (procId == null) {
            set = keySet(); //fill the set with all available identifiers
        } else {
            set = new HashSet();
            set.add(procId); //fill the set with specified identifier
        }        
        
        Iterator<String> ids = set.iterator();    
        while(ids.hasNext()) {
            String id = ids.next();
            AbstractionExpression exp = (AbstractionExpression) get(id);
            if (exp == null) {
                ret = "Process " + id + " is not defined.";
                return ret;
            }
            
            ret += "agent " + id;
            if (!exp.getParams().isEmpty()) {
                ret += "(" + exp.getParams().toString() + ")";
            }
            ret += " = ";
            ret += exp.toString();

            if (ids.hasNext()) {
                ret += System.lineSeparator();
            }
        }
        return ret;
    }
}
