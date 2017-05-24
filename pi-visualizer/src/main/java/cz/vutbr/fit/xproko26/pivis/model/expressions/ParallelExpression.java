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
package cz.vutbr.fit.xproko26.pivis.model.expressions;

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Class representing parallel composition as pi-calculus expression.
 * @author Dagmar Prokopova
 */
public class ParallelExpression extends ExpressionList {
      
    private static final long serialVersionUID = 1L;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */    
    public ParallelExpression(Expression p) {
        super(p);
    }
    
    /**
     * {@inheritDoc}
     */          
    @Override
    public String toString() {
        String par = getSuccExps().stream().map(e -> e.toString()).collect(Collectors.joining(" | "));
        
        List<Expression> parlist = getParentList();
        for (Expression ex : parlist) {
            if (ex.isStringVisible()) {
                if (ex instanceof SumExpression) {
                    return par;
                }
                return "(" + par + ")";
            }
        }
        return par; 
    }
    
    /**
     * {@inheritDoc}
     */          
    @Override
    public String toStringDebug() {
        String par = getSuccExps().stream().map(e -> e.toStringDebug()).collect(Collectors.joining(" | "));
        List<Expression> parlist = getParentList();
        for (Expression ex : parlist) {
            if (ex.isStringVisible()) {
                if (ex instanceof SumExpression) {
                    return par;
                }
                return "(" + par + ")";
            }
        }
        return par; 
    }

    /**
     * {@inheritDoc}
     */      
    @Override
    public ParallelExpression copy(Expression par) {
        ParallelExpression copy = new ParallelExpression(par);
        copy.setSuccExps(getSuccExps().stream().map(e -> e.copy(copy)).collect(Collectors.toList()));
        return copy;
    }
}
