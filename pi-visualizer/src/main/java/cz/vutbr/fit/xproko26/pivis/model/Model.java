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
package cz.vutbr.fit.xproko26.pivis.model;

import java.util.ArrayList;
import java.util.List;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.AbstractionExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ConcretizeExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ReplicationExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.TauPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.names.MapTable;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameMapper;
import cz.vutbr.fit.xproko26.pivis.model.names.NameTable;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.IOReduction;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.Reduction;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.TReduction;
import cz.vutbr.fit.xproko26.pivis.model.simplifier.Simplifier;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.ReductionManager;
import cz.vutbr.fit.xproko26.pivis.model.redmanager.ReductionManagerListener;
import cz.vutbr.fit.xproko26.pivis.model.simplifier.SimplifierListener;

/**
 * Model is a singleton class which possesses application data and provides
 * methods for its maintanence.
 * @author Dagmar Prokopova
 */
public class Model {        
    
    //singleton instance of Model class
    private static Model instance;
    
    //listener for reporting data changes
    private static ModelListener listener;
    
    //reduction manager service
    private static ReductionManager redmanager;
    
    //simplifier service
    private static Simplifier simplifier;
    
    //application data
    private static Data data;
    
    //modification flag
    private static boolean modified;
    
    /**
     * Private constructor which creates reduction manager and simplifier
     * as specialized extensions of the model class.
     */
    private Model() {        
        redmanager = ReductionManager.getInstance();
        simplifier = Simplifier.getInstance();                        
    }
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of Model class
     */
    public static Model getInstance() {
        if(instance == null) {
            instance = new Model();
        }
        return instance;
    }
    
    /**
     * Sets model listener for declaring data changes and creates and sets
     * listeners for model components.
     * @param l model listener
     */
    public void addListener(ModelListener l) {
        listener = l;
        
        setRedManagerListener();
        setSimplifierListener();
    }
    
    /**
     * Creates and sets reduction manager listener
     */
    private void setRedManagerListener() {
        redmanager.addListener(new ReductionManagerListener() {
            @Override
            public Expression getInstance(ConcretizeExpression cexp) {
                //returns concretize expression instance
                if (cexp.getSuccExp() == null) {
                    try {
                        //instantiate if not created yet
                        return instantiate(cexp);
                    } catch (Exception ex) {};
                }
                return cexp.getSuccExp();                
            }
        });
    }
    
    /**
     * Creates and sets simplifier listener.
     */
    private void setSimplifierListener() {
        simplifier.addListener(new SimplifierListener() {
            @Override
            public void requestReplication(ReplicationExpression exp) {
                //replicates expression
                replicate(exp);
            }
        });
    }
    
    /**
     * Replicates specified expression and creates new helper branch out of it.
     * @param exp replication expression
     * @return replication helper
     */
    private ReplicationExpression replicate(ReplicationExpression exp) {
        //get parent
        ParallelReplicationExpression parallel = (ParallelReplicationExpression) exp.getParent();
        //copy the expression
        ReplicationExpression helper = (ReplicationExpression) exp.copy(parallel);
        //create new names for all restrictions and inputs
        NameMapper.getInstance().traverse(helper, new MapTable(), true);
        //mark it as helper branch
        helper.setCopyType(false);
        //connect to parent
        parallel.addExp(helper);
        //set modified flag
        setModified(true);
        
        return helper;
    }
    
    /**
     * Creates instance of concretize expression and returns it.
     * @param cexp concretize expression
     * @throws Exception 
     */
    private AbstractionExpression instantiate(ConcretizeExpression cexp) throws Exception {

        //get process definition
        AbstractionExpression procdef = getProcDef(cexp.getIDRef().toString(), cexp.getArgs());        
        //create copy
        AbstractionExpression inst = procdef.copy(cexp);

        //substitute names, create unique name values for restrictions and input 
        MapTable maptable = new MapTable(cexp.getRoot());
        maptable.add(inst.getParams(), cexp.getArgs());
        NameMapper.getInstance().traverse(inst, maptable, true);

        //connect instance to concretize expression
        cexp.setSuccExp(inst);        
        //set modified flag
        setModified(true);
        
        return inst;
    }
    
