package com.comcast.oscar.utilities;

import java.util.StringTokenizer;

public class PrettyPrint {

	private static final String START_INDENT_TOKEN	= "{";
	private static final String END_INDENT_TOKEN 	= "}";
	private static final String NEW_LINE_TOKEN 		= ";";
	private static final String OPEN_COMMENT_SLASH 	= "/*";
	private static final String CLOSE_COMMENT_SLASH = "*/";
	
	private String sInputCode ;
	private String sOutputCode ;
	private int iIndentCurrentDepth = 0;

	
	/**
	 * 
	 * @param sbCode
	 */
	public PrettyPrint(String sInputCode) {
		
		this.sInputCode = sInputCode;
				
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
		
		//Remove all long whitespace and clean up each token
		this.sInputCode = this.sInputCode	.replaceAll("\\s+", " ")
											.replaceAll("\\*/", " */")
											.replaceAll(";", " ;");
		
		//Create token Stream..bringing legacy back!!!!
		StringTokenizer stConfiguration = new StringTokenizer(sInputCode);
		
		//Cycle thru each token
		while (stConfiguration.hasMoreTokens()) {
			
			String sToken = stConfiguration.nextToken();

			//Check for open curly brace
			if (START_INDENT_TOKEN.equals(sToken)) {
				
				sOutputCode += indentation(iIndentCurrentDepth);
				
				sOutputCode += (sToken);
				
				sOutputCode += ('\n');
				
				iIndentCurrentDepth++;
				
				sOutputCode += (indentation(iIndentCurrentDepth));
			
			//Check for close curly brace
			} else if (END_INDENT_TOKEN.equals(sToken)) {
							
				iIndentCurrentDepth--;
				
				sOutputCode += (indentation(--iIndentCurrentDepth));
				
				sOutputCode += (sToken);
				
				sOutputCode += ('\n');
			
			//Check for semi-colon
			} else if (CLOSE_COMMENT_SLASH.equals(sToken)) {
				
				sOutputCode += (sToken);
				
				sOutputCode += ('\n');
				
				//sOutputCode += (indentation(iIndentCurrentDepth));

			} else if (NEW_LINE_TOKEN.equals(sToken)) {				
				sOutputCode += (sToken);
				sOutputCode += ('\t');
			} else {
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
