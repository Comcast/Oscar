package com.comcast.oscar.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.comcast.oscar.parser.TlvConfigurationFileParser;
import com.comcast.oscar.parser.tlvLexer;
import com.comcast.oscar.parser.tlvParser;
import com.comcast.oscar.utilities.HexString;

/*
	Copyright 2015 Comcast Cable Communications Management, LLC
	___________________________________________________________________
	Licensed under the Apache License, Version 2.0 (the "License")
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	
	@author Maurice Garcia (maurice.garcia.2015@gmail.com)

*/

/**
 */
public class DocsisLexerTest {

	/**
	 * Method readFile.
	 * @param fileName String
	 * @return String
	 * @throws IOException
	 */
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) { 
	            sb.append(line);
	            sb.append('\n');
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	

	/**
	 * Method main.
	 * @param args String[]
	 */
	public static void main(String[] args) {
		
		DocsisLexerTest mRead = new DocsisLexerTest();
		
		String config = null;
		try {
			config = mRead.readFile("DocsisConfig.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create a CharStream that reads from standard input
		ANTLRInputStream input = new ANTLRInputStream(config); 

		// create a lexer that feeds off of input CharStream
		tlvLexer lexer = new tlvLexer(input); 

		// create a buffer of tokens pulled from the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		tlvParser parser = new tlvParser(tokens);
		
		ParseTree tree = parser.begin(); // begin parsing at init rule
		
		System.out.println(tree.toStringTree(parser)); // print LISP-style tree	
		
		System.out.println("+-------------------------------------------------------------------------------------------------------------+");
		
		ParseTreeWalker walker = new ParseTreeWalker();
		
		TlvConfigurationFileParser tcfpTlvConfig = new TlvConfigurationFileParser();
		
		walker.walk(tcfpTlvConfig, tree);
				
		System.out.println(tree.toStringTree(parser)); // print LISP-style tree	
		
		System.out.println("DocsisLexerTest() -> " + tcfpTlvConfig.toString());
		
		System.out.println("DocsisLexerTest(HexString) -> " + new HexString(tcfpTlvConfig.toByteArray()).toString(":"));
				
	}

}
