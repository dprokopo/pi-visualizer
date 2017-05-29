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
package cz.vutbr.fit.xproko26.pivis.formater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import cz.vutbr.fit.xproko26.pivis.model.ProcessList;
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
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * TextFormater is a singleton class which provides methods for converting
 * expression into the list of TextBlobs which can be perceived as formated 
 * string.
 * @author Dagmar Prokopova
 */
public class TextFormater extends ExpressionVisitor<List<TextBlob>> {
    
    //text formater instance
    private static TextFormater instance;
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of TextFormater class
     */
    public static TextFormater getInstance() {
        if(instance == null) {
            instance = new TextFormater();
        }
        return instance;
    }
    
    /**
     * Creates formated TextBlob list out of process list.
     * @param proclist process list
     * @return 
     */
    public List<TextBlob> getString(ProcessList proclist) {
        List<TextBlob> list = new ArrayList<>();
        Iterator<String> iter = proclist.getProcessIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            list.add(new TextBlob("agent "));
            TextBlob idblob = new TextBlob(id);
            idblob.setProcId(true);
            list.add(idblob);
            AbstractionExpression exp = (AbstractionExpression) proclist.get(id);
            if (!exp.getParams().isEmpty()) {
                list.add(new TextBlob("("));
                list.addAll(getNameListBlobs(exp.getParams()));
                list.add(new TextBlob(")"));
            }        
            list.add(new TextBlob(" = "));
            
            list.addAll(getString(exp));
            if (iter.hasNext()) {
                list.add(new TextBlob("\n"));
            }
        }
        return list;
    }
    
    /**
     * Creates formated TextBlob list out of expression.
     * @param exp expression to be converted
     * @return 
     */
    public List<TextBlob> getString(Expression exp) {
        if (exp == null) {
            return new ArrayList<>();
        }
        
        return visit(exp, new ArrayList<>());
    }

    
    @Override
    public List<TextBlob> visit(RootExpression node, List<TextBlob> o) {
        return visit(node.getSuccExp(), o);
    }
    

    @Override
    public List<TextBlob> visit(RestrictionExpression node, List<TextBlob> o) {
        o.add(new TextBlob("(^"));
        o.addAll(getNameListBlobs(node.getRestrictions()));
        o.add(new TextBlob(")"));
        return visit(node.getSuccExp(), o);
    }
    

    @Override
    public List<TextBlob> visit(SumExpression node, List<TextBlob> o) {
        
        boolean parentheses = false;
        //check if parentheses are needed
        List<Expression> parlist = node.getParentList();
        for (Expression ex : parlist) {
            if (ex.isStringVisible()) {
                parentheses = true;
                break;
            }
        }
        
        List<TextBlob> ret = new ArrayList<>();
        
        if (parentheses) {
            ret.add(new TextBlob("("));
        }
        
        boolean first = true;
        for (Expression exp : node.getSuccExps()) {
            if (!first) {
                ret.add(new TextBlob(" + "));
            }
            ret.addAll(visit(exp, new ArrayList<>()));
            first = false;                     
        }  
            
        if (parentheses) {
            ret.add(new TextBlob(")"));
        }

        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
        }
        o.addAll(ret);
        return new ArrayList<>(o);
    }

    
    @Override
    public List<TextBlob> visit(ParallelExpression node, List<TextBlob> o) {
        
        
        boolean parentheses = false;
        //check if parentheses are needed
        List<Expression> parlist = node.getParentList();
        for (Expression ex : parlist) {
            if (ex.isStringVisible()) {
                if (!(ex instanceof SumExpression)) {
                    parentheses = true;
                }
                break;
            }
        }
        
        List<TextBlob> ret = new ArrayList<>();
        
        if (parentheses) {
            ret.add(new TextBlob("("));
        }

        boolean first = true;
        for (Expression exp : node.getSuccExps()) {
            if (!first) {
                ret.add(new TextBlob(" | "));
            }
            ret.addAll(visit(exp, new ArrayList<>()));
            first = false;                     
        }            
        
        if (parentheses) {
            ret.add(new TextBlob(")"));
        }
        
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
        }
        o.addAll(ret);
        return new ArrayList<>(o);
    }

    
    @Override
    public List<TextBlob> visit(ParallelReplicationExpression node, List<TextBlob> o) {
        
        boolean parentheses = false;
        if (node.isVisible()) {
            //check if parentheses are needed
            List<Expression> parlist = node.getParentList();
            for (Expression ex : parlist) {
                if (ex.isStringVisible()) {
                    if (!(ex instanceof SumExpression)) {
                        parentheses = true;
                    }
                    break;
                }
            }
        }
        
        List<TextBlob> ret = new ArrayList<>();;
        
        if (parentheses) {
            ret.add(new TextBlob("("));
        }
        
        boolean first = true;
        Expression origexp = null;
        for (Expression exp : node.getSuccExps()) {
            if (exp.isReplicationOriginal()) {
                origexp = exp;  //save original rep. to place it at the end
            }
            else if (!exp.isReplicationHelper()) {
                if (!first) {
                    ret.add(new TextBlob(" | "));
                }

                ret.addAll(visit(exp, new ArrayList<>()));
                first = false;                   
            }
        }
        
        //process orig rep. branch
        if (!first) {
            ret.add(new TextBlob(" | "));            
        }
        ret.addAll(visit(origexp, new ArrayList<>()));  
                
        if (parentheses) {
            ret.add(new TextBlob(")"));
        }
        
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
        }
        o.addAll(ret);
        return new ArrayList<>(o);
    }

    
    @Override
    public List<TextBlob> visit(ReplicationExpression node, List<TextBlob> o) {

        List<TextBlob> ret = new ArrayList<>();        
        if (node.isReplicationOriginal()) {            
            ret.add(new TextBlob("!"));
        }                
        ret.addAll(visit(node.getSuccExp(), new ArrayList<>()));
        
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
        }
        
        o.addAll(ret);
        return new ArrayList<>(o);
    }

    
    @Override
    public List<TextBlob> visit(InPrefixExpression node, List<TextBlob> o) {
        List<TextBlob> ret = new ArrayList<>();
        ret.add(getNameBlob(node.getChannel()));
        if (node.getParams() != null && node.getParams().size() > 0) {
            ret.add(new TextBlob("("));
            ret.addAll(getNameListBlobs(node.getParams()));
            ret.add(new TextBlob(")"));
        }
               
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
            if (node.getVisual().isReductionSelected()) {
                ret.forEach(e -> e.setReductionSelected(true));
            }            
        }

        o.addAll(ret);
        o.add(new TextBlob("."));
        return visit(node.getSuccExp(), o);
    }

    
    @Override
    public List<TextBlob> visit(OutPrefixExpression node, List<TextBlob> o) {
        List<TextBlob> ret = new ArrayList<>();
        ret.add(new TextBlob("'"));
        ret.add(getNameBlob(node.getChannel()));
        if (node.getParams() != null && node.getParams().size() > 0) {
            ret.add(new TextBlob("<"));
            ret.addAll(getNameListBlobs(node.getParams()));
            ret.add(new TextBlob(">"));
        }
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }
            if (node.getVisual().isReductionSelected()) {
                ret.forEach(e -> e.setReductionSelected(true));
            }            
        }

        o.addAll(ret);
        o.add(new TextBlob("."));
        return visit(node.getSuccExp(), o);
    }

    
    @Override
    public List<TextBlob> visit(TauPrefixExpression node, List<TextBlob> o) {
        TextBlob blob = new TextBlob("t");
        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                blob.setSelected(true);
            }
            if (node.getVisual().isReductionSelected()) {
                blob.setReductionSelected(true);
            }   
        }
        o.add(blob);
        o.add(new TextBlob("."));
        return visit(node.getSuccExp(), o);
    }

    
    @Override
    public List<TextBlob> visit(MatchExpression node, List<TextBlob> o) {
        List<TextBlob> ret = new ArrayList<>();
        ret.add(new TextBlob("["));
        ret.add(getNameBlob(node.getLeft()));
        ret.add(new TextBlob("="));
        ret.add(getNameBlob(node.getRight()));
        ret.add(new TextBlob("]"));

        if (node.getVisual() != null) {
            if (node.getVisual().isSelected()) {
                ret.forEach(e -> e.setSelected(true));
            }          
        }

        o.addAll(ret);
        return visit(node.getSuccExp(), o);
    }

    
    @Override
    public List<TextBlob> visit(ConcretizeExpression node, List<TextBlob> o) {
        List<TextBlob> ret = new ArrayList<>();        
        if ((node.getSuccExp() == null) || ((node.getVisual() != null) && (node.getVisual().isCollapsed()))) {
            TextBlob idblob = new TextBlob(node.getIDRef().toString());
            idblob.setProcId(true);
            ret.add(idblob);
            
            if (node.getArgs() != null && node.getArgs().size() > 0) {
                ret.add(new TextBlob("<"));
                ret.addAll(getNameListBlobs(node.getArgs()));
                ret.add(new TextBlob(">"));
            }            
            if (node.getVisual() != null) {
                if (node.getVisual().isSelected()) {
                    ret.forEach(e -> e.setSelected(true));
                }
            }            
            o.addAll(ret);
            return new ArrayList<>(o);
        }
        else {
            ret = visit(node.getSuccExp(), new ArrayList<>());
            if (node.getVisual() != null) {
                if (node.getVisual().isSelected()) {
                    ret.forEach(e -> e.setSelected(true));
                }
            }
            o.addAll(ret);
            return new ArrayList<>(o);
        }
    }

    
    @Override
    public List<TextBlob> visit(NilExpression node, List<TextBlob> o) {
        TextBlob blob = new TextBlob(node.toString());
        if ((node.getVisual() != null) && (node.getVisual().isSelected())) {
            blob.setSelected(true);
        }
        o.add(blob);
        return new ArrayList<>(o);
    }

    
    @Override
    public List<TextBlob> visit(AbstractionExpression node, List<TextBlob> o) {
        return visit(node.getSuccExp(), o);
    }
    
    
    /**
     * Creates TextBlob out of name reference.
     * @param ref name reference
     * @return TextBlob
     */
    private TextBlob getNameBlob(NameRef ref) {
        TextBlob blob = new TextBlob(ref.toString());
        blob.setName(true);
        if (ref.getNameValue().getVisual() != null) {
            if (ref.getNameValue().getVisual().isSelected()) {
                blob.setSelected(true);
            }
        }
        return blob;
    }
    
    
    /**
     * Creates list of TextBlobs out of list of name references.
     * @param list list of name references
     * @return list of TextBlobs
     */
    private List<TextBlob> getNameListBlobs(NRList list) {
        List<TextBlob> ret = new ArrayList<>();
        boolean first = true;
        for (NameRef r : list) {
            if (!first) {
                ret.add(new TextBlob(","));
            }
            ret.add(getNameBlob(r));
            first = false;
        }
        return ret;
    }
    
}
