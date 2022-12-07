grammar Arithmetic;

// Install this for good support: https://plugins.jetbrains.com/plugin/7358-antlr-v4
// You may have to manually build the main module of this gradle module if you make changes in here

@header {
package me.retrodaredevil.actions.lang.antlr;
}

expression
    : term (EXPRESSION_OP term)*
    ;

term
    : factor (TERM_OP factor)*
    ;

factor
    : '(' expression ')'
    | NUMBER
    ;

EXPRESSION_OP
    : (OP_PLUS | OP_MINUS)
    ;
TERM_OP
    : (OP_MULT | OP_DIV)
    ;

OP_PLUS
    : '+'
    ;
OP_MINUS
    : '-'
    ;
OP_MULT
    : '*'
    ;
OP_DIV
    : '/'
    ;

NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

WS
    : [ \t\r\n]+ -> skip
    ;
