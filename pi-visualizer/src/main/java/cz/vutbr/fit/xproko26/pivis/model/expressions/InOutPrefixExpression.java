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

import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * Abstract class which represents either in-prefix or out-prefix pi-calculus
 * expression.
 * @author Dagmar Prokopova
 */
public abstract class InOutPrefixExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;
   
    //name reference of channel
    private NameRef channel;
    
    //list of parameters
    private NRList params;    

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public InOutPrefixExpression(Expression p) {
        super(p);
    }
    
    /**
     * Returns name reference of channel name.
     * @return channel name reference
     */
    public NameRef getChannel() {
        return channel;
    }
    
    /**
     * Sets channel name reference.
     * @param c channel name reference
     */
    public void setChannel(NameRef c) {
        channel = c;
    }
    
    /**
     * Returns list of parameters as name references.
     * @return list of parameter name references
     */
    public NRList getParams() {
        return params;
    }
    
    /**
     * Sets the list of parameters.
     * @param p list of parameters
     */
    public void setParams(NRList p) {
        params = p;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReductionStopper() {
        return true;
    }

}
