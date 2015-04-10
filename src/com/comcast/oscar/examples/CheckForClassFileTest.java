package com.comcast.oscar.examples;

/**
 */
public class CheckForClassFileTest {

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
	// TODO Auto-generated method stub
	
		try {
			Class.forName("com.agentpp.smiparser.SMI");
			System.out.println("SMI Found Class");
		} catch (ClassNotFoundException e) {
			System.out.println("SMI Not Found Class");
		}
		
	}

}
