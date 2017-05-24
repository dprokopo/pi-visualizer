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

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprLexer;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser.CmdContext;
import cz.vutbr.fit.xproko26.pivis.model.expressions.RootExpression;
import cz.vutbr.fit.xproko26.pivis.model.expressions.Expression;
import cz.vutbr.fit.xproko26.pivis.model.ProcessDefinition;

/**
 * TextParser is a singleton class which (with the usage of Antlr library) 
 * provides methods for parsing application text commands and pi-calculus 
 * expressions. It invokes corresponding listener methods when valid command
 * is parsed or throws an excepion if parsing failed.
 * @author Dagmar Prokopova
 */
public class TextParser {
       
    //singleton instance of TextParser
    private static TextParser instance;
    
    //reference to text parser listener
    private static TextParserListener listener;
    
    /**
     * Method for accessing the singleton instance.
     * @return instance of TextParser class
     */
    public static TextParser getInstance() {
        if(instance == null) {
            instance = new TextParser();
        }
        return instance;
    }
    
    /**
     * Adds TextParserListener.
     * @param l listener to be added
     */
    public void addListener(TextParserListener l) {
        listener = l;
    }
    
    /**
     * Parses string of multiple lines containing process definitions.
     * @param lines input string to be parsed
     * @return list of parsed process definitions
     * @throws Exception if parsing failed
     */
    public List<ProcessDefinition> parseProcDefs(String lines) throws Exception {
        
        try {
            //split input string into tokens
            CharStream input = new ANTLRInputStream(lines);
            PiExprLexer lexer = new PiExprLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(ErrorListener.getInstance());
            CommonTokenStream tokens = new CommonTokenStream(lexer);
  
            //parse tokens into tree
            PiExprParser parser = new PiExprParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(ErrorListener.getInstance());
            
            List<ProcessDefinition> proclist = new ArrayList<>();
            List<CmdContext> cmds = parser.cmds().cmd();
            //for each line (command) apply CommandVisitor to get the command type
            for (CmdContext cmd : cmds) {
                Command command = cmd.accept(new CommandVisitor());
                switch (command.getType()) {
                    case AGENT:
                        proclist.add(getProcessDefinition(command.getContext()));
                        break;
                    case EMPTY:
                        break;
                    default:
                        throw new Exception("Error: Parser failed to get valid process definition.");
                }
            }
            
            return proclist;

        } catch (ParseCancellationException ex) {
            throw new Exception("Error: Parser failed to get valid process definition.");
        }
    }
    
    /**
     * Parses single command and invokes appropriate listener methods based on its type.
     * Throws an exception if parsing failed.
     * @param instring input string to be parsed
     * @throws Exception of parsing failed
     */
    public void parseCommand(String instring) throws Exception {
    
        try {
            //split input string into tokens
            CharStream input = new ANTLRInputStream(instring);
            PiExprLexer lexer = new PiExprLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(ErrorListener.getInstance());
            CommonTokenStream tokens = new CommonTokenStream(lexer);
  
            //parse tokens into tree
            PiExprParser parser = new PiExprParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(ErrorListener.getInstance());
            
            //extract and process command from the tree
            List<CmdContext> cmds = parser.cmds().cmd();
            if (!cmds.isEmpty()) {
                //invoke listener method according to command type
                processCmd(cmds.get(0).accept(new CommandVisitor()));
            }

        } catch (ParseCancellationException ex) {
            throw new Exception("Syntax error: " + ex.getMessage());
        }
    }

    /**
     * Returns process definition created out of agent command context by extracting
     * process identifier and using expression visitor to create expression tree.
     * @param ctx agent command context
     * @return process definition
     * @throws Exception if there are free names in the expression which were 
     * not specified as process definition parameters
     */
    private ProcessDefinition getProcessDefinition(Object ctx) throws Exception {
        PiExprParser.AgentContext procdef = (PiExprParser.AgentContext) ctx;
                
        try {
            //create new process definition with the usage of expression visitor
            return new ProcessDefinition(procdef.ID().getText(), procdef.def().accept(new ExpressionVisitor(null)));
        } catch (ParseCancellationException e) {
            throw new Exception("Validation error: Definition of proces '" + procdef.ID().getText() + "' contains free name '" + e.getMessage() + "'");
        }
    }
    
    /**
     * Returns expression tree created out of show command context with the usage
     * of expression visitor.
     * @param ctx show command context
     * @return expression tree
     */
    private Expression getExpressionTree(Object ctx) {
        PiExprParser.ShowContext expr = (PiExprParser.ShowContext) ctx;
        
        //create root expression which stores used names
        RootExpression rootexp = new RootExpression();
        rootexp.setSuccExp(expr.sum().accept(new ExpressionVisitor(rootexp)));
        
        return rootexp;
    }
    
    /**
     * Returns process identifier extracted out of env command context. Returns
     * null if no identifier was specified.
     * @param ctx env command context
     * @return process identifier
     */
    private String getProcId(Object ctx) {
        PiExprParser.EnvContext env = (PiExprParser.EnvContext) ctx; 
        return (env.ID() == null) ? null : env.ID().toString();
    }
    
    /**
     * Invokes appropriate listener method based on parsed command type.
     * @param command parsed command
     * @throws Exception if some of the internal methods failed (i.e. getting
     * process definition)
     */
    private void processCmd(Command command) throws Exception {

        if (listener == null)
            return;
        
        switch (command.getType()) {
            case HELP:
                listener.cmdHelp();
                break;
            case RESET:
                listener.cmdReset();
                break;
            case EXIT:
                listener.cmdExit();
                break;
            case CLEAR:
                listener.cmdClear();
                break;
            case LIST:
                listener.cmdList();
                break;
            case AGENT:
                listener.cmdAgent(getProcessDefinition(command.getContext()));
                break;
            case SHOW:
                listener.cmdShow(getExpressionTree(command.getContext()));
                break;
            case REDUCE:
                listener.cmdReduce();
                break;
            case SIMPLIFY:
                listener.cmdSimplify();
                break;
            case ENV:                
                listener.cmdEnv(getProcId(command.getContext()));
                break;
            default:
                break;
        }
    }

}
