grammar TestSuite;

suite:
	'Suite' IDENTIFIER '{'
		(suite | step)* 
	'}'
;

step:
	IDENTIFIER IDENTIFIER '{'
		(assertion | attribute)* 
	'}'
;

assertion: 'assert' IDENTIFIER VERB value;

attribute: IDENTIFIER ':' value;

value: NUMBER | STRING;

STRING : DQUOTE (ESC | ~["\\])* DQUOTE;

VERB: 'Contains' | 'Equals';

IDENTIFIER: [A-Za-z] [._\-A-Za-z0-9]*;

NUMBER: '-'? INT '.' [0-9]+ EXP? | '-'? INT EXP | '-'? INT;

fragment DQUOTE: '"';

fragment ESC: '\\' (["\\/bfnrt] | UNICODE);

fragment UNICODE: 'u' HEX HEX HEX HEX;

fragment HEX: [0-9a-fA-F];

fragment INT: '0' | [1-9] [0-9]*; // no leading zeros

fragment EXP: [Ee] [+\-]? INT; // \- since - means "range" inside [...]

// Just ignore WhiteSpaces
WS: [ \t\r\n]+ -> skip;