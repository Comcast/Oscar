// Generated from C:\Users\mgarci00\Documents\Workspace\Oscar\ANTLR\grammar\tlv.g4 by ANTLR 4.1

package com.comcast.oscar.parser;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tlvParser}.
 */
public interface tlvListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tlvParser#startTLV}.
	 * @param ctx the parse tree
	 */
	void enterStartTLV(@NotNull tlvParser.StartTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#startTLV}.
	 * @param ctx the parse tree
	 */
	void exitStartTLV(@NotNull tlvParser.StartTLVContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#sub}.
	 * @param ctx the parse tree
	 */
	void enterSub(@NotNull tlvParser.SubContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#sub}.
	 * @param ctx the parse tree
	 */
	void exitSub(@NotNull tlvParser.SubContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#subTLV}.
	 * @param ctx the parse tree
	 */
	void enterSubTLV(@NotNull tlvParser.SubTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subTLV}.
	 * @param ctx the parse tree
	 */
	void exitSubTLV(@NotNull tlvParser.SubTLVContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#emptyTLV}.
	 * @param ctx the parse tree
	 */
	void enterEmptyTLV(@NotNull tlvParser.EmptyTLVContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#emptyTLV}.
	 * @param ctx the parse tree
	 */
	void exitEmptyTLV(@NotNull tlvParser.EmptyTLVContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#dataType}.
	 * @param ctx the parse tree
	 */
	void enterDataType(@NotNull tlvParser.DataTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#dataType}.
	 * @param ctx the parse tree
	 */
	void exitDataType(@NotNull tlvParser.DataTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#oid}.
	 * @param ctx the parse tree
	 */
	void enterOid(@NotNull tlvParser.OidContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#oid}.
	 * @param ctx the parse tree
	 */
	void exitOid(@NotNull tlvParser.OidContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#snmp}.
	 * @param ctx the parse tree
	 */
	void enterSnmp(@NotNull tlvParser.SnmpContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#snmp}.
	 * @param ctx the parse tree
	 */
	void exitSnmp(@NotNull tlvParser.SnmpContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#configType}.
	 * @param ctx the parse tree
	 */
	void enterConfigType(@NotNull tlvParser.ConfigTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#configType}.
	 * @param ctx the parse tree
	 */
	void exitConfigType(@NotNull tlvParser.ConfigTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull tlvParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull tlvParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#subTypeValue}.
	 * @param ctx the parse tree
	 */
	void enterSubTypeValue(@NotNull tlvParser.SubTypeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subTypeValue}.
	 * @param ctx the parse tree
	 */
	void exitSubTypeValue(@NotNull tlvParser.SubTypeValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#typeValue}.
	 * @param ctx the parse tree
	 */
	void enterTypeValue(@NotNull tlvParser.TypeValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#typeValue}.
	 * @param ctx the parse tree
	 */
	void exitTypeValue(@NotNull tlvParser.TypeValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#subType}.
	 * @param ctx the parse tree
	 */
	void enterSubType(@NotNull tlvParser.SubTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#subType}.
	 * @param ctx the parse tree
	 */
	void exitSubType(@NotNull tlvParser.SubTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(@NotNull tlvParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(@NotNull tlvParser.ValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#begin}.
	 * @param ctx the parse tree
	 */
	void enterBegin(@NotNull tlvParser.BeginContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#begin}.
	 * @param ctx the parse tree
	 */
	void exitBegin(@NotNull tlvParser.BeginContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#sTlv}.
	 * @param ctx the parse tree
	 */
	void enterSTlv(@NotNull tlvParser.STlvContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#sTlv}.
	 * @param ctx the parse tree
	 */
	void exitSTlv(@NotNull tlvParser.STlvContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#tlv}.
	 * @param ctx the parse tree
	 */
	void enterTlv(@NotNull tlvParser.TlvContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#tlv}.
	 * @param ctx the parse tree
	 */
	void exitTlv(@NotNull tlvParser.TlvContext ctx);

	/**
	 * Enter a parse tree produced by {@link tlvParser#snmpType}.
	 * @param ctx the parse tree
	 */
	void enterSnmpType(@NotNull tlvParser.SnmpTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tlvParser#snmpType}.
	 * @param ctx the parse tree
	 */
	void exitSnmpType(@NotNull tlvParser.SnmpTypeContext ctx);
}