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

import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;


/**
 * Class ProcessDefinition represents pair composed of process identifier and 
 * abstraction expression.
 * @author Dagmar Prokopova
 */
public class ProcessDefinition {
    
    //process identifier
    private final String id;
    //process expression
    private final Expression expression;        
    
    /**
     * Constructor creating the complete process definition.
     * @param id process identifier
     * @param exp process expression
     */
    public ProcessDefinition(String id, Expression exp) {
        this.id = id;
        this.expression = exp;
    }
    
    /**
     * Returns process identifier.
     * @return process identifier.
     */
    public String getID() {
        return id;
    }
    
    /**
     * Returns process expresion
     * @return process expression
     */
    public Expression getExpression() {
        return expression;
    }
}