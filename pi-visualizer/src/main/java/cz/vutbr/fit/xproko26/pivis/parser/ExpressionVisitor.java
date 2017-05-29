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
package cz.vutbr.fit.xproko26.pivis.parser;

import static java.util.stream.Collectors.toList;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprBaseVisitor;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.MatchExpression;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;
import cz.vutbr.fit.xproko26.pivis.model.expressions.NilExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SimpleExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RestrictionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SumExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;

/**
 * Traverses parser tree and creates internal representation of pi-calculus 
 * expressions based on the visited parser contexts. For name lists uses either
 * {@link NRListVisitor NRListVisitor} or {@link NVListVisitor NVListVisitor}.
 * @author Dagmar Prokopova
 */
public class ExpressionVisitor extends PiExprBaseVisitor<Expression> {
    
    //parent expression of the expression which should be created
    private final Expression parent;
        
    /**
     * Constructor which initializes parent expression.
     * @param par parent expression of expression which will be created
     */
    public ExpressionVisitor(Expression par) {
        parent = par;
    }
    
    
    @Override
    public Expression visitDef(PiExprParser.DefContext ctx) {
        
        //ceate abstraction expression and set its parent
        AbstractionExpression ex = new AbstractionExpression(parent);
        
        //process parameters
        if (ctx.varlist() == null) {
            ex.setParams(new NRList());            
        } else {
            ex.createNames(ctx.varlist().accept(new NVVarListVisitor()));
        }
        //visit following context and set the created expression as successor
        ex.setSuccExp(ctx.sum().accept(new ExpressionVisitor(ex)));
        return ex;
    }
    
    
    @Override
    public Expression visitNil(PiExprParser.NilContext ctx) {
        
        //create nil expression and set its parent
        NilExpression ex = new NilExpression(parent);
        return ex;
    }
    
    
    @Override
    public Expression visitConcretization(PiExprParser.ConcretizationContext ctx) {
        
        //create concretize expression and set its parent
        ConcretizeExpression ex = new ConcretizeExpression(parent);
                        
        //process identifier
        NameValue nv = new NameValue(ctx.ID().getText());
        nv.setProcess();
        ex.setIDRef(parent.getNameReference(nv));               
        
        //process parameters
        if (ctx.varlist() == null) {
            ex.setArgs(new NRList());
        } else {
            ex.setArgs(ctx.varlist().accept(new NRVarListVisitor(ex)));
        }
        
        return ex;
    }
    
    
    @Override
    public Expression visitRestriction(PiExprParser.RestrictionContext ctx) {
        
        //create restriction expression and set its parent
        RestrictionExpression ex = new RestrictionExpression(parent);
        
        //process restricted names
        ex.createNames(ctx.nlist().accept(new NVListVisitor()));
        
        //visit following context and set the created expression as successor
        ex.setSuccExp(ctx.proc().accept(new ExpressionVisitor(ex)));
        return ex;
    }
    
    
    @Override
    public Expression visitPrefix(PiExprParser.PrefixContext ctx) {
        
        //process prefix expression
        Expression ex = ctx.pi().accept(this);
        
        //visit following context and set the created expression as successor
        ((SimpleExpression) ex).setSuccExp(ctx.proc().accept(new ExpressionVisitor(ex)));        
        return ex;
    }
    
    
    @Override
    public Expression visitTau(PiExprParser.TauContext ctx) {
        
        //create tau prefix expression and set its parent
         return new TauPrefixExpression(parent);
    }
    
    
    @Override
    public Expression visitInput(PiExprParser.InputContext ctx) {
        
        //create input prefix expression and set its parent
        InPrefixExpression ex = new InPrefixExpression(parent);
        
        //process channel name
        ex.setChannel(parent.getNameReference(new NameValue(ctx.NAME().getText())));
        if (ex.getChannel() == null) {
            //throw an exception if name was not defined
            throw new ParseCancellationException(ctx.NAME().getText());
        }
        
        //process parameters
        if (ctx.varlist() == null) {
            ex.setParams(new NRList());            
        } else {
            ex.createNames(ctx.varlist().accept(new NVVarListVisitor()));
        }
        return ex;
        
    }
    
