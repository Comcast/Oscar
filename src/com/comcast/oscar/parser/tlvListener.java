// Generated from tlv.g4 by ANTLR 4.13.2

package com.comcast.oscar.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tlvParser}.
 */
public interface tlvListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tlvParser#configType}.
	 * @param ctx the parse tree
	 */
	void enterConfigType(tlvParser.ConfigTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#configType}.
	 * @param ctx the parse tree
	 */
	void exitConfigType(tlvParser.ConfigTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#startTLV}.
	 * @param ctx the parse tree
	 */
	void enterStartTLV(tlvParser.StartTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#startTLV}.
	 * @param ctx the parse tree
	 */
	void exitStartTLV(tlvParser.StartTLVContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#tlv}.
	 * @param ctx the parse tree
	 */
	void enterTlv(tlvParser.TlvContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#tlv}.
	 * @param ctx the parse tree
	 */
	void exitTlv(tlvParser.TlvContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#sTlv}.
	 * @param ctx the parse tree
	 */
	void enterSTlv(tlvParser.STlvContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#sTlv}.
	 * @param ctx the parse tree
	 */
	void exitSTlv(tlvParser.STlvContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(tlvParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(tlvParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#typeValue}.
	 * @param ctx the parse tree
	 */
	void enterTypeValue(tlvParser.TypeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#typeValue}.
	 * @param ctx the parse tree
	 */
	void exitTypeValue(tlvParser.TypeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#subTypeValue}.
	 * @param ctx the parse tree
	 */
	void enterSubTypeValue(tlvParser.SubTypeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subTypeValue}.
	 * @param ctx the parse tree
	 */
	void exitSubTypeValue(tlvParser.SubTypeValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#subTLV}.
	 * @param ctx the parse tree
	 */
	void enterSubTLV(tlvParser.SubTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subTLV}.
	 * @param ctx the parse tree
	 */
	void exitSubTLV(tlvParser.SubTLVContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#subType}.
	 * @param ctx the parse tree
	 */
	void enterSubType(tlvParser.SubTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subType}.
	 * @param ctx the parse tree
	 */
	void exitSubType(tlvParser.SubTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#sub}.
	 * @param ctx the parse tree
	 */
	void enterSub(tlvParser.SubContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#sub}.
	 * @param ctx the parse tree
	 */
	void exitSub(tlvParser.SubContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#emptyTLV}.
	 * @param ctx the parse tree
	 */
	void enterEmptyTLV(tlvParser.EmptyTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#emptyTLV}.
	 * @param ctx the parse tree
	 */
	void exitEmptyTLV(tlvParser.EmptyTLVContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#snmpType}.
	 * @param ctx the parse tree
	 */
	void enterSnmpType(tlvParser.SnmpTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#snmpType}.
	 * @param ctx the parse tree
	 */
	void exitSnmpType(tlvParser.SnmpTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#oid}.
	 * @param ctx the parse tree
	 */
	void enterOid(tlvParser.OidContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#oid}.
	 * @param ctx the parse tree
	 */
	void exitOid(tlvParser.OidContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterDataType(tlvParser.DataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitDataType(tlvParser.DataTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(tlvParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(tlvParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#snmp}.
	 * @param ctx the parse tree
	 */
	void enterSnmp(tlvParser.SnmpContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#snmp}.
	 * @param ctx the parse tree
	 */
	void exitSnmp(tlvParser.SnmpContext ctx);
	/**
	 * Enter a parse tree produced by {@link tlvParser#begin}.
	 * @param ctx the parse tree
	 */
	void enterBegin(tlvParser.BeginContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#begin}.
	 * @param ctx the parse tree
	 */
	void exitBegin(tlvParser.BeginContext ctx);
}