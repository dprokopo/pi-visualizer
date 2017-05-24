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
 * Class representing replication as part of the replication expression (the other 
 * part is {@link ParallelReplicationExpression ParallelReplicationExpression}).
 * Replication may take over one of the following types: original replication,
 * visible copy, invisible copy (helper). Original replication is the expression
 * as it was defined by the user with (!) sign included. Replication copy is
 * exact copy of replication original which is visualized (i.e. user replicated
 * the original expression). Helper is special type of copy which was not
 * visualized by user, but serves for purpose of searching for possible reductions,
 * since replication branch may interact with itself even if it was not explicitly
 * replicated before the search was performed.
 * @author Dagmar Prokopova
 */
public class ReplicationExpression extends SimpleExpression {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Collection of all available replication type:
     * ORIGINAL, COPY, HELPER
     */
    private enum Type {
        ORIGINAL, COPY, HELPER
    }
    
    //type of the replication
    private Type type;
    

    /**
     * Constructor which sets parent expression and default type.
     * @param p parent expression
     */    
    public ReplicationExpression(Expression p) {
        super(p);
        type = Type.ORIGINAL;
    }
    
    /**
     * Sets type to either COPY or HELPER according to specified parameter.
     * @param visible true if expression should be set to COPY type,
     * false if it should be set to HELPER type
     */
    public void setCopyType(boolean visible) {
        type = visible ? Type.COPY : Type.HELPER;
    }  

    /**
     * {@inheritDoc}
     */    
    @Override
    public boolean isReplicationCopy() {
        return ((type == Type.HELPER) || (type == Type.COPY));
    }

    /**
     * {@inheritDoc}
     */        
    @Override
    public boolean isReplicationHelper() {
        return (type == Type.HELPER);
    }

    /**
     * {@inheritDoc}
     */        
    @Override
    public boolean isReplicationOriginal() {
        return (type == Type.ORIGINAL);
    }

    /**
     * {@inheritDoc}
     */        
    @Override
    public String toString() {
        if (isReplicationCopy()) {
            return getSuccExp().toString(); 
        }
        else {
            return "!" + getSuccExp().toString();           
        }
    }
    
    /**
     * {@inheritDoc}
     */        
    @Override
    public String toStringDebug() {
        return "!" + getSuccExp().toStringDebug();          
    }    
    
    /**
     * {@inheritDoc}
     */        
    @Override
    public ReplicationExpression copy(Expression par) {
        ReplicationExpression copy = new ReplicationExpression(par);
        copy.setSuccExp(getSuccExp().copy(copy));
        copy.type = type;
        return copy;
    }
}
