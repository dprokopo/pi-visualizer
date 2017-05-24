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

import cz.vutbr.fit.xproko26.pivis.model.ProcessDefinition;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;

/**
 * Interface containing methods invoked for a command being successfully parsed.
 * @author Dagmar Prokopova
 */
public interface TextParserListener {
    
    /**
     * Invoked when exit command parsed.
     */
    public void cmdExit();
    
    /**
     * Invoked when clear command parsed.
     */
    public void cmdClear();
    
    /**
     * Invoked when reset command parsed.
     */
    public void cmdReset();
    
    /**
     * Invoked when help command parsed.
     */
    public void cmdHelp();
    
    /**
     * Invoked when agent command parsed.
     * @param procdef parsed process definition
     */
    public void cmdAgent(ProcessDefinition procdef);
    
    /**
     * Invoked when show command parsed.
     * @param expr parsed expression
     */
    public void cmdShow(Expression expr);
    
    /**
     * Invoked when redlist command parsed.
     */
    public void cmdList();
    
    /**
     * Invoked when reduce command parsed.
     */
    public void cmdReduce();
    
    /**
     * Invoked when simplify command parsed.
     */
    public void cmdSimplify();
    
    /**
     * Invoked when env command parsed.
     * @param id process identifier specified for env command or null
     */
    public void cmdEnv(String id);
}
