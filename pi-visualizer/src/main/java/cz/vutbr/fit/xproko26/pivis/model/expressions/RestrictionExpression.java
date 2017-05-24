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
 * Class representing pi-calculus restriction expression that
 * restricts the scope of the specified names.
 * @author Dagmar Prokopova
 */
public class RestrictionExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;
    
    //list of restricted names
    private NRList res;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */    
    public RestrictionExpression(Expression p) {
        super(p);
    }

    /**
     * Returns list of name references to restricted names
     * @return list of restricted names
     */
    public NRList getRestrictions() {
        return res;
    }
    
    /**
     * Sets list of name references as restricted names
     * @param rlist list of restricted names
     */
    public void setRestrictions(NRList rlist) {
        res = rlist;
    }

    /**
     * Creates local name reference table (restriction) from the specified
     * name values.
     * @param namelist list of name values
     */
    public void createNames(NVList namelist) {
        res = new NRList();
        namelist.forEach((n) -> {
            //set every name as private
            n.setPrivate();
            //send request to global table to create name of value n
            res.add(Model.getInstance().getNameTable().createName(n));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NameRef getNameReference(NameValue val) {
        
        //check if specified name was defined as one of the restricted names
        for (NameRef n: res) {
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
        String ret = "(^" + res.toString() + ")";
        ret += getSuccExp().toString();        
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toStringDebug() {        
        String ret = "(^" + res.toStringDebug() + ")";
        ret += getSuccExp().toStringDebug();       
        return ret;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public RestrictionExpression copy(Expression par) {
        RestrictionExpression copy = new RestrictionExpression(par);
        copy.res = res.copy();
        copy.setSuccExp(getSuccExp().copy(copy));
        return copy;
    }

}
