package com.comcast.oscar.buildbulk;

import java.io.File;
import java.util.ArrayList;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.test.TestDirectoryStructure;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;


public class MergeBulkBuild {
	
	private ArrayList<File> alfInputDirectory = new ArrayList<File>();
	
	private ArrayList<ArrayList<ConfigurationFile>> alalcf = new ArrayList<ArrayList<ConfigurationFile>>();
	
	public String NOMENCLATURE_SEPERATOR = "_";
	
	private int iConfigurationFileType = -1;
	private String sSharedSecret = "SHAREDSECRET";
	
	private boolean debug = Boolean.FALSE;
	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param sSharedSecret
	 */
	public MergeBulkBuild(int iConfigurationFileType,String sSharedSecret) {
		this.iConfigurationFileType = iConfigurationFileType;
		this.sSharedSecret = sSharedSecret;
	}
	
	/**
	 * 
	 * @param iConfigurationFileType
	 */
	public MergeBulkBuild(int iConfigurationFileType) {
		this.iConfigurationFileType = iConfigurationFileType;
	}
	
	/**
	 * 
	 * @param fInputDirectory
	 */
	public void addInputDirectory(File fInputDirectory) {
		alfInputDirectory.add(fInputDirectory);
	}
	
	/**
	 * 
	 */
	public void start() {
		
		boolean localDebug = Boolean.TRUE;
		
		alalcf.clear();
		
		for (File fInputDirectory:alfInputDirectory) {			
			alalcf.add(getInputConfigurationFiles(fInputDirectory));	
		}
		
		ArrayList<ConfigurationFile> alcfTemp = new ArrayList<ConfigurationFile>();
		
		/*Load the first part of the configuration*/
		alcfTemp.addAll(alalcf.get(0));

		if(localDebug)
			System.out.println("MergeBulkBuild.start() -> Files: " + alcfTemp);

		
		for (int iIndex = 1; iIndex < alalcf.size(); iIndex++) {
			alcfTemp = mergerDirectories(alcfTemp,alalcf.get(iIndex));
		}
		
		if(localDebug)
			System.out.println("MergeBulkBuild.start() -> Number of Configurations: " + alcfTemp.size());

		if(localDebug)
			System.out.println("MergeBulkBuild.start() -> Number of Configurations: " + alcfTemp.toString());

		for(ConfigurationFile cf:alcfTemp) {
			
			cf.commit();
						
			ConfigurationFileExport cfe = new ConfigurationFileExport(cf);

			cfe.writeToDisk(new File(TestDirectoryStructure.outputDir() + File.separator + cf.getConfigurationFileName()));
			
		}
		
	}
	
	/**
	 * 
	 * @param fDirectory1
	 * @param fDirectory2
	 */
	private ArrayList<ConfigurationFile> mergerDirectories(ArrayList<ConfigurationFile> alcf1, ArrayList<ConfigurationFile> alcf2) {
		
		boolean localDebug = Boolean.TRUE;
		
		ArrayList<ConfigurationFile> alcf = new ArrayList<ConfigurationFile>();
		
		for (ConfigurationFile cf1 : alcf1) {
			
			for (ConfigurationFile cf2 : alcf2) {

				TlvBuilder tb = new TlvBuilder();
				
				tb.add(cf1.toTlvBuilder());
				tb.add(cf2.toTlvBuilder());

				String sMergeFileName = 
						TrimFileExtention(cf1.getConfigurationFileName()) + 
						NOMENCLATURE_SEPERATOR + 
						TrimFileExtention(cf2.getConfigurationFileName());
				
				if (localDebug|debug)
					System.out.println("mergerDirectories()" + sMergeFileName);
				
				ConfigurationFile cf = new ConfigurationFile(sMergeFileName, iConfigurationFileType, tb, sSharedSecret);
				
				alcf.add(cf);
				
			}
			
		}
		
		return alcf;
		
	}
	
	/**
	 * 
	 * @return List<File>
	 */
	public ArrayList<ConfigurationFile> getInputConfigurationFiles(File fInputDirectory) {

		System.out.println("getInputConfigurationFiles() " + fInputDirectory);
		
		ArrayList<ConfigurationFile> alfConfigurationFile = new ArrayList<ConfigurationFile>();

		for (File fConfigurationFile : fInputDirectory.listFiles()) {
			
			System.out.println("getInputConfigurationFiles() " + fConfigurationFile);
			
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
				
				ConfigurationFile cf = new ConfigurationFile(iConfigurationFileType,cfi.getTlvBuilder());				
				
				cf.removeAllSecurityHash();
				
				cf.setConfigurationFileName(fConfigurationFile.getName());
				
				alfConfigurationFile.add(cf);
				
			} else {
				
				TlvBuilder tb = new TlvBuilder();
				
				try {
					tb.add(new HexString(bConfigurationFile));
				} catch (TlvException e) {
					e.printStackTrace();
				}
				
				ConfigurationFile cf = new ConfigurationFile(iConfigurationFileType,tb);
				
				cf.removeAllSecurityHash();
				
				cf.setConfigurationFileName(fConfigurationFile.getName());
									
				alfConfigurationFile.add(cf);
				
			}
		}
		
		return alfConfigurationFile;
	}
	
	private static String TrimFileExtention(String sFilename) {
		return sFilename.replaceAll("(\\..*)", "");
	}
	
}