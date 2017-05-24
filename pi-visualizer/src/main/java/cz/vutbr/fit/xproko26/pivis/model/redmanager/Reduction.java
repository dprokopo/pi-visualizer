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

import java.io.Serializable;
import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;

/**
 * Abstract class which comprises expressions of pi-calculus that can 
 * participate in a reduction action.
 * @author Dagmar Prokopova
 */
public abstract class Reduction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Compares two reductions and returns true if they are equal (which means
     * their expressions are equal).
     * @param red reduction passed for comparison
     * @return true if reductions are equal
     */
    public boolean equals(Reduction red) {
        //get lists of expressions
        List<Expression> exps1 = getExpressions();
        List<Expression> exps2 = red.getExpressions();
        
        //compare coresponding expressions from the lists
        if (exps1.size() == exps2.size()) {
            for (int i=0; i < exps1.size(); i++) {
                if (exps1.get(i) != exps2.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Returns list of expressions comprised in the reduction.
     * @return list of expressions
     */
    public abstract List<Expression> getExpressions();
    
    /**
     * Returns complementary expression from the reduction to the expression
     * specified as an argument. If there is none complement returns null.
     * @param exp expression which complement should be returned
     * @return complementary expression
     */
    public abstract Expression getComplement(Expression exp);
    
    /**
     * Returns true if reduction is complete.
     * @return true if complete
     */
    public abstract boolean isComplete();
    
    /**
     * Creates copy of the reduction.
     * @return copied reduction
     */
    public abstract Reduction copy();

    
    


}
