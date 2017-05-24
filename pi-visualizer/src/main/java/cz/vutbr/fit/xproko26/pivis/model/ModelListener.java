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

import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;

/**
 * Interface containing methods for reporting changes in Model class.
 * @author Dagmar Prokopova
 */
public interface ModelListener {
    
    /**
     * Indicates change of visualized expression.
     * @param exp current expression
     */
    public void expressionModified(Expression exp);
    
    /**
     * Indicates change of reduction list.
     * @param list list of reductions
     * @param index index of selected reduction
     */
    public void redListModified(List<Reduction> list, int index);
    
    /**
     * Indicates change of process list.
     * @param list current process list
     */
    public void procListModified(ProcessList list);
    
    /**
     * Indicates change of selected reduction.
     * @param index index of selected reduction in reduction list
     */
    public void reductionModified(int index);
    
    /**
     * Indicates data initialization.
     */
    public void initialized();
}
