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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class which represents pi-calculus expression containing more
 * than one succesor.
 * @author Dagmar Prokopova
 */
public abstract class ExpressionList extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    //list of successors
    private List<Expression> exps;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public ExpressionList(Expression p) {
        super(p);
    }
    
    /**
     * Returns list of direct desecendants.
     * @return list of child expressions
     */
    public List<Expression> getSuccExps() {
        return exps;
    }
    
    /**
     * Sets list of direct descendants.
     * @param elist lisst of child expressions to be set
     */
    public void setSuccExps(List<Expression> elist) {
        exps = elist;
    }
    
    /**
     * Adds single expression into the list of direct descendants.
     * @param e expression to be add
     */
    public void addExp(Expression e) {
        if (exps == null) {
            //creates empty list if there is none yet
            exps = new ArrayList<>();
        }
        exps.add(e);
    }
    
    /**
     * Removes specified expression from the list of direct descendants.
     * @param e expression to be removed
     */
    public void removeExp(Expression e) {
        if (exps != null) {
            exps.remove(e);
        }
    }
    
    /**
     * Removes itself from the expression tree and connects its parent with
     * specified expression.
     * @param exp expression to be connected to parent
     */
    public void remove(Expression exp) {
        Expression parent = getParent();                
        if (parent != null) {
            parent.replaceSucc(this, exp);
        }        
        exp.setParent(parent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceSucc(Expression oldsucc, Expression newsucc) {
        for (int i = 0; i < exps.size(); i++) {
            //look for old expression
            if (exps.get(i) == oldsucc) {
                //replace with new one
                exps.set(i, newsucc);
                break;
            }
        }
    }

}
