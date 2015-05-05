// Generated from C:\Users\mgarci00\Documents\Workspace\Oscar\ANTLR\grammar\tlv.g4 by ANTLR 4.1

package com.comcast.oscar.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class tlvParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__20=1, T__19=2, T__18=3, T__17=4, T__16=5, T__15=6, T__14=7, T__13=8, 
		T__12=9, T__11=10, T__10=11, T__9=12, T__8=13, T__7=14, T__6=15, T__5=16, 
		T__4=17, T__3=18, T__2=19, T__1=20, T__0=21, WS=22, WS_NO_SKIP=23, SPACE=24, 
		NO_WS=25, LBRACE=26, RBRACE=27, SEMICOLON=28, LPARENTH=29, RPARENTH=30, 
		COMMA=31, DOT=32, COLON=33, SINGLE_QUOTE=34, DOUBLE_QUOTE=35, LBRACKET=36, 
		RBRACKET=37, TICK=38, INT=39, SINGLE_INT=40, SINGLE_OCTET=41, IP_PORT_RANGE=42, 
		HEX_NIBBLE=43, HEX_NIBBLE_UPPERCASE=44, HEX_NIBBLE_LOWERCASE=45, HEX_BYTE=46, 
		HEX_BYTE_UPPER_CASE=47, HEX_BYTE_LOWER_CASE=48, ALPHA_NUMERIC=49, IDENTIFIER=50, 
		SINGLE_QUOTE_STRING=51, EMBEDDED_STRING=52, HEX_ARRAY=53, VALUE_BIT_ARRAY=54, 
		VALUE_DOUBLE_BYTE_ARRAY=55, MAC_ADDRESS_ARRAY=56, IPv4_ADDRESS=57, IPv4_TRANSPORT_ADDRESS=58, 
		IPv6_ADDRESS=59, IPv6_TRANSPORT_ADDRESS=60, MULTI_LINE_COMMENT=61, INT_OID=62, 
		STRING_OID=63, EMBEDDED_NO_STRING=64, SINGLE_QUOTE_NO_STRING=65, SNMP_OID_1=66, 
		SNMP_OID=67;
	public static final String[] tokenNames = {
		"<INVALID>", "'/'", "'Counter32'", "'HexString'", "'OctetString'", "'Snmp11'", 
		"'Docsis'", "'='", "'Integer'", "'DPoE'", "'IpAddress'", "'PacketCable-1.0'", 
		"'Counter'", "'Integer32'", "'TimeTicks'", "'Snmp64'", "'Gauge'", "'PacketCable-1.5'", 
		"'Main'", "'@'", "'PacketCable-2.0'", "'Gauge32'", "WS", "WS_NO_SKIP", 
		"' '", "''", "'{'", "'}'", "';'", "'('", "')'", "','", "'.'", "':'", "'''", 
		"'\"'", "'['", "']'", "'`'", "INT", "SINGLE_INT", "SINGLE_OCTET", "IP_PORT_RANGE", 
		"HEX_NIBBLE", "HEX_NIBBLE_UPPERCASE", "HEX_NIBBLE_LOWERCASE", "HEX_BYTE", 
		"HEX_BYTE_UPPER_CASE", "HEX_BYTE_LOWER_CASE", "ALPHA_NUMERIC", "IDENTIFIER", 
		"SINGLE_QUOTE_STRING", "EMBEDDED_STRING", "HEX_ARRAY", "VALUE_BIT_ARRAY", 
		"VALUE_DOUBLE_BYTE_ARRAY", "MAC_ADDRESS_ARRAY", "IPv4_ADDRESS", "IPv4_TRANSPORT_ADDRESS", 
		"IPv6_ADDRESS", "IPv6_TRANSPORT_ADDRESS", "MULTI_LINE_COMMENT", "INT_OID", 
		"STRING_OID", "EMBEDDED_NO_STRING", "SINGLE_QUOTE_NO_STRING", "SNMP_OID_1", 
		"SNMP_OID"
	};
	public static final int
		RULE_configType = 0, RULE_startTLV = 1, RULE_tlv = 2, RULE_sTlv = 3, RULE_type = 4, 
		RULE_typeValue = 5, RULE_subTypeValue = 6, RULE_subTLV = 7, RULE_subType = 8, 
		RULE_sub = 9, RULE_emptyTLV = 10, RULE_snmpType = 11, RULE_oid = 12, RULE_dataType = 13, 
		RULE_value = 14, RULE_snmp = 15, RULE_begin = 16;
	public static final String[] ruleNames = {
		"configType", "startTLV", "tlv", "sTlv", "type", "typeValue", "subTypeValue", 
		"subTLV", "subType", "sub", "emptyTLV", "snmpType", "oid", "dataType", 
		"value", "snmp", "begin"
	};

	@Override
	public String getGrammarFileName() { return "tlv.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public tlvParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ConfigTypeContext extends ParserRuleContext {
		public ConfigTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_configType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterConfigType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitConfigType(this);
		}
	}

	public final ConfigTypeContext configType() throws RecognitionException {
		ConfigTypeContext _localctx = new ConfigTypeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_configType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(34);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 6) | (1L << 9) | (1L << 11) | (1L << 17) | (1L << 18) | (1L << 20))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StartTLVContext extends ParserRuleContext {
		public TlvContext tlv() {
			return getRuleContext(TlvContext.class,0);
		}
		public SubContext sub() {
			return getRuleContext(SubContext.class,0);
		}
		public EmptyTLVContext emptyTLV() {
			return getRuleContext(EmptyTLVContext.class,0);
		}
		public SnmpContext snmp() {
			return getRuleContext(SnmpContext.class,0);
		}
		public StartTLVContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_startTLV; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterStartTLV(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitStartTLV(this);
		}
	}

	public final StartTLVContext startTLV() throws RecognitionException {
		StartTLVContext _localctx = new StartTLVContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_startTLV);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(36); tlv();
				}
				break;

			case 2:
				{
				setState(37); sub();
				}
				break;

			case 3:
				{
				setState(38); emptyTLV();
				}
				break;

			case 4:
				{
				setState(39); snmp();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TlvContext extends ParserRuleContext {
		public TypeValueContext typeValue() {
			return getRuleContext(TypeValueContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TlvContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tlv; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterTlv(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitTlv(this);
		}
	}

	public final TlvContext tlv() throws RecognitionException {
		TlvContext _localctx = new TlvContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_tlv);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42); match(IDENTIFIER);
			setState(43); typeValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class STlvContext extends ParserRuleContext {
		public SubTypeValueContext subTypeValue() {
			return getRuleContext(SubTypeValueContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public STlvContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sTlv; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSTlv(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSTlv(this);
		}
	}

	public final STlvContext sTlv() throws RecognitionException {
		STlvContext _localctx = new STlvContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_sTlv);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45); match(IDENTIFIER);
			setState(46); subTypeValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48); match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeValueContext extends ParserRuleContext {
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
		public TerminalNode MAC_ADDRESS_ARRAY() { return getToken(tlvParser.MAC_ADDRESS_ARRAY, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode IPv6_ADDRESS() { return getToken(tlvParser.IPv6_ADDRESS, 0); }
		public TerminalNode IPv4_ADDRESS() { return getToken(tlvParser.IPv4_ADDRESS, 0); }
		public TerminalNode IPv4_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv4_TRANSPORT_ADDRESS, 0); }
		public TerminalNode HEX_ARRAY() { return getToken(tlvParser.HEX_ARRAY, 0); }
		public TerminalNode IPv6_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv6_TRANSPORT_ADDRESS, 0); }
		public TerminalNode INT() { return getToken(tlvParser.INT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TerminalNode VALUE_BIT_ARRAY() { return getToken(tlvParser.VALUE_BIT_ARRAY, 0); }
		public TerminalNode VALUE_DOUBLE_BYTE_ARRAY() { return getToken(tlvParser.VALUE_DOUBLE_BYTE_ARRAY, 0); }
		public TypeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterTypeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitTypeValue(this);
		}
	}

	public final TypeValueContext typeValue() throws RecognitionException {
		TypeValueContext _localctx = new TypeValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_typeValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << IDENTIFIER) | (1L << HEX_ARRAY) | (1L << VALUE_BIT_ARRAY) | (1L << VALUE_DOUBLE_BYTE_ARRAY) | (1L << MAC_ADDRESS_ARRAY) | (1L << IPv4_ADDRESS) | (1L << IPv4_TRANSPORT_ADDRESS) | (1L << IPv6_ADDRESS) | (1L << IPv6_TRANSPORT_ADDRESS))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(54);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(51); match(SEMICOLON);
				}
				break;

			case 2:
				{
				{
				setState(52); match(SEMICOLON);
				{
				setState(53); match(MULTI_LINE_COMMENT);
				}
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubTypeValueContext extends ParserRuleContext {
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
		public TerminalNode MAC_ADDRESS_ARRAY() { return getToken(tlvParser.MAC_ADDRESS_ARRAY, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode IPv6_ADDRESS() { return getToken(tlvParser.IPv6_ADDRESS, 0); }
		public TerminalNode IPv4_ADDRESS() { return getToken(tlvParser.IPv4_ADDRESS, 0); }
		public TerminalNode IPv4_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv4_TRANSPORT_ADDRESS, 0); }
		public TerminalNode HEX_ARRAY() { return getToken(tlvParser.HEX_ARRAY, 0); }
		public TerminalNode IPv6_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv6_TRANSPORT_ADDRESS, 0); }
		public TerminalNode INT() { return getToken(tlvParser.INT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TerminalNode VALUE_BIT_ARRAY() { return getToken(tlvParser.VALUE_BIT_ARRAY, 0); }
		public TerminalNode VALUE_DOUBLE_BYTE_ARRAY() { return getToken(tlvParser.VALUE_DOUBLE_BYTE_ARRAY, 0); }
		public SubTypeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subTypeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSubTypeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSubTypeValue(this);
		}
	}

	public final SubTypeValueContext subTypeValue() throws RecognitionException {
		SubTypeValueContext _localctx = new SubTypeValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_subTypeValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << IDENTIFIER) | (1L << HEX_ARRAY) | (1L << VALUE_BIT_ARRAY) | (1L << VALUE_DOUBLE_BYTE_ARRAY) | (1L << MAC_ADDRESS_ARRAY) | (1L << IPv4_ADDRESS) | (1L << IPv4_TRANSPORT_ADDRESS) | (1L << IPv6_ADDRESS) | (1L << IPv6_TRANSPORT_ADDRESS))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(60);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(57); match(SEMICOLON);
				}
				break;

			case 2:
				{
				{
				setState(58); match(SEMICOLON);
				{
				setState(59); match(MULTI_LINE_COMMENT);
				}
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubTLVContext extends ParserRuleContext {
		public STlvContext sTlv() {
			return getRuleContext(STlvContext.class,0);
		}
		public SubContext sub() {
			return getRuleContext(SubContext.class,0);
		}
		public EmptyTLVContext emptyTLV() {
			return getRuleContext(EmptyTLVContext.class,0);
		}
		public SnmpContext snmp() {
			return getRuleContext(SnmpContext.class,0);
		}
		public SubTLVContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subTLV; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSubTLV(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSubTLV(this);
		}
	}

	public final SubTLVContext subTLV() throws RecognitionException {
		SubTLVContext _localctx = new SubTLVContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_subTLV);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(62); sTlv();
				}
				break;

			case 2:
				{
				setState(63); sub();
				}
				break;

			case 3:
				{
				setState(64); emptyTLV();
				}
				break;

			case 4:
				{
				setState(65); snmp();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubTypeContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public SubTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_subType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSubType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSubType(this);
		}
	}

	public final SubTypeContext subType() throws RecognitionException {
		SubTypeContext _localctx = new SubTypeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_subType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68); match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SubContext extends ParserRuleContext {
		public TerminalNode RBRACE() { return getToken(tlvParser.RBRACE, 0); }
		public SubTLVContext subTLV(int i) {
			return getRuleContext(SubTLVContext.class,i);
		}
		public TerminalNode LBRACE() { return getToken(tlvParser.LBRACE, 0); }
		public List<SubTLVContext> subTLV() {
			return getRuleContexts(SubTLVContext.class);
		}
		public SubTypeContext subType() {
			return getRuleContext(SubTypeContext.class,0);
		}
		public SubContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sub; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSub(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSub(this);
		}
	}

	public final SubContext sub() throws RecognitionException {
		SubContext _localctx = new SubContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_sub);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); subType();
			setState(71); match(LBRACE);
			setState(73); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(72); subTLV();
				}
				}
				setState(75); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 5) | (1L << 15) | (1L << IDENTIFIER))) != 0) );
			setState(77); match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EmptyTLVContext extends ParserRuleContext {
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public EmptyTLVContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emptyTLV; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterEmptyTLV(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitEmptyTLV(this);
		}
	}

	public final EmptyTLVContext emptyTLV() throws RecognitionException {
		EmptyTLVContext _localctx = new EmptyTLVContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_emptyTLV);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79); match(IDENTIFIER);
			setState(83);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(80); match(SEMICOLON);
				}
				break;

			case 2:
				{
				{
				setState(81); match(SEMICOLON);
				{
				setState(82); match(MULTI_LINE_COMMENT);
				}
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SnmpTypeContext extends ParserRuleContext {
		public SnmpTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snmpType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSnmpType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSnmpType(this);
		}
	}

	public final SnmpTypeContext snmpType() throws RecognitionException {
		SnmpTypeContext _localctx = new SnmpTypeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_snmpType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			switch (_input.LA(1)) {
			case 5:
				{
				{
				setState(85); match(5);
				}
				}
				break;
			case 15:
				{
				{
				setState(86); match(15);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OidContext extends ParserRuleContext {
		public OidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterOid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitOid(this);
		}
	}

	public final OidContext oid() throws RecognitionException {
		OidContext _localctx = new OidContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_oid);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(95); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			do {
				switch (_alt) {
				case 1+1:
					{
					setState(95);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						setState(89);
						matchWildcard();
						}
						break;

					case 2:
						{
						setState(90); match(DOT);
						}
						break;

					case 3:
						{
						setState(91); match(DOUBLE_QUOTE);
						}
						break;

					case 4:
						{
						setState(92); match(SINGLE_QUOTE);
						}
						break;

					case 5:
						{
						setState(93); match(LPARENTH);
						}
						break;

					case 6:
						{
						setState(94); match(RPARENTH);
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(97); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			} while ( _alt!=1 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataTypeContext extends ParserRuleContext {
		public DataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitDataType(this);
		}
	}

	public final DataTypeContext dataType() throws RecognitionException {
		DataTypeContext _localctx = new DataTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_dataType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			switch (_input.LA(1)) {
			case 8:
				{
				{
				setState(99); match(8);
				}
				}
				break;
			case 13:
				{
				{
				setState(100); match(13);
				}
				}
				break;
			case 12:
				{
				{
				setState(101); match(12);
				}
				}
				break;
			case 2:
				{
				{
				setState(102); match(2);
				}
				}
				break;
			case 16:
				{
				{
				setState(103); match(16);
				}
				}
				break;
			case 21:
				{
				{
				setState(104); match(21);
				}
				}
				break;
			case 14:
				{
				{
				setState(105); match(14);
				}
				}
				break;
			case 10:
				{
				{
				setState(106); match(10);
				}
				}
				break;
			case 4:
				{
				{
				setState(107); match(4);
				}
				}
				break;
			case 3:
				{
				{
				setState(108); match(3);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitValue(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_value);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(115); 
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			do {
				switch (_alt) {
				case 1+1:
					{
					setState(115);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						setState(111);
						matchWildcard();
						}
						break;

					case 2:
						{
						setState(112); match(19);
						}
						break;

					case 3:
						{
						setState(113); match(7);
						}
						break;

					case 4:
						{
						setState(114); match(1);
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(117); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			} while ( _alt!=1 && _alt!=-1 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SnmpContext extends ParserRuleContext {
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public OidContext oid() {
			return getRuleContext(OidContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public SnmpTypeContext snmpType() {
			return getRuleContext(SnmpTypeContext.class,0);
		}
		public List<TerminalNode> DOUBLE_QUOTE() { return getTokens(tlvParser.DOUBLE_QUOTE); }
		public TerminalNode DOUBLE_QUOTE(int i) {
			return getToken(tlvParser.DOUBLE_QUOTE, i);
		}
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public SnmpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_snmp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterSnmp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitSnmp(this);
		}
	}

	public final SnmpContext snmp() throws RecognitionException {
		SnmpContext _localctx = new SnmpContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_snmp);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(119); snmpType();
			setState(120); oid();
			setState(121); dataType();
			setState(122); match(DOUBLE_QUOTE);
			setState(123); value();
			setState(124); match(DOUBLE_QUOTE);
			setState(128);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(125); match(SEMICOLON);
				}
				break;

			case 2:
				{
				{
				setState(126); match(SEMICOLON);
				{
				setState(127); match(MULTI_LINE_COMMENT);
				}
				}
				}
				break;
			}
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BeginContext extends ParserRuleContext {
		public StartTLVContext startTLV(int i) {
			return getRuleContext(StartTLVContext.class,i);
		}
		public ConfigTypeContext configType() {
			return getRuleContext(ConfigTypeContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(tlvParser.RBRACE, 0); }
		public List<StartTLVContext> startTLV() {
			return getRuleContexts(StartTLVContext.class);
		}
		public TerminalNode LBRACE() { return getToken(tlvParser.LBRACE, 0); }
		public BeginContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_begin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).enterBegin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof tlvListener ) ((tlvListener)listener).exitBegin(this);
		}
	}

	public final BeginContext begin() throws RecognitionException {
		BeginContext _localctx = new BeginContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_begin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130); configType();
			setState(131); match(LBRACE);
			setState(133); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(132); startTLV();
				}
				}
				setState(135); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << 5) | (1L << 15) | (1L << IDENTIFIER))) != 0) );
			setState(137); match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3E\u008e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\3\2\3\2\3\3\3\3\3\3\3\3\5\3+\n\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7"+
		"\3\7\3\7\3\7\5\79\n\7\3\b\3\b\3\b\3\b\5\b?\n\b\3\t\3\t\3\t\3\t\5\tE\n"+
		"\t\3\n\3\n\3\13\3\13\3\13\6\13L\n\13\r\13\16\13M\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\5\fV\n\f\3\r\3\r\5\rZ\n\r\3\16\3\16\3\16\3\16\3\16\3\16\6\16b\n\16"+
		"\r\16\16\16c\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17p\n"+
		"\17\3\20\3\20\3\20\3\20\6\20v\n\20\r\20\16\20w\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\5\21\u0083\n\21\3\22\3\22\3\22\6\22\u0088\n\22\r"+
		"\22\16\22\u0089\3\22\3\22\3\22\4cw\23\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"\2\4\7\2\b\b\13\13\r\r\23\24\26\26\5\2))\64\64\67>\u009c\2$\3"+
		"\2\2\2\4*\3\2\2\2\6,\3\2\2\2\b/\3\2\2\2\n\62\3\2\2\2\f\64\3\2\2\2\16:"+
		"\3\2\2\2\20D\3\2\2\2\22F\3\2\2\2\24H\3\2\2\2\26Q\3\2\2\2\30Y\3\2\2\2\32"+
		"a\3\2\2\2\34o\3\2\2\2\36u\3\2\2\2 y\3\2\2\2\"\u0084\3\2\2\2$%\t\2\2\2"+
		"%\3\3\2\2\2&+\5\6\4\2\'+\5\24\13\2(+\5\26\f\2)+\5 \21\2*&\3\2\2\2*\'\3"+
		"\2\2\2*(\3\2\2\2*)\3\2\2\2+\5\3\2\2\2,-\7\64\2\2-.\5\f\7\2.\7\3\2\2\2"+
		"/\60\7\64\2\2\60\61\5\16\b\2\61\t\3\2\2\2\62\63\7\64\2\2\63\13\3\2\2\2"+
		"\648\t\3\2\2\659\7\36\2\2\66\67\7\36\2\2\679\7?\2\28\65\3\2\2\28\66\3"+
		"\2\2\29\r\3\2\2\2:>\t\3\2\2;?\7\36\2\2<=\7\36\2\2=?\7?\2\2>;\3\2\2\2>"+
		"<\3\2\2\2?\17\3\2\2\2@E\5\b\5\2AE\5\24\13\2BE\5\26\f\2CE\5 \21\2D@\3\2"+
		"\2\2DA\3\2\2\2DB\3\2\2\2DC\3\2\2\2E\21\3\2\2\2FG\7\64\2\2G\23\3\2\2\2"+
		"HI\5\22\n\2IK\7\34\2\2JL\5\20\t\2KJ\3\2\2\2LM\3\2\2\2MK\3\2\2\2MN\3\2"+
		"\2\2NO\3\2\2\2OP\7\35\2\2P\25\3\2\2\2QU\7\64\2\2RV\7\36\2\2ST\7\36\2\2"+
		"TV\7?\2\2UR\3\2\2\2US\3\2\2\2V\27\3\2\2\2WZ\7\7\2\2XZ\7\21\2\2YW\3\2\2"+
		"\2YX\3\2\2\2Z\31\3\2\2\2[b\13\2\2\2\\b\7\"\2\2]b\7%\2\2^b\7$\2\2_b\7\37"+
		"\2\2`b\7 \2\2a[\3\2\2\2a\\\3\2\2\2a]\3\2\2\2a^\3\2\2\2a_\3\2\2\2a`\3\2"+
		"\2\2bc\3\2\2\2cd\3\2\2\2ca\3\2\2\2d\33\3\2\2\2ep\7\n\2\2fp\7\17\2\2gp"+
		"\7\16\2\2hp\7\4\2\2ip\7\22\2\2jp\7\27\2\2kp\7\20\2\2lp\7\f\2\2mp\7\6\2"+
		"\2np\7\5\2\2oe\3\2\2\2of\3\2\2\2og\3\2\2\2oh\3\2\2\2oi\3\2\2\2oj\3\2\2"+
		"\2ok\3\2\2\2ol\3\2\2\2om\3\2\2\2on\3\2\2\2p\35\3\2\2\2qv\13\2\2\2rv\7"+
		"\25\2\2sv\7\t\2\2tv\7\3\2\2uq\3\2\2\2ur\3\2\2\2us\3\2\2\2ut\3\2\2\2vw"+
		"\3\2\2\2wx\3\2\2\2wu\3\2\2\2x\37\3\2\2\2yz\5\30\r\2z{\5\32\16\2{|\5\34"+
		"\17\2|}\7%\2\2}~\5\36\20\2~\u0082\7%\2\2\177\u0083\7\36\2\2\u0080\u0081"+
		"\7\36\2\2\u0081\u0083\7?\2\2\u0082\177\3\2\2\2\u0082\u0080\3\2\2\2\u0083"+
		"!\3\2\2\2\u0084\u0085\5\2\2\2\u0085\u0087\7\34\2\2\u0086\u0088\5\4\3\2"+
		"\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a"+
		"\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\7\35\2\2\u008c#\3\2\2\2\20*8"+
		">DMUYacouw\u0082\u0089";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}