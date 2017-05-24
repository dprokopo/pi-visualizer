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

import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * Class representing match prefix pi-calculus expression that compares
 * two names. If names differ it evaluates as Nil otherwise has no
 * special effect and evaluates as its subexpression.
 * @author Dagmar Prokopova
 */
public class MatchExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;
    
    //first name reference to be compared
    private NameRef left;
    //second name reference to be compared
    private NameRef right;
    
    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public MatchExpression(Expression p) {
        super(p);
    }
    
    /**
     * Returns first name reference.
     * @return first name
     */
    public NameRef getLeft() {
        return left;
    }

    /**
     * Sets first name reference.
     * @param nr first name
     */
    public void setLeft(NameRef nr) {
        left = nr;
    }
    
    /**
     * Returns second name reference.
     * @return second name
     */
    public NameRef getRight() {
        return right;
    }
    
    /**
     * Sets second name reference.
     * @param nr second name
     */
    public void setRight(NameRef nr) {
        right = nr;
    }
    
    /**
     * Checks if name references point to the same name. Returns true
     * if names are equal.
     * @return boolean result of comparison
     */
    public boolean isValid() {
        return left.equals(right);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        
        String ret = "[" + left.toString() + "=" + right.toString() + "]";
        ret += getSuccExp().toString();
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toStringDebug() {
        
        String ret = "[" + left.toString() + "=" + right.toString() + "]";
        ret += getSuccExp().toStringDebug();
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MatchExpression copy(Expression par) {
        MatchExpression copy = new MatchExpression(par);
        copy.left = left.copy();
        copy.right = right.copy();        
        copy.setSuccExp(getSuccExp().copy(copy)); 
        return copy;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReductionStopper() {
        //in case the names are not equal, block evaluation of its descendants
        return !isValid();
    }
}
