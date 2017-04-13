package com.comcast.oscar.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class Disk {
	//Log4J2 logging
    private static final Logger logger = LogManager.getLogger(Disk.class);
	
	private static boolean debug = Boolean.FALSE;
	
	/**
	 * 
	 * @param bStream
	 * @param fOutput
	
	 * @return boolean
	 */
	public static boolean writeToDisk(byte[] bStream , File fOutput) {
		
		boolean localDebug = Boolean.FALSE;
		
		if (bStream == null) {

			if (debug|localDebug) {
				logger.debug("Disk.writeToDisk() - NULL ByteArray");
			}
			
			return false;
		}
		
		if (debug|localDebug) {
			logger.debug("Disk.writeToDisk() " +
									" - Total Byte Count: " + bStream.length +
									" - FileName: " + fOutput.getName());
		}
		
		OutputStream out = null;
		
		if (fOutput.getName() != null) {
			
			try {
				out = new FileOutputStream(fOutput);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else if (fOutput.getName().length() != 0) {
			
			try {

				out = new FileOutputStream(fOutput.getName());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			
			File fCf = null;
			
			fCf = fOutput;
			
			try {
				out = new FileOutputStream(fCf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
				
		try {
			out.write(bStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		    	
		return true;
	}
	
	/**
	 * 
	
	 * @param fOutput
	
	 * @param sInput String
	 * @return boolean
	 */
	public static boolean writeToDisk(String sInput , File fOutput) {
		
		boolean localDebug = Boolean.FALSE;
		
		HexString hsInput = new HexString();
		
		hsInput.add(sInput);
		
		byte[] baInput = hsInput.toByteArray();
		
		if (debug|localDebug) {
			logger.debug("Disk.writeToDisk() " +
									" - Total Byte Count: " + baInput.length +
									" - FileName: " + fOutput);
		}
		
		OutputStream out = null;
		
		if (fOutput.getName() != null) {
			
			try {
				out = new FileOutputStream(fOutput);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		} else if (fOutput.getName().length() != 0) {
			
			try {

				out = new FileOutputStream(fOutput.getName());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			
			File fCf = null;
			
			fCf = fOutput;
			
			try {
				out = new FileOutputStream(fCf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
				
		try {
			out.write(baInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		    	
		return true;
	}

}
