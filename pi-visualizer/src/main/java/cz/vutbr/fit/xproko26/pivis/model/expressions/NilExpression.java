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
 * Class representing nil pi-calculus expression and thus leaf node 
 * of the expression tree which means it does not have any successor.
 * @author Dagmar Prokopova
 */
public class NilExpression extends Expression {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public NilExpression(Expression p) {
        super(p);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "0";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toStringDebug() {
        return "0";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NilExpression copy(Expression par) {
        return new NilExpression(par);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceSucc(Expression oldsucc, Expression newsucc) {
        //does nothing since it has no successor
    }

}
