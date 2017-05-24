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
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;

/**
 * Class which represents reduction comprised of single unobservable tau prefix.
 * @author Dagmar Prokopova
 */
public class TReduction extends Reduction {
    
    private static final long serialVersionUID = 1L;
    
    //tau-prefix expression
    private final TauPrefixExpression tauexpr;
    
    /**
     * Constructor which initializes tau-prefix expression.
     * @param ex tau-prefix expression
     */
    public TReduction(TauPrefixExpression ex) {
        tauexpr = ex;
    }    
    
    /**
     * Returns tau-prefix expression.
     * @return tau-prefix expression
     */
    public TauPrefixExpression getTau() {
        return tauexpr;
    }
    
    /**
     * {@inheritDoc}
     */         
    @Override
    public List<Expression> getExpressions() {
        List<Expression> explist = new ArrayList<>();
        explist.add(tauexpr);
        return explist;
    }
            
    /**
     * {@inheritDoc}
     */         
    @Override
    public Expression getComplement(Expression exp) {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */         
    @Override
    public boolean isComplete() {
        return (tauexpr != null);
    }

    /**
     * {@inheritDoc}
     */         
    @Override
    public TReduction copy() {
        return new TReduction(tauexpr);
    }

}
