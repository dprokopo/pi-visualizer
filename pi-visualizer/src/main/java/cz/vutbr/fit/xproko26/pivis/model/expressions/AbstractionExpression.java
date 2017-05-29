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
 * Class representing pi-calculus abstraction as a part of process definition.
 * @author Dagmar Prokopova
 */
public class AbstractionExpression extends SimpleExpression {

    private static final long serialVersionUID = 1L;
    
    //list of all free names (parameters) of the defined process
    private NRList params;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */    
    public AbstractionExpression(Expression p) {
        super(p);
    }
    
    /**
     * Returns list references to free names (parameters)
     * @return list of free names
     */
    public NRList getParams() {
        return params;
    }
    
    /**
     * Sets list of references to free names (parameters)
     * @param p list of free namse
     */
    public void setParams(NRList p) {
        params = p;
    }
    
    /**
     * Creates local name reference table (params) from the specified
     * name values.
     * @param namelist list of name values
     */
    public void createNames(NVList namelist) {
        params = new NRList();
        namelist.forEach(n -> {
            params.add(Model.getInstance().getNameTable().createName(n));
        });
    }    
        
    @Override
    public NameRef getNameReference(NameValue val) {
        
        //check if specified name was defined as one of the parameter names
        for (NameRef n: params) {
            if (Model.getInstance().getNameTable().getNameValue(n).equals(val)) {
                return n;
            }
        }
        
        //if not, redirect request to parent
        if (getParent() != null) {
            return getParent().getNameReference(val); 
        }
        
        //check if defined process
        if (val.isProcess()) {
            val.setDefProcess(true);
            return Model.getInstance().getNameTable().createName(val);                   
        }
        
        return null;                           
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getSuccExp().toString();
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toStringDebug() {
        return getSuccExp().toStringDebug();
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public AbstractionExpression copy(Expression par) {
        AbstractionExpression copy = new AbstractionExpression(par);        
        copy.setSuccExp(getSuccExp().copy(copy));        
        copy.params = params.copy();
        return copy;
    }

    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean isStringVisible() {
        return false;
    }
   
}
