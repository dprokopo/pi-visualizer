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
package cz.vutbr.fit.xproko26.pivis.visualizer;

import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionVisitor;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
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

/**
 * NodeValueModifier is a singleton auxiliary class which traverses the 
 * expression tree and modifies the visual objects (NodeValues) of visited 
 * expressions with respect to {@link ModifierAction ModifierAction} 
 * specification.
 * @author Dagmar Prokopova
 */
public class NodeValueModifier extends ExpressionVisitor<Void> {
        
    //singleton instance of NodeVAlueModifier
    private static NodeValueModifier instance;
    
    //modification action which will be applied
    private static ModifierAction action;    
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of NodeValueModifier class
     */
    public static NodeValueModifier getInstance() {
        if(instance == null) {
            instance = new NodeValueModifier();
        }
        return instance;
    }
    
    /**
     * Initializes traversal of expression tree which root is passed as an argument.
     * The second argument specifies modification action.
     * @param exp root of the expression tree
     * @param a modification action
     */
    public void traverse(Expression exp, ModifierAction a) {
        if (exp == null)
            return;
        
        action = a;
        visit(exp, null);
    }

    
    @Override
    public Void visit(RootExpression node, Void o) {
        visit(node.getSuccExp(), null);
        return null;
    }

    @Override
    public Void visit(RestrictionExpression node, Void o) {
        visit(node.getSuccExp(), null);
        return null;
    }

    @Override
    public Void visit(SumExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            node.getSuccExps().stream().forEach((e) -> visit(e, null));
        }        
        return null;
    }

    @Override
    public Void visit(ParallelExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            node.getSuccExps().stream().forEach((e) -> visit(e, null));
        }        
        return null;
    }
        
    @Override
    public Void visit(ParallelReplicationExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);            
        }
        node.getSuccExps().stream().forEach((e) -> visit(e, null));
        return null;
    }

    @Override
    public Void visit(ReplicationExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);            
        }
        if (!node.isReplicationHelper()) {
            //visit successor only if it is not helper
            visit(node.getSuccExp(), null);
        }
        return null;
    }

    @Override
    public Void visit(InPrefixExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            visit(node.getSuccExp(), null);
        }        
        return null;
    }

    @Override
    public Void visit(OutPrefixExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            visit(node.getSuccExp(), null);
        }        
        return null;
    }

    @Override
    public Void visit(TauPrefixExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            visit(node.getSuccExp(), null);
        }        
        return null;
    }

    @Override
    public Void visit(MatchExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            visit(node.getSuccExp(), null);
        }        
        return null;
    }

    @Override
    public Void visit(ConcretizeExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
            if (node.getSuccExp() != null) {
                visit(node.getSuccExp(), null);
            }
        }        
        return null;
    }

    @Override
    public Void visit(NilExpression node, Void o) {
        NodeValue nv = node.getVisual();
        if (nv != null) {
            action.apply(nv);
        }
        return null;
    }

    @Override
    public Void visit(AbstractionExpression node, Void o) {
        visit(node.getSuccExp(), null);
        return null;
    }
    
}
