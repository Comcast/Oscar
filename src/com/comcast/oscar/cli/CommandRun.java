package com.comcast.oscar.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.comcast.oscar.ber.BERService;
import com.comcast.oscar.buildbulk.BulkBuild;
import com.comcast.oscar.cli.commands.CVC;
import com.comcast.oscar.cli.commands.DigitmapDisplay;
import com.comcast.oscar.cli.commands.DigitmapInsert;
import com.comcast.oscar.cli.commands.DownstreamFrequency;
import com.comcast.oscar.cli.commands.Firmware;
import com.comcast.oscar.cli.commands.FullTLVDisplay;
import com.comcast.oscar.cli.commands.HexDisplay;
import com.comcast.oscar.cli.commands.Input;
import com.comcast.oscar.cli.commands.JSONDisplay;
import com.comcast.oscar.cli.commands.JSONtoTLV;
import com.comcast.oscar.cli.commands.Key;
import com.comcast.oscar.cli.commands.MaxCPE;
import com.comcast.oscar.cli.commands.MIBSCompile;
import com.comcast.oscar.cli.commands.OID;
import com.comcast.oscar.cli.commands.Output;
import com.comcast.oscar.cli.commands.SNMP4JLicense;
import com.comcast.oscar.cli.commands.Specification;
import com.comcast.oscar.cli.commands.TFTPServer;
import com.comcast.oscar.cli.commands.TLV;
import com.comcast.oscar.cli.commands.TLVDescription;
import com.comcast.oscar.cli.commands.TLVtoJSON;
import com.comcast.oscar.configurationfile.CommonTlvInsertions;
import com.comcast.oscar.configurationfile.ConfigrationFileException;
import com.comcast.oscar.configurationfile.ConfigurationFileExport;
import com.comcast.oscar.configurationfile.ConfigurationFileImport;
import com.comcast.oscar.configurationfile.ConfigurationFile;
import com.comcast.oscar.constants.Constants;
import com.comcast.oscar.snmp4j.smi.SMIManagerService;
import com.comcast.oscar.tlv.TlvBuilder;
import com.comcast.oscar.tlv.TlvException;
import com.comcast.oscar.utilities.DirectoryStructure;
import com.comcast.oscar.utilities.HexString;

