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

/**
 * Class representing parsed command which except for command type holds also
 * respective parser context for consecutive processing.
 * @author Dagmar Prokopova
 */
public class Command {
    
    /**
     * Possible command types.
     */
    public enum Type {
        AGENT, SHOW, EMPTY, CLEAR, RESET, ENV, HELP, EXIT, LIST, SIMPLIFY, REDUCE
    }
    
    //type of the command
    private final Type type;
    
    //parser context
    private final Object ctx;
    
    /**
     * Constructor which initializes both type and parser context.
     * @param t type of the command
     * @param c parser context
     */
    public Command(Type t, Object c) {
        type = t;
        ctx = c;
    }
    
    /**
     * Returns type of the command.
     * @return type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Returns parser context.
     * @return parser context
     */
    public Object getContext() {
        return ctx;
    }
}
