grammar tlv;

@lexer::header {
package com.comcast.oscar.parser;
}

@parser::header {
package com.comcast.oscar.parser;
}

WS            : [ \t\r\n]+ -> skip;
WS_NO_SKIP    : [ \t\r\n]+;      
SPACE         : ' ';
NO_WS         : '';
LBRACE        : '{';
RBRACE        : '}';
SEMICOLON     : ';';
LPARENTH      : '(';
RPARENTH      : ')';
COMMA         : ',';
DOT           : '.';
COLON         : ':';
SINGLE_QUOTE  : '\'';
DOUBLE_QUOTE  : '"';
LBRACKET      : '\[';
RBRACKET      : '\]';
TICK          : '`';
PLUS		  : '+';

/* ************************************************************************************************
*                                     Number Parsers
**************************************************************************************************/
INT           : [0-9]+;
SINGLE_INT    : [0-9];
SINGLE_OCTET  : ([01]?SINGLE_INT SINGLE_INT?|'2'[0-4]SINGLE_INT|'25'[0-5]);
IP_PORT_RANGE : ([0-9]?SINGLE_INT SINGLE_INT?|'6'[0-4]SINGLE_INT|'65'[0-4]SINGLE_INT|'655'[0-2][0-9]SINGLE_INT|'6553'[0-5]);

/* ************************************************************************************************
*                                     Hex Parsers
**************************************************************************************************/
HEX_NIBBLE            : [0-9a-fA-F];
HEX_NIBBLE_UPPERCASE  : [0-9A-F];
HEX_NIBBLE_LOWERCASE  : [0-9a-f];

HEX_BYTE              : HEX_NIBBLE HEX_NIBBLE;
HEX_BYTE_UPPER_CASE   : HEX_NIBBLE_UPPERCASE HEX_NIBBLE_UPPERCASE;
HEX_BYTE_LOWER_CASE   : HEX_NIBBLE_LOWERCASE HEX_NIBBLE_LOWERCASE;

/* ************************************************************************************************
*                                     MAC ADDRESS Parsers
**************************************************************************************************/


/* ************************************************************************************************
*                                     Code Block Tokens
**************************************************************************************************
*
* Various Format Values: 
*     (00000001)(00000010)(00000011)(00000100)
*     (1,2)(3,4)(5,6)(7,8)
*     (03:FF:FF:FF:FF:FF)(04:FF:FF:FF:FF:FF)
*     0.0.0.0(0)
*     2001:558:0:0:0:0:0:0(0)
*     56:A8:B2:47:5B:3B:D7:40:DA:51:3C:2B:24:02:DB:4C
*
***************************************************************************************************/
// Top Level TLV Name or Sub TLV Name
ALPHA_NUMERIC           : ([a-zA-Z0-9]);

//mgarcia - 150416 - Added $
//mgarcia - 150724 - Added +
IDENTIFIER              : ([a-zA-Z0-9]|'-'|'_'|'\''|'$'|'.'|'+')+;

SINGLE_QUOTE_STRING     : SINGLE_QUOTE IDENTIFIER SINGLE_QUOTE;

EMBEDDED_STRING         : (SINGLE_QUOTE_STRING DOT);

HEX_ARRAY               : (LBRACKET ((HEX_BYTE COLON)|(HEX_BYTE))+ RBRACKET);

//(00000001)(00000010)(00000011)(00000100)
VALUE_BIT_ARRAY : ('('([0]|[1])+')')+;

//(1,2)(3,4)(5,6)(7,8)
VALUE_DOUBLE_BYTE_ARRAY : ('('([01]?SINGLE_INT SINGLE_INT?|'2'[0-4]SINGLE_INT|'25'[0-5])','([01]?SINGLE_INT SINGLE_INT?|'2'[0-4] SINGLE_INT |'25'[0-5])')')+;

//(03:FF:FF:FF:FF:FF)(04:FF:FF:FF:FF:FF)
MAC_ADDRESS_ARRAY : (LPARENTH ((HEX_BYTE COLON)|(HEX_BYTE))+ RPARENTH)+;

//0.0.0.0
IPv4_ADDRESS  : ((SINGLE_OCTET DOT) | SINGLE_OCTET)+;

//0.0.0.0(0)
IPv4_TRANSPORT_ADDRESS : ((SINGLE_OCTET DOT) |  SINGLE_OCTET)+ LPARENTH IP_PORT_RANGE RPARENTH;

//2001:558:0:0:0:0:0:0
IPv6_ADDRESS : (HEX_NIBBLE+|(HEX_NIBBLE+ COLON))+;

//2001:558:0:0:0:0:0:0(0)
IPv6_TRANSPORT_ADDRESS : IPv6_ADDRESS LPARENTH IP_PORT_RANGE RPARENTH;                           


/* ******************************************************************************
                               Remark or Comment Fields
*******************************************************************************/
                    
