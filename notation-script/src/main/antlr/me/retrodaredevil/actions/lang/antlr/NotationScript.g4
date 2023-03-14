grammar NotationScript;

// Run `man ascii` to view ascii table

@header {
package me.retrodaredevil.actions.lang.antlr;
}

// We may add more sections to top_node in the future to support an "import" section
top_node
    : NEW_LINE* node NEW_LINE*
    ;

node
    : node_part node_list_part? linked_node_part? term?
    ;


node_part
    : IDENTIFIER PAREN_L NEW_LINE* PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* argument_list NEW_LINE* COMMA? PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* argument_list NEW_LINE* COMMA NEW_LINE* named_argument_list NEW_LINE* COMMA? NEW_LINE* PAREN_R
    | IDENTIFIER PAREN_L NEW_LINE* named_argument_list NEW_LINE* COMMA? NEW_LINE* PAREN_R
    | IDENTIFIER simple_argument
    | IDENTIFIER
    ;
node_list_part
    : CURLY_L NEW_LINE* (node (term NEW_LINE* node)* term? NEW_LINE*)? CURLY_R
    ;
linked_node_part
    : COLON node
    ;

argument_list
    : argument (NEW_LINE* COMMA NEW_LINE* argument)*
    ;
named_argument_list
    : lenient_identifier EQUAL argument (NEW_LINE* COMMA NEW_LINE* lenient_identifier EQUAL argument)*
    ;

lenient_identifier
    : IDENTIFIER
    | STRING
    | BOOLEAN
    | number
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
    : BRACKET_L NEW_LINE* BRACKET_R
    | BRACKET_L NEW_LINE* argument (NEW_LINE* COMMA NEW_LINE* argument)* NEW_LINE* COMMA? NEW_LINE* BRACKET_R
    ;

number
    : NUMBER
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

BOOLEAN
    : ('true' | 'false')
    ;

IDENTIFIER
    : [A-Za-z_] (('-' | '.')? [A-Za-z0-9_])*
    ;
STRING
    : '"' ([ !#-[\]-~] | '\\"')* '"'
    ;
NUMBER // This isn't the exact regex for JSON numbers, but this regex aims for something like: https://www.json.org/json-en.html
    : ('-' | '+')? ('0' | [1-9] [0-9]*) ('.' [0-9]+)? ([eE] ('-' | '+')? [0-9]+)?
    ;

MULTI_LINE_COMMENT
    : '/*' .*? '*/' -> skip
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