    @Override
    public Expression visitOutput(PiExprParser.OutputContext ctx) {
        
        //create output prefix expression and set its parent
        OutPrefixExpression ex = new OutPrefixExpression(parent);
        
        //process channel name
        ex.setChannel(parent.getNameReference(new NameValue(ctx.NAME().getText())));        
        if (ex.getChannel() == null) {
            //throw an exception if name was not defined
            throw new ParseCancellationException(ctx.NAME().getText());
        }
        
        //process parameters
        if (ctx.varlist() == null) {
            ex.setParams(new NRList());            
        } else {
            ex.setParams(ctx.varlist().accept(new NRVarListVisitor(ex)));
        }
        return ex;
    }
    
    
    
    @Override
    public Expression visitMatch(PiExprParser.MatchContext ctx) {
        
        //create match expression and set its parent
        MatchExpression ex = new MatchExpression(parent);
        
        //process left name
        ex.setLeft(parent.getNameReference(new NameValue(ctx.NAME(0).getText())));
        if (ex.getLeft() == null) {
            //throw an exception if name was not defined
            throw new ParseCancellationException(ctx.NAME(0).getText());
        }
        
        //process right name
        ex.setRight(parent.getNameReference(new NameValue(ctx.NAME(1).getText())));
        if (ex.getRight() == null) {
            //throw an exception if name was not defined
            throw new ParseCancellationException(ctx.NAME(1).getText());
        }
        
        //visit following context and set the created expression as successor
        ex.setSuccExp(ctx.proc().accept(new ExpressionVisitor(ex)));        
        return ex;
    }     
    
    
    @Override
    public Expression visitSummation(PiExprParser.SummationContext ctx) {
        
        //create summation expression and set its parent
        SumExpression ex = new SumExpression(parent);
        
        //visit following contexts and set the created expression as successors
        ex.setSuccExps(ctx.par().stream().map(proc -> proc.accept(new ExpressionVisitor(ex))).collect(toList()));
        return ex;
    }
    
    
    @Override
    public Expression visitParallel(PiExprParser.ParallelContext ctx) {
        
        //create parallel expression and set its parent
        ParallelExpression ex = new ParallelExpression(parent);
        
        //visit following contexts and set the created expression as successors
        ex.setSuccExps(ctx.proc().stream().map(proc -> proc.accept(new ExpressionVisitor(ex))).collect(toList()));
        return ex;
    }
    
    
    @Override
    public Expression visitReplication(PiExprParser.ReplicationContext ctx) {
        
        //create parallel replication expression and set its parent
        ParallelReplicationExpression par = new ParallelReplicationExpression(parent);
        
        //create helper-replication expression
        ReplicationExpression ex1 = new ReplicationExpression(par);
        //visit following context and set the created expression as successor
        ex1.setSuccExp(ctx.proc().accept(new ExpressionVisitor(ex1)));
        ex1.setCopyType(false);
                
        //create original replication expression
        ReplicationExpression ex2 = new ReplicationExpression(par);
        //visit following context and set the created expression as successor
        ex2.setSuccExp(ctx.proc().accept(new ExpressionVisitor(ex2)));
        
        //connect both to parallel replication expression
        par.addExp(ex1);
        par.addExp(ex2);
        
        return par;
    }
    
    
    @Override
    public Expression visitContinuesum(PiExprParser.ContinuesumContext ctx) {
        //ignore context
        return ctx.par().accept(new ExpressionVisitor(parent));
    }
    
    
    @Override
    public Expression visitContinuepar(PiExprParser.ContinueparContext ctx) {
        //ignore context
        return ctx.proc().accept(new ExpressionVisitor(parent));
    } 
        
    
    @Override
    public Expression visitParentheses(PiExprParser.ParenthesesContext ctx) {
        //ignore context
        return ctx.sum().accept(new ExpressionVisitor(parent));
    }        

}