//SINGLE_LINE_COMMENT : '//' (~[\n\r])* ('\n' | '\r' | '\r\n')? -> skip;

MULTI_LINE_COMMENT  : '/*' .*? '*/' -> skip;

/* ******************************************************************************
*                                   SNMP
**********************************************************************************/                                    
// .1.
INT_OID                 : ('.' [0-9]+ '.');

//  sysContact.
STRING_OID              : (IDENTIFIER DOT);

//  ''.
EMBEDDED_NO_STRING      : (SINGLE_QUOTE_NO_STRING DOT);
SINGLE_QUOTE_NO_STRING  : SINGLE_QUOTE SINGLE_QUOTE;

/********************************SNMP OID PARSER********************************                    
*
*   1 - vacmSecurityToGroupStorageType.2.'readwritesec'
*   2 - docsDevFilterLLCStatus.20
*   3 - vacmAccessStorageType.'readwritegroup'.''.2.noAuthNoPriv
*   
********************************************************************************/
SNMP_OID_1                : (
                            ( 
                              INT_OID 
                |             EMBEDDED_STRING 
                |             STRING_OID 
                |             EMBEDDED_NO_STRING
                            )+ 
                            (
                              INT
                |             IDENTIFIER
                |             SINGLE_QUOTE_STRING
                            )
                          );

SNMP_OID                  : (ALPHA_NUMERIC|'.'|'"'|'\''|'('|')')+?;

/* *****************************************************************************
                               START OF PARSER
*******************************************************************************/
configType      : (
                    'Main'
                |   'Docsis'
                |   'PacketCable-1.0'
                |   'PacketCable-1.5'
                |   'PacketCable-2.0'
                |   'DPoE'
                  );

//Start TLV Parsing
startTLV        : (
                    tlv 
                |   sub 
                |   emptyTLV 
                |   snmp 
                  ); 

//Generic TLV Parsers
tlv             : IDENTIFIER typeValue;
sTlv            : IDENTIFIER subTypeValue;

//The Name of the Type To be later looked up in the Dictionary
type :  IDENTIFIER;

//Value that is inserted in the JSON Object Dictionary
typeValue       : (
                      INT                     
                |     IDENTIFIER              
                |     VALUE_BIT_ARRAY         
                |     VALUE_DOUBLE_BYTE_ARRAY 
                |     MAC_ADDRESS_ARRAY       
                |     IPv4_TRANSPORT_ADDRESS  
                |     IPv6_TRANSPORT_ADDRESS  
                |     IPv6_ADDRESS            
                |     IPv4_ADDRESS
                |     HEX_ARRAY
                           
                  ) (
                      SEMICOLON | 
                      (
                        SEMICOLON (
                                    MULTI_LINE_COMMENT
                                  )
                      )
                  );

subTypeValue    : (
                      INT                     
                |     IDENTIFIER              
                |     VALUE_BIT_ARRAY         
                |     VALUE_DOUBLE_BYTE_ARRAY 
                |     MAC_ADDRESS_ARRAY       
                |     IPv4_TRANSPORT_ADDRESS  
                |     IPv6_TRANSPORT_ADDRESS  
                |     IPv6_ADDRESS            
                |     IPv4_ADDRESS
                |     HEX_ARRAY
                  ) (
                      SEMICOLON | 
                      (
                        SEMICOLON (
                                    MULTI_LINE_COMMENT
                                  )
                      )
                  );

// Sub TLV embedded in TopLevel TLV
subTLV          : (
                    sTlv 
                |   sub 
                |   emptyTLV 
                |   snmp 
                  ); 

subType :  IDENTIFIER;

sub : subType LBRACE subTLV+ RBRACE;


// No Value Found in TLV
emptyTLV : IDENTIFIER (SEMICOLON | (SEMICOLON (MULTI_LINE_COMMENT)));

snmpType        :         (
                            ('Snmp11')
                |           ('Snmp64')
                          );
                          
// ObjectID to pass to SNMP4J Class to be convered to BER Encoding
oid             :         (.|'.'|'"'|'\''|'('|')')+?;

// Reserve Words for SNMP DataTypes
dataType        :         (
                            ('Integer')
                |           ('Integer32')
                |           ('Counter')
                |           ('Counter32')
                |           ('Gauge')
                |           ('Gauge32')
                |           ('TimeTicks')
                |           ('IpAddress')
                |           ('OctetString')
                |           ('HexString')                
                          );

value           :         (.|'@'|'='|'/')+?;

snmp            :         ( 
                              snmpType 
                              oid 
                              dataType 
                              DOUBLE_QUOTE 
                              value 
                              DOUBLE_QUOTE 
                              (
                                  SEMICOLON 
                                | (SEMICOLON (MULTI_LINE_COMMENT))
                              )
                          );


/* *********************************BEGIN***************************************/
begin           :         configType LBRACE startTLV+ RBRACE; 
                          