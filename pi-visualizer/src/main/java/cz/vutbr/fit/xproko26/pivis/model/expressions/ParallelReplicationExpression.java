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

import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Class representing parallel composition as part of the replication 
 * expression, since each replication can be expanded into parallel composition.
 * ParallelReplication usually has one original replication expression among
 * its successors and arbitrary number of copies which can be either visible
 * or invisible (helper). Apart from replication expressions (original or copies)
 * there can be any other type of expression which evolved from one of
 * replication branches by process of reduction.
 * @author Dagmar Prokopova
 */
public class ParallelReplicationExpression extends ParallelExpression {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */    
    public ParallelReplicationExpression(Expression p) {
        super(p);
    }
    
    /**
     * Returns true if there is at least one copy (incl. invisible helper)
     * of original replication expression among its successors.
     * @return true if replication copy exists
     */
    public boolean hasCopy() {
        for (Expression ex : getSuccExps()) {
            if (ex.isReplicationCopy()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if there is at least one invisible copy (helper)
     * of original replication expression among its successors.
     * @return true if replication helper exists
     */
    public boolean hasHelper() {
        for (Expression ex : getSuccExps()) {
            if (ex.isReplicationHelper()) {
                return true;
            }
        }
        return false;
    }
        
    /**
     * Returns original replication expression if there is such among
     * its successors. Otherwise returns null.
     * @return original replication expression
     */
    public ReplicationExpression getRepOriginal() {
        for (Expression ex : getSuccExps()) {
            if (ex.isReplicationOriginal()) {
                return (ReplicationExpression) ex;
            }
        }
        return null;
    }
    
    /**
     * Returns invisible replication copy (helper) if there is such among
     * its successors. Otherwise returns null.
     * @return helper replication expression
     */
    public ReplicationExpression getHelper() {
        for (Expression ex : getSuccExps()) {
            if (ex.isReplicationHelper()) {
                return (ReplicationExpression) ex;                
            }
        }
        return null;
    }
    
    /**
     * Returns true if the parallel expression is visible, that means
     * it has more than one visible successor.
     * @return 
     */
    public boolean isVisible() {
        int i = 0;
        for (Expression ex : getSuccExps()) {
            //if successor is not helper, it means it is visible
            if (!ex.isReplicationHelper()) {
                i++;                
            }
        }
        return (i > 1);
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public String toString() {
        String par = getSuccExps().stream().filter(e -> !e.isReplicationHelper()).map(e -> e.toString()).collect(Collectors.joining(" | "));
        if (isVisible()) {
            List<Expression> parlist = getParentList();
            for (Expression ex : parlist) {
                if (ex.isStringVisible()) {
                    if (ex instanceof SumExpression) {
                        return par;
                    }
                    return "(" + par + ")";
                }
            }
        }
        return par; 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toStringDebug() {
        String par = getSuccExps().stream().filter(e -> !e.isReplicationHelper()).map(e -> e.toStringDebug()).collect(Collectors.joining(" | "));
        if (isVisible()) {
            List<Expression> parlist = getParentList();
            for (Expression ex : parlist) {
                if (ex.isStringVisible()) {
                    if (ex instanceof SumExpression) {
                        return par;
                    }
                    return "(" + par + ")";
                }
            }
        }
        return par; 
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public ParallelReplicationExpression copy(Expression par) {
        ParallelReplicationExpression copy = new ParallelReplicationExpression(par);

        ReplicationExpression orig = getRepOriginal().copy(copy);
        copy.addExp(orig);
        
        ReplicationExpression helper = orig.copy(copy);
        helper.setCopyType(false);
        copy.addExp(helper);

        return copy;
    }
    
}