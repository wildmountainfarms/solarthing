grammar ActionLang;

// Run `man ascii` to view ascii table

@header {
package me.retrodaredevil.actions.lang.antlr;
}

node
//    : node_part ('{' node_list '}')? (':' node)? TERMINATOR?
    : node_part colon simple_argument
    | node_part
    ;


node_part
    : IDENTIFIER '(' ')'
    | IDENTIFIER '(' argument_list ','? ')'
    | IDENTIFIER '(' argument_list ',' named_argument_list ','? ')'
    | IDENTIFIER '(' named_argument_list ','? ')'
    | IDENTIFIER ' ' simple_argument
//    | IDENTIFIER
    ;

//node_list
//    : ()
//    | node (TERMINATOR node)* TERMINATOR?
//    ;

argument_list
    : argument (',' argument)*
    ;
named_argument_list
    : IDENTIFIER '=' argument (',' IDENTIFIER '=' argument)*
    ;

argument
    : node
    | simple_argument
    ;
simple_argument
    : text
    | NUMBER
    | BOOLEAN
    | array
    ;

array
    : '[' ']'
    | '[' argument (',' argument)* ','? ']'
    ;

text
    : IDENTIFIER
    | STRING
    ;

colon : ':';

IDENTIFIER
    : [A-Za-z_] [A-Za-z0-9_]*
    ;
STRING
    : '"' (SIMPLE_CHARACTER | ESCAPED_CHARACTER)* '"'
    ;
SIMPLE_CHARACTER
    : [ !#-[\]-~]
    ;
ESCAPED_CHARACTER
    : '\\"'
    ;
UNSIGNED_INT
    : [0-9]+
    ;
INT
    : ('-' | '+')? UNSIGNED_INT
    ;
NUMBER
    : INT ('.' UNSIGNED_INT)?
    ;
BOOLEAN
    : ('true' | 'false')
    ;

WS
    : [ \t\r\n]+ -> skip
    ;
//TERMINATOR
//    : [;\n]
//    ;
//COMMENT
//    : '#' ~'\n'*? '\n' -> skip
//    ;
