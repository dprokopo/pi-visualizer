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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprBaseVisitor;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.names.NRList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;
import cz.vutbr.fit.xproko26.pivis.model.names.NameRef;

/**
 * NRVarListVisitor processes varlist parser context and converts it into
 * name reference list (see {@link NRList NRList}).
 * @author Dagmar Prokopova
 */
public class NRVarListVisitor extends PiExprBaseVisitor<NRList> {
    
    //closest parent expression
    private Expression parent = null;
    
    /**
     * Constructor which initializes parent expression.
     * @param par 
     */
    public NRVarListVisitor(Expression par) {
        parent = par;
    }    
    
    @Override
    public NRList visitVarlist(PiExprParser.VarlistContext ctx) {
        
        //create NRList
        NRList nl = new NRList();              
        
        ctx.varname().stream().forEach(n -> {
            
            NameValue nval = new NameValue(n.getText());
            if (n.ID() != null) {
                nval.setProcess();
            }
            
            //for each name found in the context ask parent for name reference
            NameRef nr = parent.getNameReference(nval);
            if (nr == null) {
                //throw an exception if normal name is not defined
                throw new ParseCancellationException(n.getText());
            } else {
                //add name into NRList
                nl.add(nr);
            }
        });              
        return nl;
    }
}
