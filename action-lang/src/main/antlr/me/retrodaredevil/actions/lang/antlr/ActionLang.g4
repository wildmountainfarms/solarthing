grammar ActionLang;

// Run `man ascii` to view ascii table

@header {
package me.retrodaredevil.actions.lang.antlr;
}

node
    : node_part (CURLY_L NEW_LINE* (node (term NEW_LINE* node)* term? NEW_LINE*)? CURLY_R)? (COLON node)? term?
    ;


node_part
    : IDENTIFIER PAREN_L NEW_LINE* PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* argument_list NEW_LINE* COMMA? PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* argument_list NEW_LINE* COMMA NEW_LINE* named_argument_list NEW_LINE* COMMA? NEW_LINE* PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* named_argument_list NEW_LINE* COMMA? NEW_LINE* PAREN_R
    | IDENTIFIER simple_argument
    | IDENTIFIER
    ;

argument_list
    : argument (NEW_LINE* COMMA NEW_LINE* argument)*
    ;
named_argument_list
    : IDENTIFIER EQUAL argument (NEW_LINE* COMMA NEW_LINE* IDENTIFIER EQUAL argument)*
    ;

argument
    : node
    | number
    | STRING
    | BOOLEAN
    | array
    ;
simple_argument
    : IDENTIFIER // In this case, an IDENTIFIER should be interpreted as a STRING
    | STRING
    | number
    | BOOLEAN
    | array
    ;

array
    : BRACKET_L BRACKET_R
    | BRACKET_L argument (COMMA argument)* COMMA? BRACKET_R
    ;

number
    : DECIMAL
    | INT
    | UNSIGNED_INT
    ;

term
    : NEW_LINE
    | TERMINATOR
    ;

COLON : ':';
EQUAL : '=';
COMMA : ',';
PAREN_L : '(';
PAREN_R : ')';
CURLY_L : '{';
CURLY_R : '}';
BRACKET_L : '[';
BRACKET_R : ']';

IDENTIFIER
    : [A-Za-z_] ('-'? [A-Za-z0-9_])*
    ;
STRING
    : '"' ([ !#-[\]-~] | '\\"')* '"'
    ;
DECIMAL
    : ('-' | '+')? [0-9]+ ('.' [0-9]+)?
    ;
INT
    : ('-' | '+') [0-9]+
    ;
UNSIGNED_INT
    : ('-' | '+') [0-9]+
    ;
BOOLEAN
    : ('true' | 'false')
    ;

NEW_LINE
    : '\n'
    | '//' (~'\n')*? '\n' // we need to treat end of line comments as new lines so the above grammar works
    ;

TERMINATOR
    : ';'
    ;
WS
//    : [ \t\r\n]+ -> skip
    : [ \t\r]+ -> skip
    ;
