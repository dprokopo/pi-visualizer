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

import cz.vutbr.fit.xproko26.pivis.model.Model;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NVList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;

/**
 * Class representing input prefix pi-calculus expression that
 * can receive 0 to N name references (arguments) via its channel
 * name.
 * @author Dagmar Prokopova
 */
public class InPrefixExpression extends InOutPrefixExpression {
    
    private static final long serialVersionUID = 1L;
    
     /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public InPrefixExpression(Expression p) {
        super(p);
    }
    
    /**
     * Creates local name reference table (params) from the specified
     * name values.
     * @param namelist list of name values
     */
    public void createNames(NVList namelist) {
        NRList params = new NRList();
        namelist.forEach((n) -> {            
            //send request to global table to create name of value n
            params.add(Model.getInstance().getNameTable().createName(n));
        });
        setParams(params);
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public NameRef getNameReference(NameValue val) {
        
        //check if specified name was defined as one of the parameter names
        for (NameRef n: getParams()) {
            if (Model.getInstance().getNameTable().getNameValue(n).equals(val)) {
                return n;
            }
        }
        return getParent().getNameReference(val);                
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public String toString() {                
        
        String ret = getChannel().toString();
        if (getParams() != null && getParams().size() > 0) {
            ret += "(";
            ret += getParams().toString();
            ret += ")";
        }
        ret += ".";
        ret += getSuccExp().toString();
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toStringDebug() {                
        
        String ret = getChannel().toString();
        if (getParams() != null && getParams().size() > 0) {
            ret += "(";
            ret += getParams().toStringDebug();
            ret += ")";
        }
        ret += ".";
        ret += getSuccExp().toStringDebug();
        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public InPrefixExpression copy(Expression par) {
        InPrefixExpression copy = new InPrefixExpression(par);      
        copy.setChannel(getChannel().copy());
        copy.setParams(getParams().copy());
        copy.setSuccExp(getSuccExp().copy(copy)); 
        return copy;
    }
}
