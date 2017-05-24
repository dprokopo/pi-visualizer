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

import cz.vutbr.fit.xproko26.pivis.antlr.PiExprBaseVisitor;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser;

/**
 * Returns Command object of specific type based on visited parser context.
 * @author Dagmar Prokopova
 */
public class CommandVisitor extends PiExprBaseVisitor<Command> {

    @Override
    public Command visitEmpty(PiExprParser.EmptyContext ctx) {
        return new Command(Command.Type.EMPTY, ctx);
    }
    
    @Override
    public Command visitClear(PiExprParser.ClearContext ctx) {        
        return new Command(Command.Type.CLEAR, ctx);
    }
    
    @Override
    public Command visitReset(PiExprParser.ResetContext ctx) {        
        return new Command(Command.Type.RESET, ctx);
    }
    
    @Override
    public Command visitHelp(PiExprParser.HelpContext ctx) {        
        return new Command(Command.Type.HELP, ctx);
    }
    
    @Override
    public Command visitExit(PiExprParser.ExitContext ctx) {        
        return new Command(Command.Type.EXIT, ctx);
    }
    
    @Override
    public Command visitAgent(PiExprParser.AgentContext ctx) {        
        return new Command(Command.Type.AGENT, ctx);
    }
    
    @Override
    public Command visitShow(PiExprParser.ShowContext ctx) {        
        return new Command(Command.Type.SHOW, ctx);
    }
    
    @Override
    public Command visitList(PiExprParser.ListContext ctx) {        
        return new Command(Command.Type.LIST, ctx);
    }
    
    @Override
    public Command visitSimplify(PiExprParser.SimplifyContext ctx) {        
        return new Command(Command.Type.SIMPLIFY, ctx);
    }
    
    @Override
    public Command visitReduce(PiExprParser.ReduceContext ctx) {        
        return new Command(Command.Type.REDUCE, ctx);
    }
    
    @Override
    public Command visitEnv(PiExprParser.EnvContext ctx) {        
        return new Command(Command.Type.ENV, ctx);
    }

}
