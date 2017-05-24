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
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.InPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.OutPrefixExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.ParallelExpression;


/**
 * Class which represents reduction comprised of input-prefix expression and 
 * output-prefix expression.
 * @author Dagmar Prokopova
 */
public class IOReduction extends Reduction {
    
    private static final long serialVersionUID = 1L;
    
    //input prefix expression
    private InPrefixExpression input;
    
    //output prefix expression
    private OutPrefixExpression output;

    /**
     * Constructor which initializes input-prefix expression.
     * @param exp input-prefix expression
     */
    public IOReduction(InPrefixExpression exp) {
        input = exp;
    }
    
    /**
     * Constructor which initializes output-prefix expression.
     * @param exp output-prefix expression
     */
    public IOReduction(OutPrefixExpression exp) {
        output = exp;
    }     
    
    /**
     * Constructor which initializes both input and output prefix expression.
     * @param in input-prefix expression
     * @param out output-prefix expression
     */
    public IOReduction(InPrefixExpression in, OutPrefixExpression out) {
        input = in;
        output = out;
    }   
    
    /**
     * Returns input-prefix expression
     * @return input-prefix expression
     */
    public InPrefixExpression getIn() {
        return input;
    }
    
    /**
     * Returns output-prefix expression
     * @return output-prefix expression
     */
    public OutPrefixExpression getOut() {
        return output;
    }
    
    /**
     * Sets input-prefix expression
     * @param ex input-prefix expression to be set
     */
    public void setIn(InPrefixExpression ex) {
        input = ex;
    }
    
    /**
     * Sets output-prefix expression
     * @param ex output-prefix expression to be set
     */
    public void setOut(OutPrefixExpression ex) {
        output = ex;
    }
        
    /**
     * Returns common parallel expression which precedes both input and output
     * prefix expression. Returns null if reduction is incomplete or no
     * common parallel expression is found.
     * @return parallel expression which is ancestor for both input and output
     */
    private Expression findCommonPar() {
        if (!isComplete()) {
            return null;
        }
        
        Stack<Expression> inlist = input.getParentStack();
        Stack<Expression> outlist = output.getParentStack();

        Expression par = null;        
        while((!inlist.empty()) && (!outlist.empty()) && (inlist.peek() == outlist.peek())) {            
            par = inlist.pop();
            outlist.pop();
        }
        
        if (par != null && (par instanceof ParallelExpression)) {
            return par;
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns true if the specified input and output prefix expression can
     * interact together in the reduction. They need to share the channel
     * name and have equal number of parameters.
     * @return true if name transfer from output to input is valid
     */
    public boolean isNameTransferValid() {
        if (!isComplete()) {
            return false;
        }
        return ((input.getChannel().equals(output.getChannel())) && (input.getParams().size() == output.getParams().size()));
    }

    /**
     * Returns true if replication is valid, which means there is a common parallel
     * expression and name transfer is valid.
     * @return true if the reduction is feasible
     */
    public boolean isValid() {
        return ((isNameTransferValid()) && (findCommonPar() != null));
    }
        
    /**
     * {@inheritDoc}
     */        
    @Override
    public List<Expression> getExpressions() {
        List<Expression> explist = new ArrayList<>();
        if (input != null) {
            explist.add(input);
        }
        if (output != null) {
            explist.add(output);
        }
        return explist;
    }
    
    /**
     * {@inheritDoc}
     */        
    @Override
    public Expression getComplement(Expression exp) {
        if (input == exp) {
            return output;
        }
        if (output == exp) {
            return input;
        }
        return null;
    }    

    /**
     * {@inheritDoc}
     */        
    @Override
    public boolean isComplete() {
        return ((input != null) && (output != null));
    }

    /**
     * {@inheritDoc}
     */        
    @Override
    public IOReduction copy() {
        return new IOReduction(input, output);
    }
}