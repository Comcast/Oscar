package com.comcast.oscar.utilities;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;

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


 * @author Maurice Garcia (mgarcia01752@outlook.com)
 */

public class Zip {
	
	/**
	 * 
	 * @param fFileName
	 * @param ba
	 * @param fZipFilename File
	 */
	public static void ZipFile(File fZipFilename , File fFileName, byte[] ba) {
		
		ZipFile zf = null; 
		
		//Create ZIP Build Folder and ZIP file
		zf = new ZipFile(fZipFilename);
		
		//Create a InputStream from memory
		InputStream is = new ByteArrayInputStream(ba);
		
		//This would be the name of the file for this entry in the ZIP file
		ZipParameters zp = InitZipParametersFileName(fFileName);
		
		try {
			zf.addStream(is, zp);
		} catch (ZipException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param fZipFileName
	 * @param mfba
	 */
	public static void ZipFile(File fZipFileName , Map<File,byte[]> mfba) {
		
		ZipFile zf = null; 

		//Create ZIP Build Folder and ZIP file
		zf = new ZipFile(new File(fZipFileName + ".zip"));
		
		//Cycle thru all the files and add them to the Zip File
		for(Entry<File, byte[]> eEntry : mfba.entrySet()) {
						
			File fFile = eEntry.getKey();
			
			byte[] baByteArray = eEntry.getValue();
			
			//Create a InputStream from memory
			InputStream is = new ByteArrayInputStream(baByteArray);

			//This would be the name of the file for this entry in the ZIP file
			ZipParameters zp = InitZipParametersFileName(fFile);
			
			try {
				zf.addStream(is, zp);
			} catch (ZipException e) {
				e.printStackTrace();
			}
			
		}
	
	}
	
	/**
	 * 
	 * @param f
	
	 * @return ZipParameters
	 */
	private static ZipParameters InitZipParametersFileName(File f) {
		
		// Initiate Zip Parameters which define various properties such
		// as compression method, etc. More parameters are explained in other
		// examples
		ZipParameters zp = new ZipParameters();
		
		// set compression method to deflate compression
		zp.setCompressionMethod(CompressionMethod.DEFLATE); 
		
		/* 
		 Set the compression level. This value has to be in between 0 to 9
		 Several predefined compression levels are available
		 DEFLATE_LEVEL_FASTEST - Lowest compression level but higher speed of compression
		 DEFLATE_LEVEL_FAST - Low compression level but higher speed of compression
		 DEFLATE_LEVEL_NORMAL - Optimal balance between compression level/speed
		 DEFLATE_LEVEL_MAXIMUM - High compression level with a compromise of speed
		 DEFLATE_LEVEL_ULTRA - Highest compression level but low speed
		*/
		zp.setCompressionLevel(CompressionLevel.NORMAL); 
		
		zp.setRootFolderNameInZip(f.getPath() + File.separator);
		
		zp.setFileNameInZip(f.toString());
		
		return zp;
	}
	

}
