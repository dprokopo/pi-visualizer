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
 * Class representing unobservable tau prefix of pi-calculus expression that
 * can participate in reduction on its own (without any complement).
 * @author Dagmar Prokopova
 */
public class TauPrefixExpression extends SimpleExpression {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */    
    public TauPrefixExpression(Expression p) {
        super(p);
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toString() {       
        return "t."+getSuccExp().toString();
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toStringDebug() {       
        return "t."+getSuccExp().toStringDebug();
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public TauPrefixExpression copy(Expression par) {        
        TauPrefixExpression copy = new TauPrefixExpression(par);
        copy.setSuccExp(getSuccExp().copy(copy));
        return copy;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean isReductionStopper() {
        return true;
    }
}
