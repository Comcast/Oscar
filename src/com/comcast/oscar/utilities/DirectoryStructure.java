package com.comcast.oscar.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 * 
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

 */


public class DirectoryStructure {
	
	private static boolean fromJar = false;
	
	/**
	 * Determine if launched from Jar file. For testing purposes. Set in Main
	 */
	public static void setFromJar() 
	{
		fromJar = true;
	}
	
	/**
	 * Decide the correct path to start from
	
	 * @return base directory */
	public static String sBasePath() 
	{
		if(fromJar) 
		{
			File file = new File(System.getProperty("java.class.path"));
			if (file.getParent() != null)
			{
				return file.getParent().toString();
			}
		}
		
		try 
		{
			return new java.io.File("").getCanonicalPath();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Find active path. Used for Input
	
	 * @return active directory */
	public static String sActivePath() 
	{
		String activePath = null;
		
		try 
		{
			activePath = new java.io.File("").getCanonicalPath();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return activePath;
	}	
	
	/**
	 * Directory for certificates
	
	 * @return certificates subdirectory */
	public static File fCertificatesDir() 
	{
		return new File(sBasePath() + File.separator + "certificates");
	}
	
	/**
	 * Directory for bulk builds
	
	 * @return build subdirectory */
	public static File fBuildDir() 
	{
		return new File(sBasePath() + File.separator + "build");
	}
	
	/**
	 * Directory for databases
	
	 * @return database subdirectory */
	public static File fDbDir() 
	{
		return new File(sBasePath() + File.separator + "db");
	}
	
	/**
	 * Directory for licenses
	
	 * @return licenses subdirectory */
	public static File fLicensesDir() 
	{
		return new File(sBasePath() + File.separator + "licenses");
	}
	
	/**
	 * Directory for mibs
	
	 * @return mibs subdirectory */
	public static File fMibsDir() 
	{
		return new File(sBasePath() + File.separator + "mibs");	
	}
	
	/**
	 * Directory for mib binaries
	
	 * @return bin subdirectory in mibs subdirectory */
	public static File fMibsBinaryDir() 
	{
		return new File(fMibsDir() + File.separator + "bin");	
	}
	
	/**
	 * Directory for mib text
	
	 * @return txt subdirectory in mibs subdirectory */
	public static File fMibsTextDir() 
	{
		return new File(fMibsDir() + File.separator + "txt");	
	}
	
	/**
	 * SQLite database file 
	
	 * @return dictionary file */
	public static File fDictionaryFile() 
	{
		return new File(fDbDir() + File.separator + "dictionary.sqlite");	
	}
	
	/**
	 * 
	
	 * @return timestamped directory */
	public static File fTimestampedDir() 
	{
		return new File(File.separator + getDate());
	}
	
	/**
	 * Export the dictionary.sqlite file from the jar
	 */
	public void exportDictionary() 
	{	
		InputStream is = this.getClass().getResourceAsStream("/dictionary.sqlite");		
		OutputStream os = null;

		try
		{
			os = new FileOutputStream(fDictionaryFile());
			byte[] buffer = new byte[1024];
			int length;
			
			//copy the file content in bytes 
			while ((length = is.read(buffer)) > 0) 
			{
				os.write(buffer, 0, length);
			}

			is.close();
			os.close();

			System.out.println("/* Dictionary export successful! */");
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	
	 * @return Date in yyyMMdd_HHmmss format */
	public static String getDate() 
	{
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String formatted = format.format(date);	
		return formatted;
	}
	
	/**
	 * Create subdirectories if they do not exist
	 */
	public static void createDirectories() 
	{
		if(!fCertificatesDir().exists()) 
		{
			fCertificatesDir().mkdir();
		}
		
		if(!fDbDir().exists()) 
		{
			fDbDir().mkdir();
		}
		
		if(!fMibsDir().exists())
		{
			fMibsDir().mkdir();
		}
		
		if(!fMibsBinaryDir().exists()) 
		{
			fMibsBinaryDir().mkdir();
		}
		
		if(!fMibsTextDir().exists()) 
		{
			fMibsTextDir().mkdir();
		}
	}
	
	/**
	 * Export files if they do not exist
	 */
	public void exportFiles() 
	{
		if(!fDictionaryFile().exists())
		{
			exportDictionary();
		}
	}
}
