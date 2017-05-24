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
package cz.vutbr.fit.xproko26.pivis.model.redmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.MatchExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ExpressionVisitor;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InOutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.NilExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RestrictionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.SumExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.names.MapTable;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameMapper;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * Reduction manager extends model functionallity about reduction processing
 * (i.e. searching for available reductions, executing reduction, ...)
 * @author Dagmar Prokopova
 */
public class ReductionManager extends ExpressionVisitor<Object> {
    
    //singleton instance of ReductionManager
    private static ReductionManager instance;
    
    //reference to reduction manager listener
    private static ReductionManagerListener listener;
    
    //list of all reductions available in current context
    private final List<Reduction> reductionlist;            

    /**
     * Constructor which initializes reduction list.
     */
    public ReductionManager() {
        reductionlist = new ArrayList<>();
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of ReductionManager class
     */
    public static ReductionManager getInstance() {
        if(instance == null) {
            instance = new ReductionManager();
        }
        return instance;
    }
    
    /**
     * Adds ReductionManagerListener.
     * @param l listener to be added
     */
    public void addListener(ReductionManagerListener l) {
        listener = l;
    }
    
    /**
     * Returns list of reductions found.
     * @return reduction list
     */
    public List<Reduction> getReductionList() {
        return reductionlist;
    }
    
    /**
     * Returns list of suggested complementary expressions to an expression
     * specified as an argument. The method uses generated reduction list.
     * @param exp expression for which suggestions should be found
     * @return list of suggested expressions
     */
    public List<Expression> getSuggestions(Expression exp) {
        List<Expression> ret = new ArrayList<>();
        reductionlist.stream().forEach( red -> {
            Expression compexp = red.getComplement(exp);            
            if (compexp != null) {
                ret.add(compexp);
            }
        });
        return ret;
    }
    
    /**
     * Checks whether the expression passed as an argument is eventually
     * capable of participation in reduction, which means it must be a either
     * unobservable tau prefix expression or one of input or output prefix expressions.
     * If it is input or output prefix expression, there also needs to be at least
     * one parallel expression ahead of it and no stopping expression (i.e. another
     * prefix).
     * @param exp expression to be examined
     * @return true if expression can participate in reductions
     */
    public boolean isReducibleExpression(Expression exp) {
                
        boolean selectable;
        if (exp instanceof TauPrefixExpression) {
            selectable = true;
        } else if (exp instanceof InOutPrefixExpression) {
            selectable = false; //not sure yet if there is any parallel composition
        } else {
            return false;
        }

        //traverse expression tree from bottom to top parent by parent            
        while ((exp = exp.getParent()) != null) {
            //if there is any reduction stopper on the way, return false
            if (exp.isReductionStopper()) {
                return false;
            }
            //if parallel expression is encountered, set selectable as true
            if (exp instanceof ParallelExpression) {
                selectable = true;
            }
        }
        return selectable;
    }
    
    /* ----------------- REDUCTION EXECUTION ----------------------- */
    
    /**
     * Executes reduction for specified reduction of any type by redirecting 
     * reduction request to more specialized methods.
     * @param red reduction
     */
    public void reduce(Reduction red) {

        if (red instanceof TReduction) {
            reduce((TReduction) red);
        } else {
            reduce((IOReduction) red);
        } 
    }
    
    /**
     * Executes reduction for tau-prefix expression.
     * @param red reduction containing tau-prefix expression
     */
    private void reduce(TReduction red) {

        TauPrefixExpression tau = red.getTau();
        Stack<Expression> stack = tau.getParentStack();

        Expression exp;
        while (!stack.empty()) {
            exp = stack.pop();
            //fix all preceding sums, replications and concretizations
            fixSumRepCon(exp, stack.empty() ? tau : stack.peek());
        }
        //remove node
        tau.remove();
    }
    
    /**
     * Executes reduction for complementary input and output prefix expression.
     * @param red reduction containing input and output expressions
     */
    private void reduce(IOReduction red) {
        
        InPrefixExpression in = red.getIn();
        OutPrefixExpression out = red.getOut();
        
        //get stack of ancestors
        Stack<Expression> instack = in.getParentStack();
        Stack<Expression> outstack = out.getParentStack();
        
        //get list of private names among output parameters
        NRList privnames = new NRList(out.getParams().stream().filter(n -> n.isPrivate()).collect(Collectors.toList()));

        Expression stackexp = null;
        //iterate through stacks of ancestors
        while ((!instack.empty()) && (!outstack.empty()) && instack.peek() == outstack.peek()) {
            instack.pop();
            stackexp = outstack.pop();

            if (stackexp instanceof RestrictionExpression) {
                //for each restriction expression among ancestors...
                RestrictionExpression rexp = (RestrictionExpression) stackexp;               
                for (NameRef rn : rexp.getRestrictions()) {
                    //remove all defined names from the list of private names
                    List<NameRef> common = new ArrayList<>();
                    for (NameRef pn : privnames) {
                        if (rn.equals(pn)) {
                            common.add(pn);
                        }
                    }
                    for (NameRef c : common) {
                        privnames.remove(c);
                    }
                }
            }
            else {
                //for any other type of expression fix summation, replication and concretization
                fixSumRepCon(stackexp, outstack.empty() ? out : outstack.peek());
            }
        }
        if (stackexp == null)
            return; //never happens, there is always at least one common ancestor

        //action for the last common ancestor
        if (privnames.size() > 0) { //SCOPE EXTRUSION            
                       
            //substitute private names of output params which were not defined for input
            privnames.forEach((name) -> name.substitute()); 
            
            //last common ancestor must be parallel expression or parallel replication exp.
            ParallelExpression par = (ParallelExpression) stackexp;            
            Expression nextin = (instack.empty()) ? in : instack.peek();
            Expression nextout = (outstack.empty()) ? out : outstack.peek();
            
            //insert new restriction ahead or behind common parallel expression,
            //placing behind means creating brand new parallel composition exp. only
            //for the replicated branches and putting the restriction ahead of it
            if (par instanceof ParallelReplicationExpression) {
                if (nextin instanceof ReplicationExpression) {
                    fixReplication((ReplicationExpression)instack.pop());
                    nextin = (instack.empty()) ? in : instack.peek();
                }
                if (nextout instanceof ReplicationExpression) {
                    fixReplication((ReplicationExpression)outstack.pop());
                    nextout = (outstack.empty()) ? out : outstack.peek();
                }                
                putRestBehind(par, privnames, nextin, nextout);
            }
            else { //simple parallel
                if (par.getSuccExps().size() > 2) {
                    putRestBehind(par, privnames, nextin, nextout);
                }
                else { //put retriction ahead
                    putRestAhead(par, privnames);
                }
            }
        }
        
        //actions for output branch after separation
        while (!outstack.empty()) {
            stackexp = outstack.pop();
            if (stackexp instanceof RestrictionExpression) {
                RestrictionExpression rexp = (RestrictionExpression) stackexp;
                List<NameRef> common = new ArrayList<>();
                //remove all restricted names on the way which were part of output params
                //these names are already defined due to scope extrusion
                for (NameRef nr : rexp.getRestrictions()) {
                    for (NameRef pn : privnames) { 
                        if (pn.equals(nr)) {
                            common.add(nr);
                        }
                    }
                }
                for (NameRef c : common) {
                    rexp.getRestrictions().remove(c); 
                    //it would be possible to remove the name from privnames too but not necessary
                }
                
                //if the restriction node contains no restriction names, its useless, so lets remove it
                if (rexp.getRestrictions().isEmpty()) {
                    
                    Expression prev = rexp.getParent();
                    Expression succ = rexp.getSuccExp();
                    rexp.remove();
                    
                    //check if previous and next are parallel comps and thus can be merged
                    if (prev instanceof ParallelExpression) {
                        ParallelExpression pprev = (ParallelExpression) prev;
                        //merge only if successor is simple parallel expression, not part of replication
                        if ((succ instanceof ParallelExpression) && !(succ instanceof ParallelReplicationExpression)) {
                            ParallelExpression psucc = (ParallelExpression) succ;
                            //reconnect all children
                            for (Expression ex : psucc.getSuccExps()) {
                                ex.setParent(pprev);
                                pprev.addExp(ex);
                            }
                            pprev.removeExp(psucc);
                            outstack.pop();
                        }
                    }                    
                }
            }
            else {
                //fix summations, replications and concretizations
                fixSumRepCon(stackexp, outstack.empty() ? out : outstack.peek());
            }
        }
        
        //actions for input branch after separation
        while (!instack.empty()) {
            stackexp = instack.pop();
            if (stackexp instanceof RestrictionExpression) {
                //check for label collisions in restrictions with output parameters
                ((RestrictionExpression) stackexp).getRestrictions().forEach((res) -> {
                    out.getParams().forEach((transfername) -> {
                        //substitute the name for unique one if collision found
                        if (res.labelEquals(transfername)) {
                            res.substitute();
                        }
                    });
                });
            }
            else {
                //fix summations, replications and concretizations
                fixSumRepCon(stackexp, instack.empty() ? in : instack.peek());
            }
        }        
        
        //remap names in input branch (behind input node)
        MapTable maptable = new MapTable();
        maptable.add(in.getParams(), out.getParams()); 
        NameMapper.getInstance().traverse(in.getSuccExp(), maptable, false);

        //remove nodes
        in.remove();
        out.remove();
    }
    
    /**
     * Performs correcting actions based on expression type which is either summation, 
     * replication or concretization.
     * @param exp expression the actions should be performed for
     * @param next the direct descendant of the expression
     */
    private void fixSumRepCon(Expression exp, Expression next) {
        
        //CONCRETIZE EXP
        if (exp instanceof ConcretizeExpression) {
            //sets all concretize expressions as reduced
            //there is a doubt if all or just the last one should be set as reduced
            ((ConcretizeExpression) exp).setReduced();
         //SUM EXP
        } else if (exp instanceof SumExpression) {
            //all sumation expressions are removed
            //the single branch which stays is connected directly to predecessor
            ((SumExpression) exp).remove(next);
        //REPLICATION EXP
        } else if (exp instanceof ReplicationExpression) {
            //call separate method for fixing replications
            fixReplication((ReplicationExpression)exp);
        }
    }
    
    /**
     * Performs correcting action for replication expression - removes replication
     * node and connects replacing copy if needed.
     * @param rexp replication expression the actions should be performed for
     */
    private void fixReplication(ReplicationExpression rexp) {
        ParallelReplicationExpression par = (ParallelReplicationExpression) rexp.getParent();
        //create repliaction copy as a backup
        ReplicationExpression backup = rexp.copy(par);
        //create new names for all restrictions and inputs
        NameMapper.getInstance().traverse(backup, new MapTable(), true);
        //remove replication node from the original
        rexp.remove();

        if (rexp.isReplicationOriginal()) { //original
            //connect copied original backup
            par.addExp(backup);
        } else if (!par.hasCopy()) { //copy and the parent has none
            //set as helper and connect copied backup
            backup.setCopyType(false);
            par.addExp(backup);
        }
    }
    
    /**
     * Inserts a new restriction node ahead of parallel expression specified as
     * an argument. The new restriction node contains definition of all names
     * which are present in the list passed as the second argument.
     * @param par parallel expression
     * @param privnames list of private names which should be defined in newly
     * added restriction node
     */
    private void putRestAhead(ParallelExpression par, NRList privnames) {
        Expression parent = par.getParent();
        
        if (parent instanceof RestrictionExpression) {
            //if there already is a restriction node ahead, just add names to it
            ((RestrictionExpression) parent).getRestrictions().addAll(privnames);
        } else {
            //else create new restriction node and insert it between parallel exp and its parent
            RestrictionExpression rexp = new RestrictionExpression(parent);
            if (parent != null) {
                parent.replaceSucc(par, rexp);
            }
            rexp.setRestrictions(privnames.copy());
            rexp.setSuccExp(par);
            par.setParent(rexp);
        }
    }
    
    /**
     * Extracts two branches defined by nextin and nextout argument 
     * from the existing parallel expression specified as the first argument 
     * and inserts them into brand new parallel expression.
     * Then inserts a new restriction node betweeen original parallel expression 
     * and the new one. The new restriction node contains definition of all names
     * which are present in the list passed as the second argument.
     * @param par original parallel expression
     * @param privnames list of private names which should be defined in newly
     * added restriction node
     * @param nextin the first extracted branch
     * @param nextout the second extracted branch
     */
    private void putRestBehind(ParallelExpression par, NRList privnames, Expression nextin, Expression nextout) {
        //create restriction node
        RestrictionExpression rexp = new RestrictionExpression(par);
        rexp.setRestrictions(privnames.copy());
        //remove branches from original paralell expression
        par.removeExp(nextin);
        par.removeExp(nextout);
        //connect restriction node instead
        par.addExp(rexp);
        //create new parallel expression
        ParallelExpression newpar = new ParallelExpression(rexp);
        //connect extracted branches to new parallel expression
        newpar.addExp(nextin);
        nextin.setParent(newpar);
        newpar.addExp(nextout);
        nextout.setParent(newpar);
        //connect new parallel expression to replication node
        rexp.setSuccExp(newpar);
    }


    /* --------------- SEARCHING FOR REDUCTIONS ------------------ */
    
    /**
     * Traverses the expression tree which root is passed as an argument
     * and fills the reduction list during the process. During the traversal
     * the {@link ReductionContext ReductionContext} is used to know when
     * to stop and not to search for duplicit reductions. Another structure used
     * is {@link ActionList ActionList} to report available actions from lower
     * layers of the tree towards ancestor nodes. Every parallel expression node
     * then processes these action lists from all its children branches and
     * extracts available reductions out of them which are saved into reductionlist.
     * @param exp the root node of expression tree which should be traversed
     */
    public void generateReductionList(Expression exp) {
        reductionlist.clear();        
        if (exp != null) {
            //visit root with empty reduction context
            visit(exp, new ReductionContext());
        }   
    }    

    @Override
    public Object visit(RootExpression node, Object ctx) {
        return visit(node.getSuccExp(), ctx);
    }

    @Override
    public Object visit(RestrictionExpression node, Object ctx) {
        return visit(node.getSuccExp(), ctx);
    }

    @Override
    public Object visit(SumExpression node, Object ctx) {
        ActionList alist = new ActionList();
        node.getSuccExps().stream().map((e) -> visit(e, ctx)).forEachOrdered((actions) -> {
            alist.addAll((ActionList)actions);
        });
        return alist;
    }

    @Override
    public Object visit(ParallelExpression node, Object ctx) {
        ActionList alist = new ActionList();
        List<ActionList> alists = new ArrayList<>();
        node.getSuccExps().stream().map((e) -> visit(e, ctx)).forEachOrdered((actions) -> {
            alists.add((ActionList)actions);
            alist.addAll((ActionList)actions);
        });

        if (((ReductionContext) ctx).isEnabled()) {
            extractReductions(alists); 
        }
        return alist;        
    }
        
    @Override
    public Object visit(ParallelReplicationExpression node, Object ctx) {
        ActionList alist = new ActionList();
        List<ActionList> alists = new ArrayList<>();
        Expression helper = null;
        ActionList origlist = null;
        boolean traversedCopy = false;
        for (Expression ex : node.getSuccExps()) {

            if (ex.isReplicationHelper()) {
                helper = ex; //save it in case of need
                continue; // do not search throuhg yet
            } else if (ex.isReplicationCopy()) {
                traversedCopy = true;
            }

            ActionList actions = (ActionList) visit(ex, ctx);
            alists.add(actions);
            alist.addAll(actions);
            
            if (ex.isReplicationOriginal()) {
                origlist = actions; //save in case of comparation with helper
            }
            
        }                

        if (((ReductionContext) ctx).isEnabled()) {
            extractReductions(alists); 
            
            //if there was no replication copy, search for reduction actions in helper
            if (!traversedCopy && (helper != null)) {
                alists.clear();
                ReductionContext newctx = ((ReductionContext)ctx).copy();                
                newctx.setEnabled(false); //prevent adding helper branch internal reductions into the list
                alists.add((ActionList) visit(helper, newctx));
                alists.add(origlist);
                extractReductions(alists); 
            }
            
        }
        return alist;  
    }

    @Override
    public Object visit(ReplicationExpression node, Object ctx) {
        return visit(node.getSuccExp(), ctx);
    }
    
    @Override
    public Object visit(InPrefixExpression node, Object ctx) {
        ActionList alist = new ActionList();
        alist.add(node);
        return alist;
    }

    @Override
    public Object visit(OutPrefixExpression node, Object ctx) {
        ActionList alist = new ActionList();
        alist.add(node);
        return alist;
    }

    @Override
    public Object visit(TauPrefixExpression node, Object ctx) {
        if (((ReductionContext) ctx).isEnabled()) {
            reductionlist.add(new TReduction(node));
        }
        return new ActionList();
    }

    @Override
    public Object visit(MatchExpression node, Object ctx) {
       if (node.isValid()) {
           return visit(node.getSuccExp(), ctx);
       }
       else {
           return new ActionList();
       }
    }

    @Override
    public Object visit(ConcretizeExpression node, Object ctx) {
        ReductionContext newctx = ((ReductionContext)ctx).copy();
        if (node.getSuccExp() != null) {
            if (!node.isReduced()) {
                newctx.update(node.getID(), node.getArgs());
            }
            return visit(node.getSuccExp(), newctx);
        }
        else {
            if (((ReductionContext) ctx).notUsed(node.getID(), node.getArgs())) {
                if (listener != null) {
                    Expression inst = listener.getInstance(node);
                    if (inst != null) {
                        newctx.update(node.getID(), node.getArgs());
                        return visit(inst, newctx);
                    }
                }
            }
            return new ActionList();
        }
    }

    @Override
    public Object visit(NilExpression node, Object ctx) {
        return new ActionList();
    }

    @Override
    public Object visit(AbstractionExpression node, Object ctx) {
        return visit(node.getSuccExp(), ctx);
    }
    
    /**
     * Extracts available input-output reductions from the action lists united
     * into the list which is passed as an argument.
     * @param alists list of action lists
     */
    private void extractReductions(List<ActionList> alists) {
        
        //cycle throung the action lists while there are still at least 2 of them
        for (int i=0; i < alists.size()-1; i++) {
            ActionList sublist = alists.get(i);
            //put the selected action list in contrast with all following action lists
            for (int j = i + 1; j < alists.size(); j++) {
                ActionList compsublist = alists.get(j);
                
                //for each input action in the examined list find output complements in the following lists
                sublist.getInList().forEach((in) -> {
                    compsublist.getOutList().stream().map((out) -> new IOReduction(in, out)).filter((r) -> (r.isNameTransferValid())).forEachOrdered((r) -> {
                        reductionlist.add(r);
                    });
                });
                
                //for each output action in the examined list find input complements in the following lists
                sublist.getOutList().forEach((out) -> {
                    compsublist.getInList().stream().map((in) -> new IOReduction(in, out)).filter((r) -> (r.isNameTransferValid())).forEachOrdered((r) -> {
                        reductionlist.add(r);
                    });
                });                
            }
        }
    }
    
    
    
}