    /**
     * Asks reduction manager to regenerate reduction list and reports changes.
     */
    private void generateRedList() {
        redmanager.generateReductionList(data.getExpression());
        if (listener != null) {
            listener.redListModified(redmanager.getReductionList(), getRedSelectionIndex());
        }
    }
    
    /**
     * Returns index of the selected reduction in reduction list.
     * In case that there is no or incomplete reduction selected, returns
     * negative value;
     * @return index into reduction list or -1
     */
    private int getRedSelectionIndex() {
        Reduction red = data.getReduction();
        if ((red != null) && (red.isComplete())) {
            List<Reduction> redlist = redmanager.getReductionList();
            for (int i=0; i < redlist.size(); i++) {
                if (redlist.get(i).equals(red)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Sets modification flag.
     * @param b boolean value to be set
     */
    public void setModified(boolean b) {
        modified = b;
    }
    
    /**
     * Returns true if model was modified since last save/load.
     * @return modification flag
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Initializes model with a clean data instance, removes modified flag 
     * and reports initialization by calling the listener method.
     */
    public void init() {        
        data = new Data();
        setModified(false);
        
        if (listener != null) {
            listener.initialized();
        }
    }
    
    /**
     * Sets specified data, generates reduction list and reports changes through
     * listener.
     * @param d new data to be set into model
     */
    public void setData(Data d) {
        init();
        data = d;

        //generate reduction list
        generateRedList();            
        
        
        if (listener != null) {
            listener.expressionModified(data.getExpression());
            listener.procListModified(data.getProcList());
            listener.reductionModified(getRedSelectionIndex());
        }       
    }
    
    /**
     * Returns application data as a whole.
     * @return application data
     */
    public Data getData() {
        return data;
    }
    
    
    /**
     * Returns name table.
     * @return name table
     */
    public NameTable getNameTable() {
        return data.getNameTable();
    }
    
    /**
     * Sets new expression, removes reduction and generates new reduction list.
     * @param expr expression to be set
     */
    public void setExpression(Expression expr) {
        data.setExpression(expr);
        setReduction(null);
        generateRedList();
        setModified(true);
        
        if (listener != null) {
            listener.expressionModified(data.getExpression());
        }       
    }
    
    /**
     * Returns visualized expression.
     * @return visualized expression
     */
    public Expression getExpression() {
        return data.getExpression();
    }
    
    /**
     * Adds new process definition and reports changes through listener.
     * @param proc new process definition
     */
    public void addProcDef(ProcessDefinition proc) {
        data.addProcDef(proc);
        setModified(true);
        
        if (listener != null) {
            listener.procListModified(data.getProcList());
        }        
    }
    
    /**
     * Sets multiple process definitions.
     * @param procdefs list of process definitions
     */
    public void setProcDefs(List<ProcessDefinition> procdefs) {
        data.clearProcList();
        procdefs.forEach(pd -> data.addProcDef(pd));
        setModified(true);
        
        if (listener != null) {
            listener.procListModified(data.getProcList());
        }
    }           
    
    /**
     * Returns list containing all process definitions.
     * @return process list
     */
    public ProcessList getProcList() {
        return data.getProcList();
    }
    
    /**
     * Returns abstraction expression of specified process.
     * @param id process identifier
     * @param args list of arguments
     * @return abstraction expression
     * @throws Exception in case that process is not defined or number of arguments
     * does not match.
     */
    public AbstractionExpression getProcDef(String id, NRList args) throws Exception {        
        AbstractionExpression expr = (AbstractionExpression) data.getProcess(id);
        
        //check if process is defined
        if (expr == null) {
            throw new Exception("Warning: Missing definition of proces '" + id + "'.");
        }
        
        //check if number of parameters and arguments match
        if (expr.getParams().size() != args.size()) {
            throw new Exception("Warning: Invalid number of arguments. Proces '" + id + "' requires " + expr.getParams().size() + " arguments, " + args.size() + " provided.");
        }
        
        for (int i=0; i < expr.getParams().size(); i++) {
            if (expr.getParams().get(i).isProcess()) {
                if (!args.get(i).isProcess()) {
                    throw new Exception("Warning: The " + (int)(i+1) + ". argument of process " + id + " needs to be a process name.");
                }
            }
            else {
                if (args.get(i).isProcess()) {
                    throw new Exception("Warning: The " + (int)(i+1) + ". argument " + id + " cannot be a process name.");
                }
            }
        }
        
        return expr;
    }
    
    /**
     * Returns expression instance. If there is none, instantiates expression.
     * @param exp concretize expression which instance is requested
     * @return expression instance
     * @throws Exception if instantiation failed
     */
    public Expression getExpressionInstance(Expression exp) throws Exception {

        if (exp instanceof ConcretizeExpression) {            
            ConcretizeExpression cexp = (ConcretizeExpression) exp;
            
            //if there is no instance, create one
            Expression inst = cexp.getSuccExp();
            if (inst == null) {
                inst = instantiate(cexp);
                generateRedList();
            }
            return inst;
        }
        return null;

    }
    
    /**
     * Returns replication helper. If there is none, replicates expression to
     * create new one.
     * @param exp expression to be replicated
     * @return replication helper
     */
    public Expression getReplicationHelper(Expression exp) {
        
        if (exp instanceof ReplicationExpression) {     
            ParallelReplicationExpression parallel = (ParallelReplicationExpression) exp.getParent();
                                    
            //if there is no helper branch, create new helper
            ReplicationExpression helper = parallel.getHelper();
            if (helper == null) {
                helper = replicate((ReplicationExpression)exp);
            }            
            return helper;  
        }        
        return null;
    }
    
    /**
     * Changes type of replication expression from helper to copy.
     * @param exp replication helper which type should be changed
     */
    public void changeHelperToCopy(Expression exp) {
        ((ReplicationExpression) exp).setCopyType(true);
        generateRedList();
    }
              
    
    /* ---------------- reduction (de)selections ----------------- */
    
    /**
     * Checks if the specified expression can be reduced and thus selected
     * for reduction.
     * @param exp expression to be checked
     * @return true if expression can be reduced
     */
    public boolean isReductionSelectable(Expression exp) {
        return redmanager.isReducibleExpression(exp);
    }
    
    /**
     * Returns list of expressions which are selected for reduction.
     * @return list of selected expressions
     */
    public List<Expression> getSelection() {
        Reduction red = data.getReduction();
        if (red != null) {
            return red.getExpressions();
        }
        else {
            return new ArrayList<>();
        }
    }
    
    /**
     * Returns list of expressions which are suggested as complementary choice 
     * for selected reduction. Works only for incomplete in-out-reduction.
     * @return list of suggested expressions
     */
    public List<Expression> getSuggestions() {
        Reduction red = data.getReduction();
        if (red != null && !red.isComplete()) {
            return redmanager.getSuggestions(red.getExpressions().get(0));
        }
        else {
            return new ArrayList<>();
        }
    }        
    
    /**
     * Processes selection of tau prefix expression.
     * @param ex selected tau prefix expression
     */
    private void selectForReduction(TauPrefixExpression ex) {
        data.setReduction(new TReduction(ex));
    }
    
    /**
     * Processes selection of input prefix expression.
     * @param ex selected input prefix expression
     */
    private void selectForReduction(InPrefixExpression ex) {
        Reduction red = data.getReduction();
        if ((red != null) && (red instanceof IOReduction)) {
            IOReduction iored = (IOReduction) red;
            
            if (iored.getIn() == null) {
                iored.setIn(ex);
                //check if reduction is valid
                if (iored.isValid()) {
                    return;
                }
            }
        }
        data.setReduction(new IOReduction(ex));
    }
    
    /**
     * Processes selection of output prefix expression
     * @param ex selected output prefix expression
     */
    private void selectForReduction(OutPrefixExpression ex) {
        Reduction red = data.getReduction();
        if ((red != null) && (red instanceof IOReduction)) {
            IOReduction iored = (IOReduction) red;
            
            if (iored.getOut() == null) {
                iored.setOut(ex);
                //check if reduction is valid
                if (iored.isValid()) {
                    return;
                }
            }
        }
        data.setReduction(new IOReduction(ex));
    }
    
    /**
     * Processes deselection of tau prefix expression
     * @param ex deselected tau prefix expression
     */
    private void deselectFromReduction(TauPrefixExpression ex) {
        data.setReduction(null);
    }
    
    /**
     * Processes deselection of in prefix expression
     * @param ex deselected in prefix expression
     */
    private void deselectFromReduction(InPrefixExpression ex) {
        Reduction red = data.getReduction();
        if (red.isComplete()) {
            ((IOReduction) red).setIn(null);
        } else {
            data.setReduction(null);
        }
    }
    
    /**
     * Processes deselection of out prefix expression
     * @param ex deselected out prefix expression
     */
    private void deselectFromReduction(OutPrefixExpression ex) {
        Reduction red = data.getReduction();
        if (red.isComplete()) {
            ((IOReduction) red).setOut(null);
        } else {
            data.setReduction(null);
        }
    }
    
    /**
     * Processes selection of expression and reports changes.
     * @param ex selected expression
     */
    public void selectForReduction(Expression ex) {
        
        if (ex instanceof TauPrefixExpression) {
            selectForReduction((TauPrefixExpression) ex);
        }        
        else if (ex instanceof InPrefixExpression) {
            selectForReduction((InPrefixExpression) ex);
        }
        else if (ex instanceof OutPrefixExpression) {
            selectForReduction((OutPrefixExpression) ex);
        }
        
        if (listener != null) {
            listener.reductionModified(getRedSelectionIndex());
        }
        
        //set modified flag
        setModified(true);
    }
    
    /**
     * Processes deselection of expression an reports changes.
     * @param ex deselected expression
     */
    public void deselectFromReduction(Expression ex) {
        
        if (ex instanceof TauPrefixExpression) {
            deselectFromReduction((TauPrefixExpression) ex);
        }        
        else if (ex instanceof InPrefixExpression) {
            deselectFromReduction((InPrefixExpression) ex);
        }
        else if (ex instanceof OutPrefixExpression) {
            deselectFromReduction((OutPrefixExpression) ex);
        }
        
        if (listener != null) {
            listener.reductionModified(getRedSelectionIndex());
        }
        
        //set modified flag
        setModified(true);
    }

    /**
     * Sets selected reduction and reports change.
     * @param r reduction to be set
     */
    public void setReduction(Reduction r) {
        data.setReduction(r);
        if (listener != null) {
            listener.reductionModified(getRedSelectionIndex());
        }
        
        //set modified flag
        setModified(true);
    }
    

    /* -------------- reduction and simplification -------------- */
    
    /**
     * In case there is complete reduction selected, executes reduction
     * regenerates reduction list and reports changes, otherwise throws
     * an exception containing warning message.
     * @throws Exception when incomplete reduction is selected 
     */
    public void reduce() throws Exception {
        
        Reduction red = data.getReduction();
        if (red == null) {
            throw new Exception("Warning: Nothing selected for reduction.");
        } else if (!red.isComplete()) {
            throw new Exception("Warning: Incomplete action selected for reduction.");
        } else {  
            redmanager.reduce(red);           
            if (listener != null) {
                listener.expressionModified(data.getExpression());
            }
            setReduction(null);
            generateRedList(); 
            
            //set modified flag
            setModified(true);
        }        
    }

    /**
     * In case there is an expression visualized, executes simplification
     * over it, regenerates reduction list and reports changes, otherwise
     * throws an exception containing warning message.
     * @throws Exception 
     */
    public void simplify() throws Exception {
        Expression exp = getExpression();
        if (exp == null) {
            throw new Exception("Warning: No expression to simplify.");
        } else {
            simplifier.makeSimple(exp);
            if (listener != null) {
                listener.expressionModified(data.getExpression());
            }
            setReduction(null);
            generateRedList();
            
            //set modified flag
            setModified(true);
        }
    }
         
}
