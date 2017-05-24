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
package cz.vutbr.fit.xproko26.pivis.model.simplifier;

import java.util.ArrayList;
import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionVisitor;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionList;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.MatchExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.NilExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RestrictionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SumExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * Simplifier extends model functionallity about expression simplification
 * (i.e. merging duplicit node neighbours, removing nil branches, removing 
 * replication copies, ...)
 * @author Dagmar Prokopova
 */
public class Simplifier extends ExpressionVisitor<Object> {

    //singleton instance of Simplifier
    private static Simplifier instance;
    
    //reference to simplifier listener
    private static SimplifierListener listener;

    /**
     * Method for accessing the singleton instance.
     * @return instance of Simplifier class
     */
    public static Simplifier getInstance() {
        if(instance == null) {
            instance = new Simplifier();
        }
        return instance;
    }
    
    /**
     * Adds SimplifierListener.
     * @param l listener to be added
     */
    public void addListener(SimplifierListener l) {
        listener = l;
    }
    
    /**
     * Traverses the expression tree which root is passed as an argument.
     * During traversal simplifies the expression by removing nodes which 
     * are followed by Nil expression, merging neighbrour nodes of the same type 
     * (summation, parallel composition, restriction or replication), removing 
     * match prefixes which evaluate as true and removing redundant replication
     * copy branches.
     * @param exp the root node of expression tree which should be simplified
     * @return simplified expression
     */
    public Expression makeSimple(Expression exp) {
        if (exp != null) {
            visit(exp, null); 
        }      
        return exp;
    }
    
    
    @Override
    public Object visit(RootExpression node, Object o) {
        return visit(node.getSuccExp(), o);
    }

    @Override
    public Object visit(RestrictionExpression node, Object o) {
        visit(node.getSuccExp(), o);
        if (node.getSuccExp() instanceof NilExpression) {
            //remove node if its direct descendant is Nil expression
            node.remove();
        }
        else {
            Expression parent = node.getParent();
            //check if there are two neighbour nodes of the same type
            if (parent instanceof RestrictionExpression) {
                //transfer own restrictions to parent
                for (NameRef nr : node.getRestrictions()) {
                    ((RestrictionExpression) parent).getRestrictions().add(nr);
                }
                //remove node
                node.remove();
            }
        }
        return null;
    }

    @Override
    public Object visit(SumExpression node, Object o) {
        List<Expression> tmp = new ArrayList<>(node.getSuccExps());
        tmp.forEach(e-> visit(e, null));
        tmp.clear();
        
        //remove all Nil branches
        node.getSuccExps().forEach(e->{
            if (e instanceof NilExpression)
                tmp.add(e);
        });
        tmp.forEach(e -> node.removeExp(e));
        
        //check if node is redundant and should be removed
        if (!removeIfRedundant(node)) { //was not removed
            Expression parent = node.getParent();
            //check if there are two neighbour nodes of the same type
            if (parent instanceof SumExpression) {
                //give children to parent
                for (Expression e : node.getSuccExps()) {
                    e.setParent(parent);
                    ((SumExpression) parent).addExp(e);
                }
                //remove node
                ((SumExpression) parent).removeExp(node);
            }
        }
        return null;
    }

    @Override
    public Object visit(ParallelExpression node, Object o) {
        List<Expression> tmp = new ArrayList<>(node.getSuccExps());
        tmp.forEach(e-> visit(e, null));
        tmp.clear();
        
        //remove all Nil branches
        node.getSuccExps().forEach(e->{
            if (e instanceof NilExpression)
                tmp.add(e);
        });
        tmp.forEach(e -> node.removeExp(e));
        
        //check if node is redundant and should be removed
        if (!removeIfRedundant(node)) { //was not removed
            Expression parent = node.getParent();
            //check if there are two neighbour nodes of the same type
            if (parent instanceof ParallelExpression) { //valid even if parent is ParallelReplicationExpression
                //give children to parent
                for (Expression e : node.getSuccExps()) {
                    e.setParent(parent);
                    ((ParallelExpression) parent).addExp(e);
                }
                //remove node
                ((ParallelExpression) parent).removeExp(node);
            }
        }
        
        return null;
    }

