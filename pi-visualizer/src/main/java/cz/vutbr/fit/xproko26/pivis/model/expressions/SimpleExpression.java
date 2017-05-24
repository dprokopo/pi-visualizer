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

/**
 * Abstract class which represents pi-calculus expression containing exactly
 * one succesor.
 * @author Dagmar Prokopova
 */
public abstract class SimpleExpression extends Expression {

    private static final long serialVersionUID = 1L;
    
    //successor
    private Expression exp;    

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public SimpleExpression(Expression p) {
        super(p);
    }
    
    /**
     * Returns direct descendant of the expression.
     * @return child expression
     */
    public Expression getSuccExp() {
        return exp;
    }
    
    /**
     * Sets direct descendant of the expression.
     * @param e expression to be set as direct descendant
     */
    public void setSuccExp(Expression e) {
        exp = e;
    }
    
    /**
     * Removes itself from the expression tree by connecting
     * its direct descendant with its parent.
     */
    public void remove() {
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
        if (exp == oldsucc) {
            exp = newsucc;
        }
    }

}
