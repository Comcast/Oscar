package com.comcast.oscar.buildbulk;

import java.io.File;
import java.util.ArrayList;

import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.configurationfile.ConfigurationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.HexString;

public class MergeBulkBuild {


	public String NOMENCLATURE_SEPERATOR = "";
	public static final Boolean BINARY_FILE_OUTPUT = true;
	public static final Boolean TEXTUAL_FILE_OUTPUT = false;
	
	private ArrayList<File> alfInputDirectory = new ArrayList<File>();
	private ArrayList<ArrayList<ConfigurationFile>> alalcf = new ArrayList<ArrayList<ConfigurationFile>>();
	private int iConfigurationFileType = -1;
	private String sSharedSecret = "SHAREDSECRET";
	private File fOutputDir;
	private boolean fOutputType;
	
	private boolean debug = Boolean.FALSE;
	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param fOutputDir
	 * @param fOutputType
	 * @param sSharedSecret
	 */
	public MergeBulkBuild(int iConfigurationFileType,File fOutputDir, boolean fOutputType , String sSharedSecret) {
		this.iConfigurationFileType = iConfigurationFileType;
		this.sSharedSecret = sSharedSecret;
		this.fOutputDir = fOutputDir;
		this.fOutputType = fOutputType;
	}
	
	/**
	 * 
	 * @param iConfigurationFileType
	 * @param fOutputDir
	 * @param fOutputType
	 */
	public MergeBulkBuild(int iConfigurationFileType,File fOutputDir, boolean fOutputType) {
		this.iConfigurationFileType = iConfigurationFileType;
		this.fOutputDir = fOutputDir;
		this.fOutputType = fOutputType;
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
		
		boolean localDebug = Boolean.FALSE;
		
		alalcf.clear();
		
		for (File fInputDirectory:alfInputDirectory) {			
			alalcf.add(getInputConfigurationFiles(fInputDirectory));	
		}
		
		ArrayList<ConfigurationFile> alcfTemp = new ArrayList<ConfigurationFile>();
		
		/*Load the first part of the configuration*/
		alcfTemp.addAll(alalcf.get(0));

		if(localDebug|debug)
			System.out.println("MergeBulkBuild.start() -> Files: " + alcfTemp);

		
		for (int iIndex = 1; iIndex < alalcf.size(); iIndex++) {
			try {
				alcfTemp = mergerDirectories(alcfTemp,alalcf.get(iIndex));
			} catch (MergeBulkBuildException e) {
				System.err.println(e.getMessage());
				System.err.println(e.getLocalizedMessage());
			}
		}
		
		if(localDebug|debug)
			System.out.println("MergeBulkBuild.start() -> Number of Configurations: " + alcfTemp.size());

		if(localDebug|debug)
			System.out.println("MergeBulkBuild.start() -> Number of Configurations: " + alcfTemp.toString());

		for(ConfigurationFile cf:alcfTemp) {
			
			cf.commit();
			
			/*Binary*/
			if (fOutputType) {
				cf.setConfigurationFileName(fOutputDir+File.separator+cf.getConfigurationFileName());
				cf.writeToDisk();
			/*Text*/
			} else {
				ConfigurationFileExport cfe = new ConfigurationFileExport(cf);
				cfe.writeToDisk(new File(fOutputDir+File.separator+cf.getConfigurationFileName()));
			}
				
		}
		
	}
	
	/**
	 * 
	 * @param fDirectory1
	 * @param fDirectory2
	 * @throws MergeBulkBuildException 
	 */
	private ArrayList<ConfigurationFile> mergerDirectories(ArrayList<ConfigurationFile> alcf1, ArrayList<ConfigurationFile> alcf2) throws MergeBulkBuildException {
		
		boolean localDebug = Boolean.FALSE;
		
		ArrayList<ConfigurationFile> alcf = new ArrayList<ConfigurationFile>();
		
		for (ConfigurationFile cf1 : alcf1) {
			
			for (ConfigurationFile cf2 : alcf2) {
				
				/*CHECK TO SEE IF THEY ARE OF THE SAME CONFIGURATION FILE TYPE */
				if (cf1.getConfigurationFileType() != cf2.getConfigurationFileType()) {
					throw new MergeBulkBuildException(	"/* " + 
														cf1.getConfigurationFileName() + 
														" is not the same configuration file type as " +
														cf2.getConfigurationFileName() +
														" */");
				}
			
				TlvBuilder tb = new TlvBuilder();
				
				/* Merger both Configurations */
				tb.add(cf1.toTlvBuilder());
				tb.add(cf2.toTlvBuilder());

				/* Merger both Configurations filenames */
				String sMergeFileName = 
						TrimFileExtention(cf1.getConfigurationFileName()) + 
						NOMENCLATURE_SEPERATOR + 
						TrimFileExtention(cf2.getConfigurationFileName());
				
				if (localDebug|debug)
					System.out.println("MergeBulkBuild.mergerDirectories() -> " + sMergeFileName);

				/* Add Configuration file to list */
				alcf.add(new ConfigurationFile(sMergeFileName, iConfigurationFileType, tb, sSharedSecret));
				
			}
			
		}
		
		return alcf;
		
	}
	
	/**
	 * 
	 * @return List<File>
	 */
	public ArrayList<ConfigurationFile> getInputConfigurationFiles(File fInputDirectory) {

		boolean localDebug = Boolean.FALSE;
		
		ArrayList<ConfigurationFile> alfConfigurationFile = new ArrayList<ConfigurationFile>();

		for (File fConfigurationFile : fInputDirectory.listFiles()) {
			
			if (localDebug|debug)
				System.out.println("MergeBulkBuild.getInputConfigurationFiles() " + fConfigurationFile);
			
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
	
	/**
	 * 
	 * @param sFilename File.txt -> File
	 * @return
	 */
	private static String TrimFileExtention(String sFilename) {
		return sFilename.replaceAll("(\\..*)", "");
	}
	
}
