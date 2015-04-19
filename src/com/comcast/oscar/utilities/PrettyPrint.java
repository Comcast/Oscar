package com.comcast.oscar.utilities;

import java.util.StringTokenizer;

public class PrettyPrint extends StringTokenizer {

	private final String START_INDENT_TOKEN		= "{";
	private final String END_INDENT_TOKEN 		= "}";
	private final String NEW_LINE_TOKEN 		= ";";
	private final String OPEN_COMMENT_SLASH 	= "/*";
	private final String CLOSE_COMMENT_SLASH 	= "*/";
	
	private String sOutputCode = "";
	private int iIndentCurrentDepth = 0;

	
	/**
	 * 
	 * @param sbCode
	 */
	public PrettyPrint(String sInputCode)  {		
		super(sInputCode	.replaceAll("\\s+", " ")
							.replaceAll("\\*/", " */")
							.replaceAll(";", " ;")
							.replaceAll("\n", "")
							.replaceAll("(\\{\\s+\\})+", "{}")
							.replaceAll("(\\w+\\s+\\{\\})+", "")		
						
				);
		
		start();
	}
	
	/**
	 * 
	 */
	public String toString() {
		return sOutputCode;
	}
	
	/**
	 * 
	 */
	private void start() {
		
		boolean boolCloseCommentSlash = false;
				
		//Cycle thru each token
		while (super.hasMoreTokens()) {
			
			String sToken = super.nextToken();
			
			//Check for open curly brace
			if (START_INDENT_TOKEN.equals(sToken)) {				
				sOutputCode += indentation(iIndentCurrentDepth);				
				sOutputCode += (sToken);				
				sOutputCode += ('\n');				
				iIndentCurrentDepth++;				
				sOutputCode += (indentation(iIndentCurrentDepth));			
			//Check for close curly brace
			} else if (END_INDENT_TOKEN.equals(sToken)) {
				sOutputCode += (indentation(--iIndentCurrentDepth));
				sOutputCode += (sToken);				
				sOutputCode += ('\n');			
			//Check for semi-colon
			} else if (CLOSE_COMMENT_SLASH.equals(sToken)) {				
				sOutputCode += (sToken);				
				sOutputCode += ('\n');						
				boolCloseCommentSlash = true;
			} else if (NEW_LINE_TOKEN.equals(sToken)) {				
				sOutputCode += (sToken);
				sOutputCode += ('\t');
			} else {				
				if (boolCloseCommentSlash) {
					sOutputCode += (indentation(iIndentCurrentDepth));
					boolCloseCommentSlash = false;
				}				
				sOutputCode += (" " + sToken);
			}	
		}	
	}
	
	/**
	 * 
	 * @param iNumberOfTabs
	 * @return String of tabs
	 */
	private String indentation(int iNumberOfTabs) {
		
		String sIndentation = "";
		
		for (int iCounter = 0 ; iCounter < iNumberOfTabs; iCounter++) {
			sIndentation += "\t";
		}
		
		return sIndentation;
		
	}
		
}
