package com.comcast.oscar.utilities;

import java.util.StringTokenizer;

/**
 * @bannerLicense
	Copyright 2015 Comcast Cable Communications Management, LLC<br>
	___________________________________________________________________<br>
	Licensed under the Apache License, Version 2.0 (the "License")<br>
	you may not use this file except in compliance with the License.<br>
	You may obtain a copy of the License at<br>
	http://www.apache.org/licenses/LICENSE-2.0<br>
	Unless required by applicable law or agreed to in writing, software<br>
	distributed under the License is distributed on an "AS IS" BASIS,<br>
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
	See the License for the specific language governing permissions and<br>
	limitations under the License.<br>


 * @author Maurice Garcia (maurice.garcia.2015@gmail.com)
 */

public class PrettyPrint extends StringTokenizer {

	private final String START_INDENT_TOKEN		= "{";
	private final String END_INDENT_TOKEN 		= "}";
	private final String NEW_LINE_TOKEN 		= ";";
	@SuppressWarnings("unused")
	private final String OPEN_COMMENT_SLASH 	= "/*";
	private final String CLOSE_COMMENT_SLASH 	= "*/";
	
	public static Integer PARAGRAPH_OFFSET		= 80;
	
	private String sOutputCode = "";
	private int iIndentCurrentDepth = 0;
	
	/**
	 * 
	 * @param sInputCode - Configuration File in ASCII
	 */
	public PrettyPrint(String sInputCode)  {
		//This is a total Hack. This will only take care of TLV that are 4 levels deep.  Will use Matcher Check in next release
		super((sInputCode	.replaceAll("\\s+", " ")
							.replaceAll("\\*/", " */")
							.replaceAll(";", " ;")
							.replaceAll("\n", " ")
							.replaceAll("(\\w|-)+\\s+\\{\\}\\s+.\\*EOCB\\*.\\s+", "").replaceAll("\\{\\s+\\}", "{}")
							.replaceAll("(\\w|-)+\\s+\\{\\}\\s+.\\*EOCB\\*.\\s+", "").replaceAll("\\{\\s+\\}", "{}")
							.replaceAll("(\\w|-)+\\s+\\{\\}\\s+.\\*EOCB\\*.\\s+", "").replaceAll("\\{\\s+\\}", "{}")
							.replaceAll("(\\w|-)+\\s+\\{\\}\\s+.\\*EOCB\\*.\\s+", "").replaceAll("\\{\\s+\\}", "{}")
							.replaceAll(".\\*EOCB\\*.", "")
				));	
		start();
	}
	
	/**
	 * 
	 */
	public String toString() {
		return sOutputCode;
	}
	
	public static String ToParagraphForm (String sText) {
		
		sText = sText.replaceAll("\\s+", " ");
		
		StringBuilder sbText = new StringBuilder(sText);
		
		int iOffSet = PARAGRAPH_OFFSET,
			iIndex = iOffSet;
		
		while(iIndex < sbText.length()) {
			
			if (!sbText.toString().substring(iIndex, iIndex+1).contains(" ")) {
				iIndex++;
				continue;
			}
		
			sbText.insert(iIndex+1, '\n');
			iIndex += iOffSet;
		}
		
		return sbText.toString();
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