/**
 * 
 * @author Allen Flickinger (allen.flickinger@gmail.com)
 * 
@bannerLicense
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

public class CommandRun {
		
	private int iBulkBuild = BulkBuild.TEXT_OUTPUT;
	private File fSnmp4JLicence;
	private boolean boolDecompileDisplay = ConfigurationFileExport.EXPORT_FOUND_TLV;
			
	private DigitmapDisplay comDigitmapDisplay = new DigitmapDisplay();
	private FullTLVDisplay comFullTLVDisplay = new FullTLVDisplay();
	private JSONDisplay comJSONDisplay = new JSONDisplay();
	private Key comKey = new Key();
	private Specification comSpecification = new Specification();
	
	private CVC comCVC;
	private DigitmapInsert comDigitmapInsert;
	private DownstreamFrequency comDownstreamFrequency;
	private Firmware comFirmware;
	private HexDisplay comHexDisplay;
	private Input comInput;
	private JSONtoTLV comJSONtoTLV;
	private MaxCPE comMaxCPE;
	private MIBSCompile comMIBSCompile;
	private OID comOID;
	private Output comOutput;
	private TFTPServer comTFTPServer;
	private TLV comTLV;
	private TLVDescription comTLVDescription;
	private TLVtoJSON comTLVtoJSON;
			    
	/**
	 * Build all usable options with LongOpt, Description and any viable arguments
	 * @param options
	 */
	public static void buildOptions(Options options) 
	{ 
		options.addOption("h","help", false, "View all commands with descriptions.");
		options.addOption("version", false, "Display current version.");
		options.addOption(CVC.OptionParameters());
		options.addOption(DigitmapDisplay.OptionParameters());
		options.addOption(DigitmapInsert.OptionParameters());
		options.addOption(DownstreamFrequency.OptionParameters());
		options.addOption(Firmware.OptionParameters());
		options.addOption(FullTLVDisplay.OptionParameters());
		options.addOption(HexDisplay.OptionParameters());
		options.addOption(Input.OptionParameters());
		options.addOption(JSONDisplay.OptionParameters());
		options.addOption(JSONtoTLV.OptionParameters());
		options.addOption(Key.OptionParameters());
		options.addOption(MaxCPE.OptionParameters());
		options.addOption(MIBSCompile.OptionParameters());
		options.addOption(OID.OptionParameters());
		options.addOption(Output.OptionParameters());
		options.addOption(SNMP4JLicense.OptionParameters());
		options.addOption(Specification.OptionParameters());
		options.addOption(TFTPServer.OptionParameters());
		options.addOption(TLV.OptionParameters());
		options.addOption(TLVDescription.OptionParameters());
		options.addOption(TLVtoJSON.OptionParameters());
		options.addOption("c","compile", false, "Compile text to binary.");
		//options.addOption("d","decompile", false, "Decompile binary to text.");
		
		OptionBuilder.withArgName("v{erbose}");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("decompile");
        OptionBuilder.withDescription("Decompile binary to text. Option v for full TLV display.");
		options.addOption(OptionBuilder.create("d"));
		
		OptionBuilder.withArgName("bin/txt> <input dir> <*output dir");
		OptionBuilder.hasArgs();
		OptionBuilder.hasOptionalArgs();
        OptionBuilder.withValueSeparator(' ');
        OptionBuilder.withLongOpt("bulk");
        OptionBuilder.withDescription("Compile all files to binary from the input directory. Output directory optional.");
		options.addOption(OptionBuilder.create("b"));
	}
	
	/**
	 * Checks all the commands from the user. Order is IMPORTANT. Do not rearrange without full understanding.
	 * @param args
	 */
	public void run(String[] args) 
	{
		BasicParser parser = new BasicParser();
		Options options = new Options();
		buildOptions(options);
		
	    try 
	    {
	        CommandLine line = parser.parse(options, args);
            HelpFormatter hf = new HelpFormatter();
            hf.setWidth(115);
            hf.setLeftPadding(5);
            hf.setDescPadding(5);  
            
	        if (line.hasOption("h")) 
	        {
	        	hf.printHelp(Constants.OSCAR_CLI_USAGE, options);
	        }
	        
	        if (line.hasOption("version")) 
	        {
	        	System.out.println(Constants.APACHE_20_LICENCE_DISCLAIMER);
	        	System.out.println(Constants.OSCAR_VERSION);
	        }
 
	        if (line.hasOption("s4j")) 
	        {
	        	String[] optionValues = line.getOptionValues("s4j");
	        	
	        	if(optionValues.length >= 1) 
	        	{
	        		snmp4JLicenseCommand(new File(optionValues[0]));
	        	} 
	        	else 
	        	{
	    	        System.err.println(Constants.ERR_MIS_PAR);
	                hf.printHelp(Constants.OSCAR_CLI_USAGE, options);
	        	}
            } 
	        else 
	        {
            	this.fSnmp4JLicence = DirectoryStructure.fSnmp4jLicenseFile();
            }
	        
	        if (this.fSnmp4JLicence.exists() && !(HexString.fileToByteArray(this.fSnmp4JLicence).length == 0)) 
	        {
	        	SMIManagerService.SmiManagerStart(this.fSnmp4JLicence);
	        }
	        
	        if (line.hasOption("M")) 
	        {
	        	comMIBSCompile = new MIBSCompile(line.getOptionValues("M"));
	        	comMIBSCompile.mibsCompile();
	        }

	        if (line.hasOption("s")) 
	        {
	        	comSpecification.setValues(line.getOptionValues("s"));
	        	
	        	if (comSpecification.getConfigurationFileType() == -1) 
	        	{
	        		System.err.println(Specification.ERROR);
	        		System.exit(1);
	        	}
	        	
	        	if (comSpecification.getTlvDisassemble() == null) 
	        	{
	        		System.err.println(Specification.ERROR);
	        		System.exit(1);
	        	}
	        }
	        
	        if (line.hasOption("i")) 
	        {
	        	comInput = new Input(line.getOptionValues("i"));
	        	
	        	if(!comInput.hasInput()) 
	        	{
	        		System.err.println(Input.ERROR);
	        		System.exit(1);
	        	}
            }    
	        
	        if (line.hasOption("o")) 
	        {
	        	comOutput = new Output(line.getOptionValues("o"));
	        }	       
	        
	        if (line.hasOption("k")) 
	        {
	        	comKey.setKey(line.getOptionValues("k"));
	        }	 
	        
	        if (line.hasOption("f")) 
	        {
	        	comFirmware = new Firmware(line.getOptionValues("f"));
	        }
	        
	        if (line.hasOption("T")) 
	        {
	        	comTFTPServer = new TFTPServer(line.getOptionValues("T"));
	        	
	        	if(!comTFTPServer.hasAddress()) 
	        	{
	        		System.err.println(TFTPServer.ERROR);
	        		System.exit(1);
	        	}
            }  
	        
	        if (line.hasOption("m")) 
	        {
	        	comMaxCPE = new MaxCPE(line.getOptionValues("m"));
	        }     
	        
	        if (line.hasOption("df")) 
	        {
	        	comDownstreamFrequency = new DownstreamFrequency(line.getOptionValues("df"));
	        }      
	        
	        if (line.hasOption("cvc")) 
	        {
	        	comCVC = new CVC(line.getOptionValues("cvc"));
	        	
	        	if(!comCVC.hasCVC()) 
	        	{
	        		System.err.println(CVC.ERROR);
	        		System.exit(1);
	        	}
            }    
	        
	        if (line.hasOption("t")) 
	        {
	        	comTLV = new TLV(line.getOptionValues("t"));
	        }
	        
	        if (line.hasOption("dm")) 
	        {
	 	        comDigitmapInsert = new DigitmapInsert(line.getOptionValues("dm"));
	 	        
	 	        if(!comDigitmapInsert.hasDigitmap()) 
	 	        {
	 	        	System.err.println(DigitmapInsert.ERROR);
	        		System.exit(1);
	 	        }
            }  
	        
	        if (line.hasOption("O")) 
	        {
	        	comOID = new OID(line.getOptionValues("O"));
	        	
	 	        if(!comOID.hasOID()) 
	 	        {
	 	        	System.err.println(OID.ERROR);
	        		System.exit(1);
	 	        }
            } 
	        
	        if (line.hasOption("d")) 
	        {
	        	if (line.getOptionValues("d") != null) 
	        	{
		        	for (String string : line.getOptionValues("d"))
		        	{
			        	if (string.equalsIgnoreCase("v") || string.equalsIgnoreCase("verbose"))
			        	{
			        		boolDecompileDisplay = ConfigurationFileExport.EXPORT_DEFAULT_TLV;
			        	}
		        	}
	        	}
	        	
	        	decompile();
	        }
	        
	        if (line.hasOption("c")) 
	        {
	        	compile();
	        }
	        
	        if (line.hasOption("b")) 
	        {
	        	String[] optionValues = line.getOptionValues("b");
	        	
	        	if(optionValues.length >= 3) 
	        	{
	        		bulkBuild(optionValues[0]);
	        		bulkBuildCommand(new File(optionValues[1]), new File(optionValues[2]));
	        	} 
	        	else if(optionValues.length >= 2) 
	        	{
	        		bulkBuild(optionValues[0]);
	        		bulkBuildCommand(new File(optionValues[1]));
	        	} 
	        	else 
	        	{
	    	        System.err.println(Constants.ERR_MIS_PAR);
	                hf.printHelp(Constants.OSCAR_CLI_USAGE, options);
	        	}
	        } 
	        
	        if (line.hasOption("ftd")) 
	        {
	        	comFullTLVDisplay.printFullTLVDisplay(comSpecification.getConfigurationFileType());
	        }  
	        
	        if (line.hasOption("x")) 
	        {
	        	comHexDisplay = new HexDisplay(line.getOptionValues("x"));
	        	
	        	if (comInput.hasInput()) 
	        	{
	        		if (comInput.isBinary()) 
	        		{
	        			comHexDisplay.printHexDisplayFromBinary(comInput.getInput());
	        		} 
	        		else 
	        		{
	        			comHexDisplay.printHexDisplayFromText(comInput.getInput(), comSpecification.getConfigurationFileType());
	        		}
	        	}
        	}
	        
	        if (line.hasOption("j")) 
	        {
	        	if (comInput.hasInput()) 
	        	{
	        		if (comInput.isBinary()) 
	        		{
	        			comJSONDisplay.printJSONDisplayFromBinary(comInput.getInput(), comSpecification.getTlvDisassemble(), comSpecification.getConfigurationFileType());
	        		} 
	        		else 
	        		{
	        			comJSONDisplay.printJSONDisplayFromText(comInput.getInput(), comSpecification.getTlvDisassemble());
	        		}
	        	}
        	}
	        
	        if (line.hasOption("ddm")) 
	        {
	        	if (comInput.hasInput()) 
	        	{
	        		if (comInput.isBinary()) 
	        		{
	        			comDigitmapDisplay.printDigitmapDisplayFromBinary(comInput.getInput());
	        		} 
	        		else 
	        		{
	        			comDigitmapDisplay.printDigitmapDisplayFromText(comInput.getInput());
	        		}
	        	}
	        }
	        
	        if (line.hasOption("j2t")) 
	        {
	        	comJSONtoTLV = new JSONtoTLV(line.getOptionValues("j2t"));
	        	
	 	        if(comJSONtoTLV.fileExists()) 
	 	        {
	 	        	comJSONtoTLV.printTLV();
	 	        } 
	 	        else 
	 	        {
	 	        	System.err.println(JSONtoTLV.ERROR);
	 	        }
            }
	        
	        if (line.hasOption("t2j")) 
	        {
	        	comTLVtoJSON = new TLVtoJSON(line.getOptionValues("t2j"));
	        	comTLVtoJSON.printJSON(comSpecification.getTlvDisassemble());
            }
	        
	        if (line.hasOption("td")) 
	        {
	        	comTLVDescription = new TLVDescription(line.getOptionValues("td"));
	        	comTLVDescription.printTLVDescription(comSpecification.getConfigurationFileType());
            }    
	    }
	    catch( ParseException exp ) 
	    {
	        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
	    }
	}

	/**
	 * Determines bulkbuild for binary files or text files.
	 * @param string
	 */
	private void bulkBuild(String string) 
	{
		if (string.equalsIgnoreCase("txt") || string.equalsIgnoreCase("t")) 
		{
			this.iBulkBuild = BulkBuild.TEXT_OUTPUT;
		} 
		else if (string.equalsIgnoreCase("bin") || string.equalsIgnoreCase("b")) 
		{
			this.iBulkBuild = BulkBuild.BINARY_OUTPUT;
		}
		
	}

	/**
	 * Set fSnmp4JLicence file
	 * @param file
	 */
	public void snmp4JLicenseCommand(File file) 
	{
		if(file.exists()) 
		{
			this.fSnmp4JLicence = file;
		} 
		else 
		{
			System.err.println("SNMP4J Licence file " + file + " " + Constants.ERR_DNE);
        }
	}
	
	/**
	 * Decompiles the input file
	 */
	public void decompile()  
	{	
		if (comInput != null && comInput.hasInput()) 
		{
			if (comInput.isBinary()) 
			{
				ConfigurationFileExport cfe = new ConfigurationFileExport(comInput.getInput(), comSpecification.getConfigurationFileType());	

				ConfigurationFile cf = null;
							
				try 
				{	
					cf = new ConfigurationFile(comSpecification.getConfigurationFileType(), cfe.getTlvBuilder());
				} 
				catch (TlvException e) 
				{
					Thread.dumpStack();
					e.printStackTrace();
				}
				
				System.out.println(TlvBuilder.tlvDump(cf.toByteArray()));
				
				tlvInsertion(cf);
				
				//System.out.println(TlvBuilder.tlvDump(cf.toByteArray()));
				
				cf.commit();
				
				//System.out.println(TlvBuilder.tlvDump(cf.toByteArray()));
							
				ConfigurationFileExport cfeSnmp64Insert = new ConfigurationFileExport(cf);	
				System.out.println(cfeSnmp64Insert.toPrettyPrint(boolDecompileDisplay));
			} 
			else 
			{
				System.err.println("Input file " + Constants.ERR_NOT_BIN);
			}
		} 
		else 
		{
			System.err.println(Input.ERROR);
		}
	}
	
	/**
	 * Compiles the input file into the specified output
	
	 * @throws ConfigrationFileException  */
	public void compile() 
	{
		if (comInput !=null && comInput.hasInput()) 
		{	
			ConfigurationFile cf = null;
			
			if(comInput.isBinary()) 
			{		
				ConfigurationFileExport cfe = new ConfigurationFileExport(comInput.getInput(),comSpecification.getConfigurationFileType());
				
				try 
				{
					cf = new ConfigurationFile(cfe.getConfigurationFileType(), cfe.getTlvBuilder(), comKey.getKey());
				} 
				catch (TlvException e) 
				{
					e.printStackTrace();
				}				
			} 
			else 
			{
				ConfigurationFileImport cfi = null;

				try 
				{
					cfi = new ConfigurationFileImport(comInput.getInput());
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
	
				cf = new ConfigurationFile(comSpecification.getConfigurationFileType(), cfi.getTlvBuilder(), comKey.getKey());	
			}	
			
			tlvInsertion(cf);
			
			if (comOutput != null && comOutput.getOutput() != null) 
			{
				cf.setConfigurationFileName(comOutput.getOutput());
				cf.commit();
				cf.writeToDisk();
			} 
			else 
			{
				System.err.println(Output.ERROR);
			}
		} 
		else 
		{
			System.err.println(Input.ERROR);
		}
	}
	
	/**
	 * Bulk build files in directory
	 * @param inputDirectory
	 * @param outputDirectory
	 */
	public void bulkBuildCommand(File inputDirectory, File outputDirectory) 
	{
		if (inputDirectory.isDirectory()) 
		{
			if(outputDirectory.isDirectory()) 
			{
				new BulkBuild(inputDirectory, outputDirectory, this.iBulkBuild, comKey.getKey(), tlvInsertion()).start();
			} 
			else 
			{
				System.err.println("Directory " + outputDirectory + " " + Constants.ERR_DNE);
			}
		} 
		else 
		{
			System.err.println("Directory " + inputDirectory + " " + Constants.ERR_DNE);
		}
	}
	
	/**
	 * Bulk build files in directory
	 * @param inputDirectory
	 */
	public void bulkBuildCommand(File inputDirectory) 
	{
		if (inputDirectory.isDirectory()) 
		{
			new BulkBuild(inputDirectory, DirectoryStructure.fTimestampedDir(), this.iBulkBuild, comKey.getKey(), tlvInsertion()).start();
		} 
		else 
		{
			System.err.println("Directory " + inputDirectory + " " + Constants.ERR_DNE);
		}
	}
	
	/**
	 * Insert TLVs
	 * @param configurationFile ConfigurationFile
	 */
	public void tlvInsertion(ConfigurationFile configurationFile) 
	{
		if (this.comFirmware != null && this.comFirmware.getFirmware() != null) {
			try 
			{
				CommonTlvInsertions.insertFirmwareFileName(this.comFirmware.getFirmware(), configurationFile, false);
			} 
			catch (ConfigrationFileException e) 
			{
				e.printStackTrace();
			}
		}
		
		if (this.comTFTPServer != null) 
		{
			if (this.comTFTPServer.hasIpv4Address()) {
				try 
				{
					CommonTlvInsertions.insertTftpServerAddress(this.comTFTPServer.getIpv4Address(), configurationFile, false);
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
			}
			
			if(this.comTFTPServer.hasIpv6Address())
			{
				try 
				{
					CommonTlvInsertions.insertTftpServerAddress(this.comTFTPServer.getIpv6Address(), configurationFile, false);
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if (this.comMaxCPE != null && this.comMaxCPE.getMaxCPE() != -1)
		{
			try 
			{
				CommonTlvInsertions.insertMaxCPE(this.comMaxCPE.getMaxCPE(), configurationFile, false);
			} 
			catch (ConfigrationFileException e) 
			{
				e.printStackTrace();
			}
		}
		
		if (this.comDownstreamFrequency != null && this.comDownstreamFrequency.getDownstreamFrequency() != -1)
		{
			try 
			{
				CommonTlvInsertions.insertDownstreamFrequency(this.comDownstreamFrequency.getDownstreamFrequency(), configurationFile, false);
			} 
			catch (ConfigrationFileException e) 
			{
				e.printStackTrace();
			}
		}
		
		if (this.comCVC != null) 
		{
			if (this.comCVC.hasCoSigner())
			{
				try 
				{
					CommonTlvInsertions.insertCoSignCVC(this.comCVC.getCoSigner(), configurationFile, false);
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
			}
			if(this.comCVC.hasManufacturer())
			{
				try 
				{
					CommonTlvInsertions.insertManufactureCVC(this.comCVC.getManufacturer(), configurationFile, false);
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if (this.comDigitmapInsert != null) 
		{
			for (Map.Entry<String, byte[]> entry : this.comDigitmapInsert.getDigitmap().entrySet()) 
			{
				try 
				{
					CommonTlvInsertions.insertDigitMap(entry.getValue(), entry.getKey(), configurationFile, false);
				} 
				catch (ConfigrationFileException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if (this.comTLV != null && this.comTLV.getTLV() != null)
		{
			try 
			{
				CommonTlvInsertions.insertTLV(this.comTLV.getTLV(), configurationFile, false);
			} 
			catch (TlvException e) {
				e.printStackTrace();
			}
		}
		
		if (this.comOID != null) 
		{
			for (Map.Entry<String, String[]> entry : this.comOID.getMap().entrySet()) 
			{
				CommonTlvInsertions.insertSnmpOID(entry.getKey(), BERService.berStringDataTypeToByte(entry.getValue()[1]), entry.getValue()[0], configurationFile, false);
			}
		}
	}
	
	/**
	 * Insert TLVs
	 * @return commonTlvInsertions 
	 */
	public CommonTlvInsertions tlvInsertion() 
	{
		CommonTlvInsertions commonTlvInsertions = new CommonTlvInsertions();
		
		if(this.comFirmware != null && this.comFirmware.getFirmware() != null) 
		{
			commonTlvInsertions.insertFirmwareFileName(this.comFirmware.getFirmware(), true);
		}
		
		if(this.comMaxCPE != null && this.comMaxCPE.getMaxCPE() != -1) 
		{
			commonTlvInsertions.insertMaxCPE(this.comMaxCPE.getMaxCPE(), true);
		}
		
		if(this.comTFTPServer != null) 
		{
			if(this.comTFTPServer.hasIpv4Address()) 
			{
				commonTlvInsertions.insertTftpServerAddress(this.comTFTPServer.getIpv4Address(), true);
			}
			
			if(this.comTFTPServer.hasIpv6Address()) 
			{
				commonTlvInsertions.insertTftpServerAddress(this.comTFTPServer.getIpv6Address(), true);
			}
		}
		
		if(this.comDownstreamFrequency != null && this.comDownstreamFrequency.getDownstreamFrequency() != -1) 
		{
			commonTlvInsertions.insertDownstreamFrequency(this.comDownstreamFrequency.getDownstreamFrequency(), false);
		}
		
		if(this.comCVC != null) 
		{
			if(this.comCVC.hasCoSigner()) 
			{
				commonTlvInsertions.insertCoSignCVC(this.comCVC.getCoSigner(), true);
			}
			
			if(this.comCVC.hasManufacturer()) 
			{
				commonTlvInsertions.insertManufactureCVC(this.comCVC.getManufacturer(), true);
			}
		}
		
		if(this.comDigitmapInsert != null) 
		{
			for (Map.Entry<String, byte[]> entry : this.comDigitmapInsert.getDigitmap().entrySet()) commonTlvInsertions.insertDigitMap(entry.getValue(), entry.getKey());
		}
		
		try 
		{
			if(this.comTLV != null && this.comTLV.getTLV() != null)
			{
				commonTlvInsertions.insertTLV(this.comTLV.getTLV(), false);
			}
		} 
		catch (TlvException e) 
		{
			e.printStackTrace();
		}
		
		if(this.comOID != null) 
		{
			for (Map.Entry<String, String[]> entry : this.comOID.getMap().entrySet()) commonTlvInsertions.insertSnmpOID(entry.getKey(), BERService.berStringDataTypeToByte(entry.getValue()[1]), entry.getValue()[0]);
		}
		
		return commonTlvInsertions;
	}
}