    @Override
    public Object visit(ParallelReplicationExpression node, Object o) {
        
        List<Expression> tmp = new ArrayList<>(node.getSuccExps());
        tmp.forEach(e-> {
            if (!e.isReplicationCopy()) {
                visit(e, null); //visit descendant only if it is not replication copy nor helper
            }});
        tmp.clear();
        
        //remove nil children and replication copies(incl. helper)
        for (Expression exp : node.getSuccExps()) {
            if (exp instanceof NilExpression) {
                tmp.add(exp);
            }
            else if (exp.isReplicationCopy()) {
                tmp.add(exp);
            }
        }
        tmp.forEach(e -> node.removeExp(e));
        tmp.clear();
        
        Expression parent = node.getParent();   
        ReplicationExpression orig = node.getRepOriginal();
        //check if parent is parallel expression
        if (parent instanceof ParallelExpression) {            
            //transfer all non-replication children up to parent node
            for (Expression exp : node.getSuccExps()) {
                if (exp != orig) {
                    tmp.add(exp);
                }
            }
            for (Expression exp : tmp) {
                exp.setParent(parent);
                ((ParallelExpression) parent).addExp(exp);
                node.removeExp(exp);
            }
        }
        //check if parent is replication expression
        else if (parent instanceof ReplicationExpression) { //must be original, since copies are not visited
            //transfer all non-replication children up to grandparent node (which is apparently parallel-replication exp)
            for (Expression exp : node.getSuccExps()) {
                if (!(exp instanceof ReplicationExpression)) {
                    tmp.add(exp);
                }
            }
            for (Expression exp : tmp) {
                exp.setParent(parent.getParent());
                ((ParallelExpression) parent.getParent()).addExp(exp);
                node.removeExp(exp);
            }
            
            //check if there is original replication branch
            if (orig != null) {
                //situation: (replication original) -- (paralel replication) -- (replication original) -- (replication suffix)
                //connect original replication suffix directly to precedent replication node (parent)  
                orig.getSuccExp().setParent(parent);
                parent.replaceSucc(node, orig.getSuccExp());
            }
            else {
                //replace node with Nil, since it has no children
                parent.replaceSucc(node, new NilExpression(parent));
            }
            return null;
        }
        
        //check if original replication branch is still present        
        if (orig == null) { //no replication branch found  
            //check if node is redundant and should be removed
            if (!removeIfRedundant(node)) { //was not removed
                //replace with simple parallel expression since there is no replication descendant
                ParallelExpression newpar = new ParallelExpression(node.getParent());
                List<Expression> succs = node.getSuccExps();            
                succs.forEach(e -> { 
                    e.setParent(newpar);
                    newpar.addExp(e);
                });            
                node.getParent().replaceSucc(node, newpar);
            }
        }
        else {
            //re-create helper
            if (listener != null) {
                listener.requestReplication(orig);
            }
        }
        
        return null;
    }

    @Override
    public Object visit(ReplicationExpression node, Object o) {
        visit(node.getSuccExp(), o);        
        if (node.getSuccExp() instanceof NilExpression) {
            //remove node if its direct descendant is Nil expression
            node.remove();
        }
        return null;
    }

    @Override
    public Object visit(InPrefixExpression node, Object o) {
        return visit(node.getSuccExp(), o);
    }

    @Override
    public Object visit(OutPrefixExpression node, Object o) {
        return visit(node.getSuccExp(), o);
    }

    @Override
    public Object visit(TauPrefixExpression node, Object o) {
        return visit(node.getSuccExp(), o);
    }

    @Override
    public Object visit(MatchExpression node, Object o) {
        visit(node.getSuccExp(), o);        
        if ((node.getSuccExp() instanceof NilExpression) || node.isValid()) {
            //remove node if its direct descendant is Nil or is always true
            node.remove();
        }
        return null;
    }

    @Override
    public Object visit(ConcretizeExpression node, Object o) {
        visit(node.getSuccExp(), o);
        if (node.getSuccExp() instanceof NilExpression) {
            //remove node if its direct descendant is Nil
            node.remove();
        }
        return null;
    }

    @Override
    public Object visit(NilExpression node, Object o) {
        return null;
    }

    @Override
    public Object visit(AbstractionExpression node, Object o) {
        visit(node.getSuccExp(), o);
        if (node.getSuccExp() instanceof NilExpression) {
            //remove node if its direct descendant is Nil
            node.remove();
        }
        return null;
    }
    
    /**
     * Checks whether the node which can hold multiple descendants (i.e. summation 
     * or parallel composition) is redundat (has no or one descendant), and if so 
     * removes it. Method returns true, if the node is removed, false otherwise.
     * @param node node which should be checked
     * @return true if node is removed
     */
    private boolean removeIfRedundant(ExpressionList node) {
        List<Expression> succs = node.getSuccExps();
        //check if number of descendants is less than 2
        if (succs.size() < 2) {
            if (succs.isEmpty()) {
                //no descendants -> replace with Nil
                node.remove(new NilExpression(node));
            } else {
                //one descendant -> connect it directly to parent
                node.remove(succs.get(0));
            }
            return true;
        }
        return false;
    }
    
}
