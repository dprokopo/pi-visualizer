/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

grammar PiExpr;

cmds    :   (cmd NL)* cmd;

cmd     :   'agent' ID def      # Agent
        |   'show' sum          # Show
        |   'redlist'           # List
        |   'reduce'            # Reduce
        |   'simplify'          # Simplify
        |   'env' ID?           # Env
        |   'clear'             # Clear
        |   'reset'             # Reset
        |   'help'              # Help
        |   'exit'              # Exit
        |   'quit'              # Exit
        |                       # Empty
        ;

def     :   '=' sum
        |   '(' varlist ')' '=' sum
        |   '=' '(' '\\' varlist ')' sum
        ;

sum     :   par                         # Continuesum
        |   par ('+' par)+              # Summation
        ;

par     :   proc                        # Continuepar
        |   proc ('|' proc)+            # Parallel
        ;

proc    :   NIL                         # Nil
        |   ID '<' varlist '>'          # Concretization
        |   ID '(' varlist ')'          # Concretization
        |   ID                          # Concretization                
        |   pi proc                     # Prefix
        |   '[' NAME '=' NAME ']' proc  # Match
        |   '!' proc                    # Replication
        |   '(' '^' nlist ')' proc      # Restriction
        |   '(' sum ')'                 # Parentheses
        ;

pi      :   TAU '.'                        # Tau

        |   NAME '.' '(' '\\' varlist ')'   # Input
        |   NAME '(' varlist ')' '.'        # Input
        |   NAME '.'                        # Input
        
        |   '\'' NAME '.' '[' varlist ']'  # Output
        |   '\'' NAME  '<' varlist '>' '.' # Output
        |   '\'' NAME '.'                  # Output        
        
        ;

nlist   :   NAME (',' NAME)*;

varlist :   varname (',' varname)*;

varname :   NAME
        |   ID
        ;

TAU:    't';
NIL:    '0';
ID:     [A-Z]([a-zA-Z0-9] | '_' | '-')*;
NAME:   [a-z]([a-zA-Z0-9] | '_' | '-')*;

NL:     '\r'? '\n';
WS:     [ \t]+ -> skip;
