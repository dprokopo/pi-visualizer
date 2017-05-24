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
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;


/**
 * Class representing root of the pi-calculus expression tree.
 * @author Dagmar Prokopova
 */
public class RootExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;

    //list of name references to all free names
    private NRList nametable;

    /**
     * Constructor which sets parent expression to null and creates empty name table
     */    
    public RootExpression() {
        super(null);
        nametable = new NRList();
    }         

    /**
     * {@inheritDoc}
     */        
    @Override
    public NameRef getNameReference(NameValue val) {

        //check if specified name was defined as one of the free names
        for (NameRef n: nametable) {
            if (Model.getInstance().getNameTable().getNameValue(n).equals(val)) {
                return n;
            }
        }
        
        //else calls global name table to create record for such name
        NameRef newref = Model.getInstance().getNameTable().createName(val);        
        //save record into local name table
        nametable.add(newref);
        return newref;
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
    public RootExpression copy(Expression par) {
        RootExpression copy = new RootExpression();
        copy.setSuccExp(getSuccExp().copy(copy)); 
        copy.nametable = nametable.copy();
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
