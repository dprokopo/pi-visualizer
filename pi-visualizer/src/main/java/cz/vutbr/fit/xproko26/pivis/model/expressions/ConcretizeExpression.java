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
 * Class representing pi-calculus concretion of process definition.
 * @author Dagmar Prokopova
 */
public class ConcretizeExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;

    //name reference to process identifier
    private NameRef id;
    
    //list of process arguments
    private NRList args;
    
    //flag indicating whether the expression participated in reduction
    private boolean reduced;
    
        
    /**
     * Constructor which sets parent expression and reduced flag to false.
     * @param p parent expression
     */    
    public ConcretizeExpression(Expression p) {
        super(p);
        reduced = false;
    }         
    
    /**
     * Returns reference to process name. Null when not name but process ID.
     * @return name reference
     */
    public NameRef getIDRef() {
        return id;
    }
    
    /**
     * Sets name reference of process name.
     * @param ref name reference
     */
    public void setIDRef(NameRef ref) {
        id = ref;
    }
    
    /**
     * Returns list of arguments.
     * @return list of arguments
     */
    public NRList getArgs() {
        return args;
    }
    
    /**
     * Sets list of arguments.
     * @param a list of arguments
     */
    public void setArgs(NRList a) {
        args = a;
    }
    
    /**
     * Returns true if any subexpression participated in reduction and thus
     * its structure might differ from original process definition.
     * @return boolean flag indicating modification by reduction
     */
    public boolean isReduced() {
        return reduced;
    }
    
    /**
     * Sets reduction flag, which means that some subexpression participated
     * in reduction and thus its structure might differ from original
     * process definition
     */
    public void setReduced() {
        reduced = true;
    }  

    /**
     * {@inheritDoc}
     */ 
    @Override
    public String toString() {
        if (getSuccExp() == null) {
            String ret = id.toString();
            if (args != null && args.size() > 0) {
                ret += "<";
                ret += args.toString();
                ret += ">";
            }
            return ret;
        }
        else {
            return getSuccExp().toString();
        }
    }
    
    /**
     * {@inheritDoc}
     */     
    @Override
    public String toStringDebug() {
        if (getSuccExp() == null) {
            String ret = id.toString();
            if (args != null && args.size() > 0) {
                ret += "<";
                ret += args.toStringDebug();
                ret += ">";
            }

            return ret;
        }
        else {
            return getSuccExp().toString();
        }
    }
    
    /**
     * {@inheritDoc}
     */     
    @Override
    public ConcretizeExpression copy(Expression par) {
        ConcretizeExpression copy = new ConcretizeExpression(par);
        copy.id = id.copy();
        copy.args = args.copy();
        copy.setSuccExp(null);
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
