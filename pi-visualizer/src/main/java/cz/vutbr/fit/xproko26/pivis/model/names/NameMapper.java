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
package cz.vutbr.fit.xproko26.pivis.model.names;

import cz.vutbr.fit.xproko26.pivis.model.Model;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionVisitor;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SumExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.NilExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.MatchExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RestrictionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;

/**
 * NameMapper can be used in combination with {@link MapTable MapTable}
 * to traverse expression tree, remap names and also substitute and eventually 
 * replicate restricted and input prefix names if needed.
 * @author Dagmar Prokopova
 */
public class NameMapper extends ExpressionVisitor<MapTable> {

    //singleton instance of NameMapper class
    private static NameMapper instance;
    
    //replication flag indicating that the restriction and input names should be replicated
    private boolean replicate;
    
    //list of new name references which will be checked for name label conflicts
    private NRList newnames;

    /**
     * Method for accessing the singleton instance.
     * @return instance of NameMapper class
     */
    public static NameMapper getInstance() {
        if(instance == null) {
            instance = new NameMapper();
        }
        return instance;
    }
    
    /**
     * Initializes expression tree traversal
     * @param exp root of the expression tree to be traversed
     * @param mt map table filled with initial map records
     * @param rep replication flag, when true names should be replicated
     */
    public void traverse(Expression exp, MapTable mt, boolean rep) {
        replicate = rep;
        newnames = mt.getValues();
        visit(exp, mt);
    }
    
    @Override
    public MapTable visit(RootExpression node, MapTable mt) {
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(RestrictionExpression node, MapTable mt) {
        if (replicate) {
            node.getRestrictions().forEach((ref) -> {
                //replicate the old name
                NameRef newref = Model.getInstance().getNameTable().replicateName(ref);
                //save pair of old name and new name into map table
                mt.add(ref,newref);
                //remap old name reference
                mt.remap(ref);
            }); 
        }
        node.getRestrictions().forEach((ref)-> {
            newnames.forEach((newname) -> {
                //check for label conflict
                if (ref.labelEquals(newname)) {
                    ref.substitute();
                }
            });
        });
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(SumExpression node, MapTable mt) {
        node.getSuccExps().forEach((e) -> {
            visit(e, mt);
        });
        return null;
    }

    @Override
    public MapTable visit(ParallelExpression node, MapTable mt) {
        node.getSuccExps().forEach((e) -> {
            visit(e, mt);
        });
        return null;
    }
    
    
    @Override
    public MapTable visit(ParallelReplicationExpression node, MapTable mt) {
        node.getSuccExps().forEach((e) -> {
            visit(e, mt);
        });
        return null;
    }

    @Override
    public MapTable visit(ReplicationExpression node, MapTable mt) {
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(MatchExpression node, MapTable mt) {
        //remap left name if in map table
        mt.remap(node.getLeft());
        //remap right name if in map table
        mt.remap(node.getRight());
        visit(node.getSuccExp(), mt);        
        return null;
    }

    @Override
    public MapTable visit(ConcretizeExpression node, MapTable mt) {

        //remap process identifier
        mt.remap(node.getIDRef());
        
        //remap arguments if in map table
        mt.remap(node.getArgs());
        
        if (node.getSuccExp() != null) {
            visit(node.getSuccExp(), mt);
        }
        return null;
    }

    @Override
    public MapTable visit(NilExpression node, MapTable mt) {
        return null;
    }

    @Override
    public MapTable visit(AbstractionExpression node, MapTable mt) {
        //remap parameters if in map table
        mt.remap(node.getParams());
        
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(InPrefixExpression node, MapTable mt) {
        //remap channel if in map table
        mt.remap(node.getChannel());
        
        if (replicate) {
            node.getParams().forEach((ref) -> {
                //replicate the old name
                NameRef newref = Model.getInstance().getNameTable().replicateName(ref);
                //save pair of old name and new name into map table
                mt.add(ref,newref);
                //remap old name reference
                mt.remap(ref);
            });
        }
        node.getParams().forEach((ref)-> {
            newnames.forEach((newname) -> {
                //check for label conflict
                if (ref.labelEquals(newname)) {
                    ref.substitute();
                }
            });
        });
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(OutPrefixExpression node, MapTable mt) {
        //remap channel if in map table
        mt.remap(node.getChannel());
        //remap params if in map table
        mt.remap(node.getParams());
        
        visit(node.getSuccExp(), mt);
        return null;
    }

    @Override
    public MapTable visit(TauPrefixExpression node, MapTable mt) {
        visit(node.getSuccExp(), mt);
        return null;
    }
    
}
