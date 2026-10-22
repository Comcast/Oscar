// Generated from tlv.g4 by ANTLR 4.13.2

package com.comcast.oscar.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class tlvParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, WS=21, WS_NO_SKIP=22, SPACE=23, LBRACE=24, 
		RBRACE=25, SEMICOLON=26, LPARENTH=27, RPARENTH=28, COMMA=29, DOT=30, COLON=31, 
		SINGLE_QUOTE=32, DOUBLE_QUOTE=33, QUOTED_STRING=34, LBRACKET=35, RBRACKET=36, 
		TICK=37, INT=38, SINGLE_INT=39, SINGLE_OCTET=40, IP_PORT_RANGE=41, HEX_NIBBLE=42, 
		HEX_NIBBLE_UPPERCASE=43, HEX_NIBBLE_LOWERCASE=44, HEX_BYTE=45, HEX_BYTE_UPPER_CASE=46, 
		HEX_BYTE_LOWER_CASE=47, ALPHA_NUMERIC=48, IDENTIFIER=49, SINGLE_QUOTE_STRING=50, 
		EMBEDDED_STRING=51, HEX_ARRAY=52, VALUE_BIT_ARRAY=53, VALUE_DOUBLE_BYTE_ARRAY=54, 
		MAC_ADDRESS_ARRAY=55, IPv4_ADDRESS=56, IPv4_TRANSPORT_ADDRESS=57, IPv6_ADDRESS=58, 
		IPv6_TRANSPORT_ADDRESS=59, MULTI_LINE_COMMENT=60, INT_OID=61, STRING_OID=62, 
		EMBEDDED_NO_STRING=63, SINGLE_QUOTE_NO_STRING=64, SNMP_OID_1=65, SNMP_OID=66;
	public static final int
		RULE_configType = 0, RULE_startTLV = 1, RULE_tlv = 2, RULE_sTlv = 3, RULE_type = 4, 
		RULE_typeValue = 5, RULE_subTypeValue = 6, RULE_subTLV = 7, RULE_subType = 8, 
		RULE_sub = 9, RULE_emptyTLV = 10, RULE_snmpType = 11, RULE_oid = 12, RULE_dataType = 13, 
		RULE_value = 14, RULE_snmp = 15, RULE_begin = 16;
	private static String[] makeRuleNames() {
		return new String[] {
			"configType", "startTLV", "tlv", "sTlv", "type", "typeValue", "subTypeValue", 
			"subTLV", "subType", "sub", "emptyTLV", "snmpType", "oid", "dataType", 
			"value", "snmp", "begin"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'Main'", "'Docsis'", "'Docsis-4.0'", "'PacketCable-1.0'", "'PacketCable-1.5'", 
			"'PacketCable-2.0'", "'DPoE'", "'DPoG'", "'Snmp11'", "'Snmp64'", "'Integer'", 
			"'Integer32'", "'Counter'", "'Counter32'", "'Gauge'", "'Gauge32'", "'TimeTicks'", 
			"'IpAddress'", "'OctetString'", "'HexString'", null, null, "' '", "'{'", 
			"'}'", "';'", "'('", "')'", "','", "'.'", "':'", "'''", "'\"'", null, 
			"'['", "']'", "'`'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "WS", "WS_NO_SKIP", 
			"SPACE", "LBRACE", "RBRACE", "SEMICOLON", "LPARENTH", "RPARENTH", "COMMA", 
			"DOT", "COLON", "SINGLE_QUOTE", "DOUBLE_QUOTE", "QUOTED_STRING", "LBRACKET", 
			"RBRACKET", "TICK", "INT", "SINGLE_INT", "SINGLE_OCTET", "IP_PORT_RANGE", 
			"HEX_NIBBLE", "HEX_NIBBLE_UPPERCASE", "HEX_NIBBLE_LOWERCASE", "HEX_BYTE", 
			"HEX_BYTE_UPPER_CASE", "HEX_BYTE_LOWER_CASE", "ALPHA_NUMERIC", "IDENTIFIER", 
			"SINGLE_QUOTE_STRING", "EMBEDDED_STRING", "HEX_ARRAY", "VALUE_BIT_ARRAY", 
			"VALUE_DOUBLE_BYTE_ARRAY", "MAC_ADDRESS_ARRAY", "IPv4_ADDRESS", "IPv4_TRANSPORT_ADDRESS", 
			"IPv6_ADDRESS", "IPv6_TRANSPORT_ADDRESS", "MULTI_LINE_COMMENT", "INT_OID", 
			"STRING_OID", "EMBEDDED_NO_STRING", "SINGLE_QUOTE_NO_STRING", "SNMP_OID_1", 
			"SNMP_OID"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "tlv.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public tlvParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
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
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 510L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	@SuppressWarnings("CheckReturnValue")
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
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(36);
				tlv();
				}
				break;
			case 2:
				{
				setState(37);
				sub();
				}
				break;
			case 3:
				{
				setState(38);
				emptyTLV();
				}
				break;
			case 4:
				{
				setState(39);
				snmp();
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

	@SuppressWarnings("CheckReturnValue")
	public static class TlvContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TypeValueContext typeValue() {
			return getRuleContext(TypeValueContext.class,0);
		}
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
			setState(42);
			match(IDENTIFIER);
			setState(43);
			typeValue();
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

	@SuppressWarnings("CheckReturnValue")
	public static class STlvContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public SubTypeValueContext subTypeValue() {
			return getRuleContext(SubTypeValueContext.class,0);
		}
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
			setState(45);
			match(IDENTIFIER);
			setState(46);
			subTypeValue();
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

	@SuppressWarnings("CheckReturnValue")
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
			setState(48);
			match(IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class TypeValueContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(tlvParser.INT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TerminalNode VALUE_BIT_ARRAY() { return getToken(tlvParser.VALUE_BIT_ARRAY, 0); }
		public TerminalNode VALUE_DOUBLE_BYTE_ARRAY() { return getToken(tlvParser.VALUE_DOUBLE_BYTE_ARRAY, 0); }
		public TerminalNode MAC_ADDRESS_ARRAY() { return getToken(tlvParser.MAC_ADDRESS_ARRAY, 0); }
		public TerminalNode IPv4_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv4_TRANSPORT_ADDRESS, 0); }
		public TerminalNode IPv6_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv6_TRANSPORT_ADDRESS, 0); }
		public TerminalNode IPv6_ADDRESS() { return getToken(tlvParser.IPv6_ADDRESS, 0); }
		public TerminalNode IPv4_ADDRESS() { return getToken(tlvParser.IPv4_ADDRESS, 0); }
		public TerminalNode QUOTED_STRING() { return getToken(tlvParser.QUOTED_STRING, 0); }
		public TerminalNode HEX_ARRAY() { return getToken(tlvParser.HEX_ARRAY, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
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
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1148981146990673920L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(54);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(51);
				match(SEMICOLON);
				}
				break;
			case 2:
				{
				{
				setState(52);
				match(SEMICOLON);
				{
				setState(53);
				match(MULTI_LINE_COMMENT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class SubTypeValueContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(tlvParser.INT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TerminalNode VALUE_BIT_ARRAY() { return getToken(tlvParser.VALUE_BIT_ARRAY, 0); }
		public TerminalNode VALUE_DOUBLE_BYTE_ARRAY() { return getToken(tlvParser.VALUE_DOUBLE_BYTE_ARRAY, 0); }
		public TerminalNode MAC_ADDRESS_ARRAY() { return getToken(tlvParser.MAC_ADDRESS_ARRAY, 0); }
		public TerminalNode IPv4_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv4_TRANSPORT_ADDRESS, 0); }
		public TerminalNode IPv6_TRANSPORT_ADDRESS() { return getToken(tlvParser.IPv6_TRANSPORT_ADDRESS, 0); }
		public TerminalNode IPv6_ADDRESS() { return getToken(tlvParser.IPv6_ADDRESS, 0); }
		public TerminalNode IPv4_ADDRESS() { return getToken(tlvParser.IPv4_ADDRESS, 0); }
		public TerminalNode QUOTED_STRING() { return getToken(tlvParser.QUOTED_STRING, 0); }
		public TerminalNode HEX_ARRAY() { return getToken(tlvParser.HEX_ARRAY, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
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
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1148981146990673920L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(60);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				setState(57);
				match(SEMICOLON);
				}
				break;
			case 2:
				{
				{
				setState(58);
				match(SEMICOLON);
				{
				setState(59);
				match(MULTI_LINE_COMMENT);
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

	@SuppressWarnings("CheckReturnValue")
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
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(62);
				sTlv();
				}
				break;
			case 2:
				{
				setState(63);
				sub();
				}
				break;
			case 3:
				{
				setState(64);
				emptyTLV();
				}
				break;
			case 4:
				{
				setState(65);
				snmp();
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

	@SuppressWarnings("CheckReturnValue")
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
			setState(68);
			match(IDENTIFIER);
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

	@SuppressWarnings("CheckReturnValue")
	public static class SubContext extends ParserRuleContext {
		public SubTypeContext subType() {
			return getRuleContext(SubTypeContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(tlvParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(tlvParser.RBRACE, 0); }
		public List<SubTLVContext> subTLV() {
			return getRuleContexts(SubTLVContext.class);
		}
		public SubTLVContext subTLV(int i) {
			return getRuleContext(SubTLVContext.class,i);
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
			setState(70);
			subType();
			setState(71);
			match(LBRACE);
			setState(73); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(72);
				subTLV();
				}
				}
				setState(75); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 562949953422848L) != 0) );
			setState(77);
			match(RBRACE);
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

	@SuppressWarnings("CheckReturnValue")
	public static class EmptyTLVContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(tlvParser.IDENTIFIER, 0); }
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
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
			setState(79);
			match(IDENTIFIER);
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(80);
				match(SEMICOLON);
				}
				break;
			case 2:
				{
				{
				setState(81);
				match(SEMICOLON);
				{
				setState(82);
				match(MULTI_LINE_COMMENT);
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

	@SuppressWarnings("CheckReturnValue")
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
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				{
				{
				setState(85);
				match(T__8);
				}
				}
				break;
			case T__9:
				{
				{
				setState(86);
				match(T__9);
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

	@SuppressWarnings("CheckReturnValue")
	public static class OidContext extends ParserRuleContext {
		public List<TerminalNode> DOT() { return getTokens(tlvParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(tlvParser.DOT, i);
		}
		public List<TerminalNode> DOUBLE_QUOTE() { return getTokens(tlvParser.DOUBLE_QUOTE); }
		public TerminalNode DOUBLE_QUOTE(int i) {
			return getToken(tlvParser.DOUBLE_QUOTE, i);
		}
		public List<TerminalNode> SINGLE_QUOTE() { return getTokens(tlvParser.SINGLE_QUOTE); }
		public TerminalNode SINGLE_QUOTE(int i) {
			return getToken(tlvParser.SINGLE_QUOTE, i);
		}
		public List<TerminalNode> LPARENTH() { return getTokens(tlvParser.LPARENTH); }
		public TerminalNode LPARENTH(int i) {
			return getToken(tlvParser.LPARENTH, i);
		}
		public List<TerminalNode> RPARENTH() { return getTokens(tlvParser.RPARENTH); }
		public TerminalNode RPARENTH(int i) {
			return getToken(tlvParser.RPARENTH, i);
		}
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
			_alt = 1+1;
			do {
				switch (_alt) {
				case 1+1:
					{
					setState(95);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
					case 1:
						{
						setState(89);
						matchWildcard();
						}
						break;
					case 2:
						{
						setState(90);
						match(DOT);
						}
						break;
					case 3:
						{
						setState(91);
						match(DOUBLE_QUOTE);
						}
						break;
					case 4:
						{
						setState(92);
						match(SINGLE_QUOTE);
						}
						break;
					case 5:
						{
						setState(93);
						match(LPARENTH);
						}
						break;
					case 6:
						{
						setState(94);
						match(RPARENTH);
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
			} while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	@SuppressWarnings("CheckReturnValue")
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
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__10:
				{
				{
				setState(99);
				match(T__10);
				}
				}
				break;
			case T__11:
				{
				{
				setState(100);
				match(T__11);
				}
				}
				break;
			case T__12:
				{
				{
				setState(101);
				match(T__12);
				}
				}
				break;
			case T__13:
				{
				{
				setState(102);
				match(T__13);
				}
				}
				break;
			case T__14:
				{
				{
				setState(103);
				match(T__14);
				}
				}
				break;
			case T__15:
				{
				{
				setState(104);
				match(T__15);
				}
				}
				break;
			case T__16:
				{
				{
				setState(105);
				match(T__16);
				}
				}
				break;
			case T__17:
				{
				{
				setState(106);
				match(T__17);
				}
				}
				break;
			case T__18:
				{
				{
				setState(107);
				match(T__18);
				}
				}
				break;
			case T__19:
				{
				{
				setState(108);
				match(T__19);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ValueContext extends ParserRuleContext {
		public TerminalNode QUOTED_STRING() { return getToken(tlvParser.QUOTED_STRING, 0); }
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
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(QUOTED_STRING);
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

	@SuppressWarnings("CheckReturnValue")
	public static class SnmpContext extends ParserRuleContext {
		public SnmpTypeContext snmpType() {
			return getRuleContext(SnmpTypeContext.class,0);
		}
		public OidContext oid() {
			return getRuleContext(OidContext.class,0);
		}
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(tlvParser.SEMICOLON, 0); }
		public TerminalNode MULTI_LINE_COMMENT() { return getToken(tlvParser.MULTI_LINE_COMMENT, 0); }
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
			setState(113);
			snmpType();
			setState(114);
			oid();
			setState(115);
			dataType();
			setState(116);
			value();
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(117);
				match(SEMICOLON);
				}
				break;
			case 2:
				{
				{
				setState(118);
				match(SEMICOLON);
				{
				setState(119);
				match(MULTI_LINE_COMMENT);
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

	@SuppressWarnings("CheckReturnValue")
	public static class BeginContext extends ParserRuleContext {
		public ConfigTypeContext configType() {
			return getRuleContext(ConfigTypeContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(tlvParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(tlvParser.RBRACE, 0); }
		public List<StartTLVContext> startTLV() {
			return getRuleContexts(StartTLVContext.class);
		}
		public StartTLVContext startTLV(int i) {
			return getRuleContext(StartTLVContext.class,i);
		}
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
			setState(122);
			configType();
			setState(123);
			match(LBRACE);
			setState(125); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(124);
				startTLV();
				}
				}
				setState(127); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 562949953422848L) != 0) );
			setState(129);
			match(RBRACE);
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
		"\u0004\u0001B\u0084\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0003\u0001)\b\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u00057\b\u0005"+
		"\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0003\u0006=\b\u0006"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003\u0007C\b\u0007"+
		"\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0004\tJ\b\t\u000b\t\f\tK\u0001"+
		"\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0003\nT\b\n\u0001\u000b\u0001"+
		"\u000b\u0003\u000bX\b\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0004\f`\b\f\u000b\f\f\fa\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\rn\b\r\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0003\u000fy\b\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0004\u0010~\b\u0010\u000b\u0010\f\u0010\u007f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001a\u0000\u0011\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \u0000\u0002\u0001\u0000\u0001"+
		"\b\u0004\u0000\"\"&&114;\u008e\u0000\"\u0001\u0000\u0000\u0000\u0002("+
		"\u0001\u0000\u0000\u0000\u0004*\u0001\u0000\u0000\u0000\u0006-\u0001\u0000"+
		"\u0000\u0000\b0\u0001\u0000\u0000\u0000\n2\u0001\u0000\u0000\u0000\f8"+
		"\u0001\u0000\u0000\u0000\u000eB\u0001\u0000\u0000\u0000\u0010D\u0001\u0000"+
		"\u0000\u0000\u0012F\u0001\u0000\u0000\u0000\u0014O\u0001\u0000\u0000\u0000"+
		"\u0016W\u0001\u0000\u0000\u0000\u0018_\u0001\u0000\u0000\u0000\u001am"+
		"\u0001\u0000\u0000\u0000\u001co\u0001\u0000\u0000\u0000\u001eq\u0001\u0000"+
		"\u0000\u0000 z\u0001\u0000\u0000\u0000\"#\u0007\u0000\u0000\u0000#\u0001"+
		"\u0001\u0000\u0000\u0000$)\u0003\u0004\u0002\u0000%)\u0003\u0012\t\u0000"+
		"&)\u0003\u0014\n\u0000\')\u0003\u001e\u000f\u0000($\u0001\u0000\u0000"+
		"\u0000(%\u0001\u0000\u0000\u0000(&\u0001\u0000\u0000\u0000(\'\u0001\u0000"+
		"\u0000\u0000)\u0003\u0001\u0000\u0000\u0000*+\u00051\u0000\u0000+,\u0003"+
		"\n\u0005\u0000,\u0005\u0001\u0000\u0000\u0000-.\u00051\u0000\u0000./\u0003"+
		"\f\u0006\u0000/\u0007\u0001\u0000\u0000\u000001\u00051\u0000\u00001\t"+
		"\u0001\u0000\u0000\u000026\u0007\u0001\u0000\u000037\u0005\u001a\u0000"+
		"\u000045\u0005\u001a\u0000\u000057\u0005<\u0000\u000063\u0001\u0000\u0000"+
		"\u000064\u0001\u0000\u0000\u00007\u000b\u0001\u0000\u0000\u00008<\u0007"+
		"\u0001\u0000\u00009=\u0005\u001a\u0000\u0000:;\u0005\u001a\u0000\u0000"+
		";=\u0005<\u0000\u0000<9\u0001\u0000\u0000\u0000<:\u0001\u0000\u0000\u0000"+
		"=\r\u0001\u0000\u0000\u0000>C\u0003\u0006\u0003\u0000?C\u0003\u0012\t"+
		"\u0000@C\u0003\u0014\n\u0000AC\u0003\u001e\u000f\u0000B>\u0001\u0000\u0000"+
		"\u0000B?\u0001\u0000\u0000\u0000B@\u0001\u0000\u0000\u0000BA\u0001\u0000"+
		"\u0000\u0000C\u000f\u0001\u0000\u0000\u0000DE\u00051\u0000\u0000E\u0011"+
		"\u0001\u0000\u0000\u0000FG\u0003\u0010\b\u0000GI\u0005\u0018\u0000\u0000"+
		"HJ\u0003\u000e\u0007\u0000IH\u0001\u0000\u0000\u0000JK\u0001\u0000\u0000"+
		"\u0000KI\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LM\u0001\u0000"+
		"\u0000\u0000MN\u0005\u0019\u0000\u0000N\u0013\u0001\u0000\u0000\u0000"+
		"OS\u00051\u0000\u0000PT\u0005\u001a\u0000\u0000QR\u0005\u001a\u0000\u0000"+
		"RT\u0005<\u0000\u0000SP\u0001\u0000\u0000\u0000SQ\u0001\u0000\u0000\u0000"+
		"T\u0015\u0001\u0000\u0000\u0000UX\u0005\t\u0000\u0000VX\u0005\n\u0000"+
		"\u0000WU\u0001\u0000\u0000\u0000WV\u0001\u0000\u0000\u0000X\u0017\u0001"+
		"\u0000\u0000\u0000Y`\t\u0000\u0000\u0000Z`\u0005\u001e\u0000\u0000[`\u0005"+
		"!\u0000\u0000\\`\u0005 \u0000\u0000]`\u0005\u001b\u0000\u0000^`\u0005"+
		"\u001c\u0000\u0000_Y\u0001\u0000\u0000\u0000_Z\u0001\u0000\u0000\u0000"+
		"_[\u0001\u0000\u0000\u0000_\\\u0001\u0000\u0000\u0000_]\u0001\u0000\u0000"+
		"\u0000_^\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000ab\u0001\u0000"+
		"\u0000\u0000a_\u0001\u0000\u0000\u0000b\u0019\u0001\u0000\u0000\u0000"+
		"cn\u0005\u000b\u0000\u0000dn\u0005\f\u0000\u0000en\u0005\r\u0000\u0000"+
		"fn\u0005\u000e\u0000\u0000gn\u0005\u000f\u0000\u0000hn\u0005\u0010\u0000"+
		"\u0000in\u0005\u0011\u0000\u0000jn\u0005\u0012\u0000\u0000kn\u0005\u0013"+
		"\u0000\u0000ln\u0005\u0014\u0000\u0000mc\u0001\u0000\u0000\u0000md\u0001"+
		"\u0000\u0000\u0000me\u0001\u0000\u0000\u0000mf\u0001\u0000\u0000\u0000"+
		"mg\u0001\u0000\u0000\u0000mh\u0001\u0000\u0000\u0000mi\u0001\u0000\u0000"+
		"\u0000mj\u0001\u0000\u0000\u0000mk\u0001\u0000\u0000\u0000ml\u0001\u0000"+
		"\u0000\u0000n\u001b\u0001\u0000\u0000\u0000op\u0005\"\u0000\u0000p\u001d"+
		"\u0001\u0000\u0000\u0000qr\u0003\u0016\u000b\u0000rs\u0003\u0018\f\u0000"+
		"st\u0003\u001a\r\u0000tx\u0003\u001c\u000e\u0000uy\u0005\u001a\u0000\u0000"+
		"vw\u0005\u001a\u0000\u0000wy\u0005<\u0000\u0000xu\u0001\u0000\u0000\u0000"+
		"xv\u0001\u0000\u0000\u0000y\u001f\u0001\u0000\u0000\u0000z{\u0003\u0000"+
		"\u0000\u0000{}\u0005\u0018\u0000\u0000|~\u0003\u0002\u0001\u0000}|\u0001"+
		"\u0000\u0000\u0000~\u007f\u0001\u0000\u0000\u0000\u007f}\u0001\u0000\u0000"+
		"\u0000\u007f\u0080\u0001\u0000\u0000\u0000\u0080\u0081\u0001\u0000\u0000"+
		"\u0000\u0081\u0082\u0005\u0019\u0000\u0000\u0082!\u0001\u0000\u0000\u0000"+
		"\f(6<BKSW_amx\u007f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}