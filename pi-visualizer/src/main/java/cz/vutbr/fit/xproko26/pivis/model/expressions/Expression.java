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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import cz.vutbr.fit.xproko26.pivis.gui.graph.NodeValue;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;

/**
 * Abstract class which represents any pi-calculus expression as a node of
 * the expression tree.
 * @author Dagmar Prokopova
 */
public abstract class Expression implements Serializable {

    private static final long serialVersionUID = 1L;
    
    //reference to previous expression
    private Expression parent;
    
    //reference to object aggregating visual information about expression
    private NodeValue visual;
    
    /**
     * Constructor which sets parent expression.
     * @param p parent expression
     */
    public Expression(Expression p) {
        parent = p;
    }
    
    /**
     * Sets parent expression.
     * @param p parent
     */
    public void setParent(Expression p) {
        parent = p;
    }
    
    /**
     * Returns parent expression.
     * @return parent
     */
    public Expression getParent() {
        return parent;
    }
    
    /**
     * Sets object containing visual information about the expression.
     * @param nv visual object
     */
    public void setVisual(NodeValue nv) {
        visual = nv;
    }
    
    /**
     * Returns object containing visual information about the expression.
     * @return visual object
     */
    public NodeValue getVisual() {
        return visual;
    }
    
    /**
     * Returns stack of all ancestors with root placed on the top of the stack.
     * Stack does not contain expression for which the method was called.
     * @return stack of all ancestors
     */
    public Stack<Expression> getParentStack() {
        Stack<Expression> stack = new Stack<>();        
        Expression prev = this.parent;        
        while (prev != null) {
            stack.push(prev);
            prev = prev.getParent();
        }        
        return stack;
    }
    
    
    /**
     * Returns list of all ancestors with root placed at the end of the list.
     * List does not contain expression for which the method was called.
     * @return list of all ancestors
     */
    public List<Expression> getParentList() {       
        List<Expression> alist = new ArrayList<>();
        Expression prev = this.parent;        
        while (prev != null) {
            alist.add(prev);
            prev = prev.getParent();
        }    
        return alist;
    }
        
    
    /* --------- methods which can be redefined ---------- */
    
    /**
     * Returns name reference (index in name table) for specified name value.
     * In case that expression does not contain local name table, 
     * it redirects the method to its parent, otherwise it searches its
     * name table.
     * @param val name value
     * @return name reference
     */
    public NameRef getNameReference(NameValue val) {
        if (parent != null)
            return parent.getNameReference(val);
        else
            return null;
    }    
    
    /**
     * Returns true if textual representation of the expression is visible.
     * @return visibility flag of textual representation
     */
    public boolean isStringVisible() {
        return true;
    }
    
    /**
     * Returns true if expression prevents its descendants to participate
     * in reduction.
     * @return reduction stopping flag
     */
    public boolean isReductionStopper() {
        return false;
    }
    
    /**
     * Returns true if expression is replication helper.
     * @return replication helper flag
     */
    public boolean isReplicationHelper() {
        return false;
    }
    
    /**
     * Returns true if expression is replication copy (incl. helper)
     * @return replication copy flag
     */
    public boolean isReplicationCopy() {
        return false;
    }
    
    /**
     * Returns true if expression is replication original
     * @return replication original flag
     */
    public boolean isReplicationOriginal() {
        return false;
    }
            
    /* ----------------- abstract methods --------------------- */
    

    /**
     * Returns simple unformated string as textual representation of expression.
     * @return string
     */
    @Override
    public abstract String toString();
    
    /**
     * Returns simple unformated string for debugging purposes.
     * @return string
     */
    public abstract String toStringDebug();
    
    /**
     * Replaces old child-expression with new experssion.
     * @param oldsucc old expression to be replaced
     * @param newsucc new expression
     */
    public abstract void replaceSucc(Expression oldsucc, Expression newsucc);
    
    /**
     * Returns copy of the expression with specified parent.
     * @param parent parent of the copy
     * @return copied expression
     */
    public abstract Expression copy(Expression parent);
    
    
    
}
