package com.comcast.oscar.interpreter;

import com.comcast.oscar.cli.CommandRun;
import com.comcast.oscar.constants.Constants;

/**
 */
public class Interpreter {

	public final static String PROMPT = "?> ";
	public final static String END_OF_LINE = ";";
	
	private CommandRun cmds;
	
	private Scanner scanIn;
	
	private List<String> lsUserHistory = new ArrayList<String>();
	
	private final static int SESSION_STATUS_START 	= 1;
	private final static int SESSION_STATUS_STOP 	= 0;
	
	/* Check to make sure there is only 1 Interpreter started */
	private static int iSessionStatus = SESSION_STATUS_STOP;

	/**
	 * 
	 */
	public Interpreter() {		
		cmds = new CommandRun();
	}
	
	/**
	 * 
	 */
	public void start() {
		
		if (iSessionStatus == SESSION_STATUS_STOP) {
			
			iSessionStatus = SESSION_STATUS_START;

			scanIn = new Scanner(System.in);

			System.out.println("\n\n" + Constants.APACHE_20_LICENCE_DISCLAIMER + "\n\n");
			
			String[] saUserInput = null;

			while(true) {

				System.out.print(PROMPT);

				String sUserInput = scanIn.nextLine();

				if (sUserInput.contains("history")) {
					printUserHistory();
					continue;
				} else if (sUserInput.contains("clear")) {
					clearHistory();
					continue;	
				} else if (sUserInput.contains("run")) {
					saUserInput = sUserInput.split("\\s+");
					saUserInput = runCommand(Integer.parseInt(saUserInput[1]));					
				}
				
				lsUserHistory.add(sUserInput);

				saUserInput = sUserInput.split("\\s+");
				
				cmds.run(saUserInput);

			}
		}
		
	}
	
	/**
	 * 
	 */
	public void printUserHistory() {
		
		int iLineCount = 0;
		
		System.out.println("\nLine\t| Command");
		System.out.println("------------------------------------------------------------------------");
		for (String sUserHistory : lsUserHistory) {
			System.out.println(++iLineCount + "\t| " + sUserHistory);
		}
	}
	
	/**
	 * 
	 */
	public void clearHistory() {
		lsUserHistory.clear();
	}

	/**
	 * need to add code
	 * @param iLineNumber
	 */
	public String[] runCommand(int iLineNumber) {
		
		//Add code to run command from history
		//Will need to add some exception checking in case command is not there
		
		return null;
		
	}

	
}
