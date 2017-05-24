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
package cz.vutbr.fit.xproko26.pivis.model.redmanager;

import java.util.ArrayList;
import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;

/**
 * ActionList is a structure which sores available input and output prefixes
 * in separate lists. It is used by reduction manager to report available
 * reduction actions from lower nodes of expression tree to ancestors and for
 * extracting reductions at parallel nodes.
 * @author Dagmar Prokopova
 */
public class ActionList {
    
    //list of input prefix expressions
    private final List<InPrefixExpression> inlist;        
    
    //list of output prefix expressions
    private final List<OutPrefixExpression> outlist;
    
    /**
     * Constructor which initializes input and output lists.
     */
    public ActionList() {
        inlist = new ArrayList<>();
        outlist = new ArrayList<>();
    }
    
    /**
     * Returns list of input prefix expressions.
     * @return input list
     */
    public List<InPrefixExpression> getInList() {
        return inlist;
    }
    
    /**
     * Returns list of output prefix expressions.
     * @return output list
     */
    public List<OutPrefixExpression> getOutList() {
        return outlist;
    }
    
    /**
     * Adds new input prefix expression into the input list
     * @param in input prefix expression to be added
     */
    public void add(InPrefixExpression in) {
        inlist.add(in);
    }
    
    /**
     * Adds new output prefix expression into the output list.
     * @param out output prefix expression to be added
     */
    public void add(OutPrefixExpression out) {
        outlist.add(out);
    }
    
    /**
     * Adds all items from the passed action list.
     * @param al action list which items should be added
     */
    public void addAll(ActionList al) {
        inlist.addAll(al.getInList());
        outlist.addAll(al.getOutList());
    }

}
