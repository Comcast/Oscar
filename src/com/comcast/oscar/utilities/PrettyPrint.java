package com.comcast.oscar.utilities;

public class PrettyPrint {

	public static String START_INDENT_TOKEN = "{";
	public static String END_INDENT_TOKEN = "}";
	public static String NEW_LINE_TOKEN = ";";
	
	private StringBuilder sbInputCode ;
	private StringBuilder sbOutputCode ;
	private int iIndentCurrentDepth = 0;
	private int iIndexPointer = 0;
	
	/**
	 * 
	 * @param sbCode
	 */
	public PrettyPrint(StringBuilder sbInputCode) {
		this.sbInputCode = sbInputCode;
		sbOutputCode = new StringBuilder();
		start();
	}
	
	/**
	 * 
	 */
	public String toString() {
		return sbOutputCode.toString();
	}
	
	/**
	 * 
	 */
	private void start() {
		
		while(iIndexPointer < sbInputCode.length()) {
			
			String sToken = sbInputCode.charAt(iIndexPointer);
			
			if (START_INDENT_TOKEN) {
				
				sbOutputCode.append(cToken);
				
				sbOutputCode.append("\n");
				
				iIndentCurrentDepth++;
								
			} else if (END_INDENT_TOKEN.equals(cToken)) {
							
				sbOutputCode.append(indentation(iIndentCurrentDepth));
				
				sbOutputCode.append(cToken);
				
				sbOutputCode.append("\n");
				
				iIndentCurrentDepth--;
				
			} else if (NEW_LINE_TOKEN.equals(cToken)) {
				
				sbOutputCode.append(cToken);
				
				sbOutputCode.append("\n");

			} if (NEW_LINE_TOKEN.equals("\n")) {
				

			} else {
				sbOutputCode.append(indentation(iIndentCurrentDepth));
				sbOutputCode.append(cToken);
			}
			
			iIndexPointer++;
			
		}
		
	}
	
	/**
	 * 
	 * @param iNumberOfTabs
	 * @return
	 */
	private String indentation(int iNumberOfTabs) {
		
		String sIndentation = "";
		
		for (int iCounter = 0 ; iCounter < iNumberOfTabs; iCounter++) {
			System.out.println("TAB");
			sIndentation = sIndentation + "\t";
		}
		
		return sIndentation;
		
	}
	
}
