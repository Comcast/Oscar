package com.comcast.oscar.buildbulk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;


public class MergeBulkBuild {
	
	private ArrayList<File> alf = new ArrayList<File>();
	
	private ArrayList<ConfigurationFile> alcf1;
	private ArrayList<ConfigurationFile> alcf2;
	private ArrayList<ConfigurationFile> alcfTemp;
	
	public String NOMENCLATURE_SEPERATOR = "_";
	
	/**/
	public MergeBulkBuild() {}
	
	/**
	 * 
	 * @param fInputDirectory
	 */
	public void addInputDirectory(File fInputDirectory) {
		alf.add(fInputDirectory);
	}
	
	/**
	 * 
	 * @param fDirectory1
	 * @param fDirectory2
	 */
	private void mergerDirectories(File fDirectory1 , File fDirectory2) {
		
		for (File fConfigurationFile1 : fDirectory1.listFiles()) {
			System.out.println(fConfigurationFile1.getAbsolutePath());
			
			for (File fConfigurationFile2 : fDirectory2.listFiles()) {
				System.out.println(fConfigurationFile2.getAbsolutePath());
				
				
				
			}
			
		}
		
	}
	
	/**
	 * 
	 * @return List<File>
	 */
	public List<ConfigurationFile> getInputConfigurationFiles(File fInputDirectory) {

		List<ConfigurationFile> lfConfigurationFile = new ArrayList<ConfigurationFile>();

		for (final File fConfigurationFile : fInputDirectory.listFiles()) {
						
			byte[] bConfigurationFile = HexString.fileToByteArray(fConfigurationFile);
			
			if (HexString.verifyAsciiPlainText(bConfigurationFile)) {
				
				ConfigurationFileImport cfi = null;
				
				try {
					cfi = new ConfigurationFileImport(bConfigurationFile);
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (ConfigurationFileException e) {
					e.printStackTrace();
				}
				
				ConfigurationFile cf = enw ConfigurationFile()
				
			} else {
				
				TlvBuilder tb = new TlvBuilder();
				
				try {
					tb.add(new HexString(bConfigurationFile));
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				ltlvConfigurationFileInsert.add(tb);
				
			}
		}
		
		return lfConfigurationFile;
	}
	
}
